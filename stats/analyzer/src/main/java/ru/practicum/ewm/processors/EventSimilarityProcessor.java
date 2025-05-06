package ru.practicum.ewm.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.handler.EventSimilarityHandler;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventSimilarityProcessor {
    private final Consumer<String, EventSimilarityAvro> consumer;
    private final EventSimilarityHandler handler;
    @Value("${kafka.topics.events-similarity}")
    private String topic;
    @Value("${kafka.properties.consumer.poll-timeout}")
    private int pollTimeout;

    public void start() {
        try {
            consumer.subscribe(List.of(topic));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, EventSimilarityAvro> records = consumer.poll(Duration.ofMillis(pollTimeout));

                for (ConsumerRecord<String, EventSimilarityAvro> record : records) {
                    EventSimilarityAvro eventSimilarity = record.value();
                    log.info("Получили коэффициент схожести: {}", eventSimilarity);

                    handler.handle(eventSimilarity);
                }

                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка чтения данных из топика {}", topic);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }
}
