package ru.practicum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.RequestStatus;

import java.util.ArrayList;
import java.util.List;

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