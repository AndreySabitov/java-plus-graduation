package ru.practicum.ewm.handler;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface UserActionAnalyzerHandler {
    @Transactional
    void handle(UserActionAvro action);
}
