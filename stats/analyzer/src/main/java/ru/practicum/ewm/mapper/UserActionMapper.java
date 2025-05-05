package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.model.UserAction;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@UtilityClass
public class UserActionMapper {

    public UserAction mapToUserAction(UserActionAvro actionAvro) {
        return UserAction.builder()
                .eventId(actionAvro.getEventId())
                .userId(actionAvro.getUserId())
                .type(actionAvro.getActionType())
                .timestamp(actionAvro.getTimestamp())
                .build();
    }
}
