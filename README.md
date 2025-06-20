# Explore With Me (Upgraded version) 
## Микросервисная платформа для организации событий с рекомендательной системой

### Архитектурные особенности:
* Основные сервисы:
  * Event-service - управление событиями, категориями и подборками
  * Request-service - обработка заявок на участие в событиях
  * User-service - обработка операций с пользовательскими профилями
  * Comment-service - система комментирования событий
* Вспомогательные компоненты:
  * Interaction-API — общий модуль с DTO, исключениями и Feign-клиентами для межсервисного взаимодействия
  * Internal API — выделенные контроллеры для внутренней коммуникации:
    * InternalEventController
    * InternalRequestController
    * InternalCommentController
* Ключевые решения:
  * Cloud-Native подход:
    * Централизованная конфигурация через Config Server
    * Сервис обнаружения Eureka Server для динамической маршрутизации
    * Единая точка входа через Spring Cloud Gateway
    * Балансировка нагрузки с Spring Cloud Load Balancer
  * Отказоустойчивость:
    * Реализация паттерна Circuit Breaker (Resilience4j)
* Сервис статистики был переработан в рекомендательный сервис:
  * Алгоритм item-based collaborative filtering
  * Расчет косинусной меры сходства между событиями
  * Прогнозирование пользовательских оценок
  * Персонализированные рекомендации топ-N событий

### Преимущества обновления:
* Микросервисная архитектура с четким разделением ответственности
* Адаптация для работы в облачных средах (внедрены компоненты Spring Cloud: Config, Eureka, Gateway)
* Интеграция механизмов обработки больших потоков данных (Apache Kafka, Avro, Protobuf)

### Технологии:
* Java 21
* Spring Boot 3.3.4
* Maven
* PostgreSQL
* Spring Data JPA
* Hibernate ORM
* Apache Kafka
* Apache Avro
* Protocol Buffers
* gRPC
* Spring Cloud Config
* Spring Cloud Eureka
* Spring Cloud Feign
* Spring Cloud Gateway
* Spring Cloud Load Balancer
* Resilience4j

### Описание внутреннего API приложения:
#### EventService:
1. Найти событие по id: GET /internal/events/{eventId}
2. Обновить число одобренных заявок на участие: PUT /internal/events/{eventId}
3. Проверить, что событие существует: GET /internal/events
4. Удалить все события, которые инициировал пользователь: DELETE /internal/events

#### RequestService:
1. Выгрузить все подтвержденные заявки для списка событий: GET  /internal/requests/prepare-confirmed-requests
2. Найти все заявки на участие в событии: GET /internal/requests/{eventId}
3. Найти все заявки на участие по списку id: GET /internal/requests
4. Обновить статус заявки на участие: PUT /internal/requests/{requestId}
5. Удалить все заявки пользователя: DELETE /internal/requests

#### CommentService
1. Удалить все комментарии пользователя: DELETE /internal/comment

#### UserService
1. Получить список пользователей по их ids: GET /admin/users
2. Найти пользователя по id: GET /admin/users/{userId}
3. Проверить, что пользователь существует: GET /admin/users/{userId}/exists

### Внешний API приложения: 
1. [main-service](ewm-main-service-spec.json)