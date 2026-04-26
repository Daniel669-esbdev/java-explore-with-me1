package ru.practicum.dto;

import lombok.*;
import ru.practicum.RequestStatus;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    RequestStatus status;
}