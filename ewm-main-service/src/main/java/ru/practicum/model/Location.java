package ru.practicum.model;

import lombok.*;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Column(name = "lat", nullable = false)
    private Float lat;

    @Column(name = "lon", nullable = false)
    private Float lon;
}