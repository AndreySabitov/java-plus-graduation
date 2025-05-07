package ru.practicum.ewm.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserActionHandler {
    private final Map<Long, Map<Long, Float>> eventActions = new HashMap<>(); // хранит действия всех пользователей с событиями (ключ)
    private final Map<Long, Float> eventWeights = new HashMap<>(); // хранит общий вес для конкретного события
    private final Map<Long, Map<Long, Float>> minWeightsSum = new HashMap<>(); // значения числителя

    public List<EventSimilarityAvro> calcSimilarity(UserActionAvro action) {
        List<EventSimilarityAvro> similarity = new ArrayList<>();
        Long actionEventId = action.getEventId();
        Long actionUserId = action.getUserId();

        Float delta = updateEventAction(action);

        if (delta > 0) {
            if (!eventWeights.containsKey(actionEventId)) {
                eventWeights.put(actionEventId, delta);
            } else {
                eventWeights.compute(actionEventId, (k, oldWeight) -> oldWeight + delta);
            }
        }

        Set<Long> anotherEvents = eventActions.keySet().stream() // получили события с которыми были действия
                .filter(id -> !Objects.equals(id, actionEventId))
                .collect(Collectors.toSet());

        log.info("получаем коэффициенты схожести для событий {}", anotherEvents);

        anotherEvents.forEach(ae -> {
            // расчёт схожести для actionEvent и ae
            //добавляем EventSimilarityAvro в список
            float sim = (float) (getMinWeightsSum(actionEventId, ae, delta, actionUserId) /
                    (Math.sqrt(eventWeights.get(actionEventId)) * Math.sqrt(eventWeights.get(ae))));

            similarity.add(EventSimilarityAvro.newBuilder()
                    .setEventA(Math.min(actionEventId, ae))
                    .setEventB(Math.max(actionEventId, ae))
                    .setScore(sim)
                    .setTimestamp(Instant.now())
                    .build());
        });

        return similarity;
    }

    private Float updateEventAction(UserActionAvro action) {
        Long eventId = action.getEventId();
        Long userId = action.getUserId();
        Float newWeight = mapActionToWeight(action.getActionType());

        if (!eventActions.containsKey(eventId)) {
            Map<Long, Float> userActionsWeight = new HashMap<>();
            userActionsWeight.put(userId, newWeight);
            eventActions.put(eventId, userActionsWeight);
            return newWeight;
        } else {
            Map<Long, Float> userActionsWeight = eventActions.get(eventId);
            if (!userActionsWeight.containsKey(userId)) {
                userActionsWeight.put(userId, newWeight);
                eventActions.put(eventId, userActionsWeight);
                return newWeight;
            } else {
                Float oldWeight = userActionsWeight.get(userId);
                if (newWeight > oldWeight) {
                    userActionsWeight.put(userId, newWeight);
                    eventActions.put(eventId, userActionsWeight);
                    return newWeight - oldWeight;
                }
                return 0f;
            }
        }
    }

    private Float getMinWeightsSum(Long eventA, Long eventB, Float delta, Long userId) {
        if (!minWeightsSum.containsKey(eventA)) {
            Float weight = calcMinWeightsSum(eventA, eventB);
            Map<Long, Float> map = new HashMap<>();
            map.put(eventB, weight);
            minWeightsSum.put(eventA, map);
            return weight;
        } else {
            Map<Long, Float> innerMap = minWeightsSum.get(eventA);
            if (!innerMap.containsKey(eventB)) {
                Float weight = calcMinWeightsSum(eventA, eventB);
                innerMap.put(eventB, weight);
                minWeightsSum.put(eventA, innerMap);
                return weight;
            } else {
                Float oldWeight = minWeightsSum.get(eventA).get(eventB);
                if (delta == 0) {
                    return oldWeight;
                }
                Float weightA = eventActions.get(eventA).get(userId);
                Float weightB = eventActions.get(eventB).get(userId);
                if (weightB == null) {
                    return oldWeight;
                }
                float newWeight;
                if (weightA > weightB && (weightA - delta) < weightB) {
                    newWeight = oldWeight + (weightB - (weightA - delta));
                } else {
                    newWeight = oldWeight + weightA;
                }
                minWeightsSum.get(eventA).put(eventB, newWeight);
                return newWeight;
            }
        }
    }

    private Float calcMinWeightsSum(Long eventA, Long eventB) {
        List<Float> weights = new ArrayList<>();

        Map<Long, Float> userActionsA = eventActions.get(eventA);
        Map<Long, Float> userActionsB = eventActions.get(eventB);

        userActionsA.forEach((aUser, aWeight) -> {
            if (userActionsB.containsKey(aUser)) {
                weights.add(Math.min(aWeight, userActionsB.get(aUser)));
            }
        });

        if (weights.isEmpty()) {
            return 0f;
        }
        return weights.stream()
                .collect(Collectors.summingDouble(Float::floatValue))
                .floatValue();
    }

    private Float mapActionToWeight(ActionTypeAvro type) {
        return switch (type) {
            case VIEW -> 0.4f;
            case REGISTER -> 0.8f;
            case LIKE -> 1f;
        };
    }
}
