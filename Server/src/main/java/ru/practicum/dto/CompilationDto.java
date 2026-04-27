package ru.practicum.dto;

import lombok.*;
import java.util.Set;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class CompilationDto {
    private Long id;
    private Boolean pinned;
    private String title;
    private Set<EventShortDto> events;
}