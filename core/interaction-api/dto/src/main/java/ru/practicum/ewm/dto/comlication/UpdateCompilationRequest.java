package ru.practicum.ewm.dto.comlication;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequest {
    Set<Long> events;
    Boolean pinned;
    @Size(min = 1, max = 50)
    String title;
}
