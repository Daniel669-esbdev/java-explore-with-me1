package ru.practicum.dto;

import lombok.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class EndpointHitDto {
    private String app;

    private String uri;

    private String ip;
    private String timestamp;
}