package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.grpc.stats.controller.RecommendationsControllerGrpc;
import ru.practicum.ewm.repository.EventSimilarityRepository;
import ru.practicum.ewm.repository.UserActionRepository;

@GrpcService
@RequiredArgsConstructor
public class EventRecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    private final UserActionRepository userActionRepository;
    private final EventSimilarityRepository eventSimilarityRepository;
}
