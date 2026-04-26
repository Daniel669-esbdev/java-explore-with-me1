package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EndpointHitDto {
    private String app;

    private String uri;

    private String ip;
    private String timestamp;
}