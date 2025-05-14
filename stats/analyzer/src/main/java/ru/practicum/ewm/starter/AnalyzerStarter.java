package ru.practicum.ewm.starter;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.processors.EventSimilarityProcessor;
import ru.practicum.ewm.processors.UserActionProcessor;

@Component
@RequiredArgsConstructor
public class AnalyzerStarter implements CommandLineRunner {
    private final UserActionProcessor userActionProcessor;
    private final EventSimilarityProcessor eventSimilarityProcessor;


    @Override
    public void run(String... args) {
        Thread userActionThread = new Thread(userActionProcessor);
        userActionThread.setName("userActionHandlerThread");
        userActionThread.start();

        eventSimilarityProcessor.start();
    }
}
