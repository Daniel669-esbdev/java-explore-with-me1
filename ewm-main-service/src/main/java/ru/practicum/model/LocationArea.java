package ru.practicum.model;

import lombok.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.persistence.*;

@Entity
@Table(name = "location_areas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class LocationArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Embedded
    private Location location;

    @Column(name = "radius", nullable = false)
    private Float radius;
}