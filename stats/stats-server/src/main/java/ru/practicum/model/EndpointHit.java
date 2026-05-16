package ru.practicum.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hits")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String app;

    @Column(nullable = false)
    private String uri;

    @Column(nullable = false)
    private String ip;

    @Column(name = "hit_timestamp", nullable = false)
    private LocalDateTime timestamp;
}