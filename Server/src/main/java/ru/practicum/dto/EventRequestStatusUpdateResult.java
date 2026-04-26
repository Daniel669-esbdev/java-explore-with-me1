package ru.practicum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.ArrayList;
import java.util.List;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Getter
@Setter
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests;
    private List<ParticipationRequestDto> rejectedRequests;

    public EventRequestStatusUpdateResult(List<ParticipationRequestDto> confirmedRequests,
                                          List<ParticipationRequestDto> rejectedRequests) {
        this.confirmedRequests = (confirmedRequests == null) ? null : new ArrayList<>(confirmedRequests);
        this.rejectedRequests = (rejectedRequests == null) ? null : new ArrayList<>(rejectedRequests);
    }

    public List<ParticipationRequestDto> getConfirmedRequests() {
        return (confirmedRequests == null) ? null : new ArrayList<>(confirmedRequests);
    }

    public void setConfirmedRequests(List<ParticipationRequestDto> confirmedRequests) {
        this.confirmedRequests = (confirmedRequests == null) ? null : new ArrayList<>(confirmedRequests);
    }

    public List<ParticipationRequestDto> getRejectedRequests() {
        return (rejectedRequests == null) ? null : new ArrayList<>(rejectedRequests);
    }

    public void setRejectedRequests(List<ParticipationRequestDto> rejectedRequests) {
        this.rejectedRequests = (rejectedRequests == null) ? null : new ArrayList<>(rejectedRequests);
    }
}