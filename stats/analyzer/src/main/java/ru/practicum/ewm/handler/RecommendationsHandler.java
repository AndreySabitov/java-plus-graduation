package ru.practicum.ewm.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.grpc.stats.event.RecommendedEventProto;
import ru.practicum.ewm.grpc.stats.event.SimilarEventsRequestProto;
import ru.practicum.ewm.grpc.stats.event.UserPredictionsRequestProto;
import ru.practicum.ewm.model.EventSimilarity;
import ru.practicum.ewm.model.UserAction;
import ru.practicum.ewm.repository.EventSimilarityRepository;
import ru.practicum.ewm.repository.UserActionRepository;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecommendationsHandler {
    private final UserActionRepository userActionRepository;
    private final EventSimilarityRepository eventSimilarityRepository;

    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        Long userId = request.getUserId();

        List<UserAction> userActions = userActionRepository.findAllByUserId(userId,
                PageRequest.of(0, request.getMaxResults(),
                        Sort.by(Sort.Direction.DESC, "timestamp")));

        if (userActions.isEmpty()) {
            return List.of();
        }

        List<EventSimilarity> eventSimilarities = eventSimilarityRepository.findAllByEventAIn(userActions.stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet()), Sort.by(Sort.Direction.DESC, "score"));
        List<EventSimilarity> eventSimilaritiesB = eventSimilarityRepository.findAllByEventBIn(userActions.stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet()), Sort.by(Sort.Direction.DESC, "score"));

        List<Long> newEventIdsA = eventSimilarities.stream()// мероприятия с которыми пользователь ещё не взаимодействовал
                .map(EventSimilarity::getEventB)
                .filter(eventId -> !userActionRepository.existsByEventIdAndUserId(eventId, userId))
                .distinct()
                .limit(request.getMaxResults())
                .toList();

        List<Long> newEventIdsB = eventSimilaritiesB.stream()// мероприятия с которыми пользователь ещё не взаимодействовал
                .map(EventSimilarity::getEventA)
                .filter(eventId -> !userActionRepository.existsByEventIdAndUserId(eventId, userId))
                .distinct()
                .limit(request.getMaxResults())
                .toList();

        Set<Long> newEventIds = new HashSet<>(newEventIdsA);
        newEventIds.addAll(newEventIdsB);


        return newEventIds.stream()
                .map(eId -> RecommendedEventProto.newBuilder()
                        .setEventId(eId)
                        .setScore(calcScore(eId, userId))
                        .build())
                .toList();
    }

    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        Long eventId = request.getEventId();
        Long userId = request.getUserId();

        List<EventSimilarity> eventSimilaritiesA = eventSimilarityRepository.findAllByEventA(eventId,
                Sort.by(Sort.Direction.DESC, "score"));
        List<EventSimilarity> eventSimilaritiesB = eventSimilarityRepository.findAllByEventB(eventId,
                Sort.by(Sort.Direction.DESC, "score"));

        List<RecommendedEventProto> recommendations = new ArrayList<>(eventSimilaritiesA.stream()
                .filter(es -> !userActionRepository.existsByEventIdAndUserId(es.getEventB(), userId))
                .map(es -> RecommendedEventProto.newBuilder()
                        .setEventId(es.getEventB())
                        .setScore(es.getScore())
                        .build())
                .toList());

        List<RecommendedEventProto> recommendationsB = eventSimilaritiesB.stream()
                .filter(es -> !userActionRepository.existsByEventIdAndUserId(es.getEventA(), userId))
                .map(es -> RecommendedEventProto.newBuilder()
                        .setEventId(es.getEventA())
                        .setScore(es.getScore())
                        .build())
                .toList();

        recommendations.addAll(recommendationsB);

        return recommendations.stream()
                .sorted(Comparator.comparing(RecommendedEventProto::getScore).reversed())
                .toList();
    }

    private float calcScore(Long eventId, Long userId) {
        List<EventSimilarity> eventSimilaritiesA = eventSimilarityRepository.findAllByEventA(eventId,
                Sort.by(Sort.Direction.DESC, "score"));

        List<EventSimilarity> eventSimilaritiesB = eventSimilarityRepository.findAllByEventB(eventId,
                Sort.by(Sort.Direction.DESC, "score"));

        Map<Long, Float> viewedEventScores = eventSimilaritiesA.stream()
                .filter(es -> userActionRepository.existsByEventIdAndUserId(es.getEventB(), userId))
                .collect(Collectors.toMap(EventSimilarity::getEventB, EventSimilarity::getScore));

        Map<Long, Float> viewedEventScoresB = eventSimilaritiesB.stream()
                .filter(es -> userActionRepository.existsByEventIdAndUserId(es.getEventA(), userId))
                .collect(Collectors.toMap(EventSimilarity::getEventA, EventSimilarity::getScore));

        viewedEventScores.putAll(viewedEventScoresB);

        Map<Long, Float> actionMarks = userActionRepository.findAllByEventIdInAndUserId(viewedEventScores.keySet(),
                        userId).stream()
                .collect(Collectors.toMap(UserAction::getEventId, UserAction::getMark));

        Float sumWeightedMarks = ((Double) viewedEventScores.entrySet().stream()
                .map(entry -> actionMarks.get(entry.getKey()) * entry.getValue())
                .mapToDouble(Float::floatValue).sum())
                .floatValue();

        Float sumScores = ((Double) viewedEventScores.values().stream().mapToDouble(Float::floatValue).sum())
                .floatValue();

        return sumWeightedMarks / sumScores;
    }
}
