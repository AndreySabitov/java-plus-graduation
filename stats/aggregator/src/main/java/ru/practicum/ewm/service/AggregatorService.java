package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.handler.UserActionHandler;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AggregatorService {
    private final Consumer<String, UserActionAvro> consumer;
    @Value("${kafka.topics.user-action}")
    private String topic;
    @Value("${kafka.properties.consumer.poll-timeout}")
    private int pollTimeout;
    private final UserActionHandler handler;

    public void start() {
        try {
            consumer.subscribe(List.of(topic));

            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, UserActionAvro> records = consumer.poll(Duration.ofMillis(pollTimeout));

                for (ConsumerRecord<String, UserActionAvro> record : records) {
                    UserActionAvro action = record.value();
                    log.info("обрабатываем действие пользователя {}", action);
                    // обработка действий пользователей с событиями
                    List<EventSimilarityAvro> result = handler.calcSimilarity(action);
                    log.info("Получили список схожести {}", result);
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от пользователей", e);
        } finally {
            try {
                //   producer.flush();
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                //  producer.close(Duration.ofSeconds(10));
            }
        }
    }
}
