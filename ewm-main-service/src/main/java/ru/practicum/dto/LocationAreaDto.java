package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationAreaDto {
    private Long id;
    private String name;
    private Float lat;
    private Float lon;
    private Float radius;
}