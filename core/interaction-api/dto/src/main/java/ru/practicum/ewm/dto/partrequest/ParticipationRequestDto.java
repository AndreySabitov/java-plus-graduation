package ru.practicum.ewm.dto.partrequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.dto.partrequest.enums.Status;

import java.time.LocalDateTime;

import static ru.practicum.ewm.utils.date.DateTimeFormat.TIME_PATTERN;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    Long id;
    @JsonFormat(pattern = TIME_PATTERN)
    LocalDateTime created;
    Long event;
    Long requester;
    Status status;
}
