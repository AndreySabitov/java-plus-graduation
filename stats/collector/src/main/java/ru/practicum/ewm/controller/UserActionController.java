package ru.practicum.ewm.controller;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.grpc.stats.controller.UserActionControllerGrpc;
import ru.practicum.ewm.grpc.stats.event.UserActionProto;

@Slf4j
@GrpcService
public class UserActionController extends UserActionControllerGrpc.UserActionControllerImplBase {

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        log.info("Получили grpc сообщение {}", request);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
