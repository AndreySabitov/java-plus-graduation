package ru.practicum.ewm.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
    private final Map<Long, Map<Long, Float>> eventSumOfMin = new HashMap<>(); // значения числителя

    public List<EventSimilarityAvro> calcSimilarity(UserActionAvro action) {
        List<EventSimilarityAvro> similarity = new ArrayList<>();
        Set<Long> anotherEvents = eventActions.keySet().stream()
                .filter(id -> id != action.getEventId())
                .collect(Collectors.toSet());

        log.info("получаем коэффициенты схожести для событий {}", anotherEvents);

        Float delta = updateUserActionWeight(action);
        log.info("eventActions после обновления {}", eventActions);
        log.info("delta = {}", delta);
        if (delta > 0) {
            calcSumOfWeights(action.getEventId(), delta);
            log.info("map весов для всех событий {}", eventWeights);
            anotherEvents.forEach(eb ->
                    calcSumOfMinWeightsOfEvents(action.getEventId(), eb));
            log.info("eventSumOfMin {}", eventSumOfMin);
        }

        anotherEvents.forEach(eb -> {
            float result = (float) (eventSumOfMin.get(action.getEventId()).get(eb) /
                    (Math.sqrt(eventWeights.get(action.getEventId())) * Math.sqrt(eventWeights.get(eb))));

            long min = Math.min(action.getEventId(), eb);
            long max = Math.max(action.getEventId(), eb);
            similarity.add(EventSimilarityAvro.newBuilder()
                    .setEventA(min)
                    .setEventB(max)
                    .setScore(result)
                    .setTimestamp(Instant.now())
                    .build());
        });

        return similarity;
    }

    private Float updateUserActionWeight(UserActionAvro action) {
        Map<Long, Float> actionWeight;
        Float score = switch (action.getActionType()) {
            case LIKE -> 1.0f;
            case REGISTER -> 0.8f;
            case VIEW -> 0.4f;
        };

        if (!eventActions.containsKey(action.getEventId())) {
            actionWeight = new HashMap<>();
            actionWeight.put(action.getUserId(), score);
            eventActions.put(action.getEventId(), actionWeight);
            return score;
        } else {
            actionWeight = eventActions.get(action.getEventId());

            if (!actionWeight.containsKey(action.getUserId())) {
                actionWeight.put(action.getUserId(), score);
                eventActions.put(action.getEventId(), actionWeight);
                return score;
            }
            if (score > actionWeight.get(action.getUserId())) {
                Float delta = score - actionWeight.get(action.getUserId());
                actionWeight.put(action.getUserId(), score);
                eventActions.put(action.getEventId(), actionWeight);
                return delta;
            }
            return 0f;
        }
    }

    private void calcSumOfWeights(Long eventId, Float delta) {
        if (!eventWeights.containsKey(eventId)) {
            eventWeights.put(eventId, delta);
        } else {
            eventWeights.compute(eventId, (k, oldSum) -> oldSum + delta);
        }
    }

    private void calcSumOfMinWeightsOfEvents(Long eventA, Long eventB) {
        List<Float> weights = new ArrayList<>();

        Map<Long, Float> eventAActions = eventActions.get(eventA);
        Map<Long, Float> eventBActions = eventActions.get(eventB);

        eventAActions.forEach((aUser, aWeight) -> {
            if (eventBActions.containsKey(aUser)) {
                weights.add(Math.min(aWeight, eventBActions.get(aUser)));
            }
        });

        float result = weights.stream().collect(Collectors.summingDouble(Float::floatValue)).floatValue();

        if (!eventSumOfMin.containsKey(eventA)) {
            Map<Long, Float> map = new HashMap<>();
            map.put(eventB, result);
            eventSumOfMin.put(eventA, map);
        } else {
            eventSumOfMin.get(eventA).put(eventB, result);
        }
    }
}
