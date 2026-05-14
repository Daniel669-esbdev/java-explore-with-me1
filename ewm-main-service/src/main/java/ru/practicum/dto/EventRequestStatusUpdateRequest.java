package ru.practicum.dto;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.RequestStatus;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP")
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus status;
}