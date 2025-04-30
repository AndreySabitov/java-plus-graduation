### Архитектура приложения:

Приложение разбито на 4 микросервиса:
1. event-service обрабатывает запросы связанные с событиями, подборками событий и категориями
2. request-service обрабатывает запросы пользователей связанные с заявками на участие в событиях
3. user-service обрабатывает запросы связанные с пользователями
4. comment-service работает с комментариями пользователей

Вспомогательный модуль interaction-api содержит в себе общие для всех модулей DTO, Exception и Feign-клиенты, с помощью
которых происходит общение между микросервисами

Для обращения к event-service, request-service, comment-service были выделены отдельные внутренние контроллеры: 
InternalEventController, InternalRequestController и InternalCommentController

Все микросервисы при запуске регистрируются в eureka-server. Все конфигурации загружаются из config-server. Все запросы
проходят через gateway-server, который находит нужный сервис через eureka и перенаправляет запрос по нужному адресу

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

#### StatsServer
1. Сохранить статистику: POST /hit
2. Получение статистики: GET /stats

#### UserService
1. Получить список пользователей по их ids: GET /admin/users
2. Найти пользователя по id: GET /admin/users/{userId}
3. Проверить, что пользователь существует: GET /admin/users/{userId}/exists

### Внешний API приложения: 
1. ![ewm-main-service-spec.json](ewm-main-service-spec.json)
2. ![ewm-stats-service-spec.json](ewm-stats-service-spec.json)