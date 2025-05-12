package ru.practicum.ewm.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mapper.UserActionMapper;
import ru.practicum.ewm.model.UserAction;
import ru.practicum.ewm.repository.UserActionRepository;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserActionAnalyzerHandlerImpl implements UserActionAnalyzerHandler {
    private final UserActionRepository repository;

    @Transactional
    @Override
    public void handle(UserActionAvro action) {
        Long eventId = action.getEventId();
        Long userId = action.getUserId();
        Float newActionMark = switch (action.getActionType()) {
            case LIKE -> 1.0f;
            case REGISTER -> 0.8f;
            case VIEW -> 0.4f;
        };

        if (!repository.existsByEventIdAndUserId(eventId, userId)) {
            repository.save(UserActionMapper.mapToUserAction(action));
            log.info("Успешно сохранили в БД информацию о действии {}", action);
        } else {
            UserAction userAction = repository.findByEventIdAndUserId(eventId, userId);
            if (userAction.getMark() < newActionMark) {
                userAction.setMark(newActionMark);
                userAction.setTimestamp(action.getTimestamp());
            }
        }
    }
}
