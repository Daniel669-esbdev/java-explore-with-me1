package ru.practicum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.RequestStatus;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.ArrayList;
import java.util.List;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Getter
@Setter
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus status;

    public EventRequestStatusUpdateRequest(List<Long> requestIds, RequestStatus status) {
        this.requestIds = (requestIds == null) ? null : new ArrayList<>(requestIds);
        this.status = status;
    }

    public List<Long> getRequestIds() {
        return (requestIds == null) ? null : new ArrayList<>(requestIds);
    }

    public void setRequestIds(List<Long> requestIds) {
        this.requestIds = (requestIds == null) ? null : new ArrayList<>(requestIds);
    }
}