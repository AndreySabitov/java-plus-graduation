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
    private final Map<Long, Map<Long, Double>> eventActions = new HashMap<>();
    private final Map<Long, Double> eventWeights = new HashMap<>();
    private final Map<Long, Map<Long, Double>> minWeightsSum = new HashMap<>();

    public List<EventSimilarityAvro> calcSimilarity(UserActionAvro action) {
        List<EventSimilarityAvro> similarity = new ArrayList<>();
        Long actionEventId = action.getEventId();
        Long actionUserId = action.getUserId();

        double delta = updateEventAction(action);

        if (delta > 0) {

            eventWeights.merge(actionEventId, delta, Double::sum);


            Set<Long> anotherEvents = eventActions.keySet().stream() // получили события с которыми были действия
                    .filter(id -> !Objects.equals(id, actionEventId))
                    .collect(Collectors.toSet());

            log.info("получаем коэффициенты схожести для событий {}", anotherEvents);

            anotherEvents.forEach(ae -> {
                double sim = getMinWeightsSum(actionEventId, ae, delta, actionUserId) /
                        (Math.sqrt(eventWeights.get(actionEventId)) * Math.sqrt(eventWeights.get(ae)));

                if (sim != 0.0) {
                    similarity.add(EventSimilarityAvro.newBuilder()
                            .setEventA(Math.min(actionEventId, ae))
                            .setEventB(Math.max(actionEventId, ae))
                            .setScore((float) sim)
                            .setTimestamp(Instant.now())
                            .build());
                }
            });
        }

        return similarity;
    }

    private Double updateEventAction(UserActionAvro action) {
        Long eventId = action.getEventId();
        Long userId = action.getUserId();
        Double newWeight = Double.valueOf(mapActionToWeight(action.getActionType()));

        Map<Long, Double> userActions = eventActions.computeIfAbsent(eventId, k -> new HashMap<>());
        Double oldWeight = userActions.get(userId);

        if (oldWeight == null || newWeight > oldWeight) {
            userActions.put(userId, newWeight);
            return oldWeight == null ? newWeight : newWeight - oldWeight;
        }
        return 0.0;
    }

    private Double getMinWeightsSum(Long eventA, Long eventB, Double delta, Long userId) {
        Long first = Math.min(eventA, eventB);
        Long second = Math.max(eventA, eventB);

        Map<Long, Double> innerMap = minWeightsSum.computeIfAbsent(first, k -> new HashMap<>());

        Double weight = innerMap.get(second);
        if (weight == null) {
            weight = calcMinWeightsSum(eventA, eventB);
            innerMap.put(eventB, weight);

            return weight;
        }

        Double weightA = eventActions.get(eventA).get(userId);
        Double weightB = eventActions.get(eventB).get(userId);

        if (weightB == null) {
            return weight;
        }

        double newWeight;
        if (weightA > weightB && (weightA - delta) < weightB) {
            newWeight = weight + (weightB - (weightA - delta));
        } else {
            newWeight = weight;
        }
        minWeightsSum.get(first).put(second, newWeight);
        return newWeight;

    }

    private Double calcMinWeightsSum(Long eventA, Long eventB) {
        List<Double> weights = new ArrayList<>();

        Map<Long, Double> userActionsA = eventActions.get(eventA);
        Map<Long, Double> userActionsB = eventActions.get(eventB);

        userActionsA.forEach((aUser, aWeight) -> {
            if (userActionsB.containsKey(aUser)) {
                weights.add(Math.min(aWeight, userActionsB.get(aUser)));
            }
        });

        if (weights.isEmpty()) {
            return 0.0;
        }
        return weights.stream().mapToDouble(Double::doubleValue).sum();
    }

    private Float mapActionToWeight(ActionTypeAvro type) {
        return switch (type) {
            case VIEW -> 0.4f;
            case REGISTER -> 0.8f;
            case LIKE -> 1f;
        };
    }
}
