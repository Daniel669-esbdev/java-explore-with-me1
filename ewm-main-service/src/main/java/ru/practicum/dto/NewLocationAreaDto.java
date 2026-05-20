package ru.practicum.dto;

import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewLocationAreaDto {

    @NotBlank
    @Size(min = 1, max = 255)
    private String name;

    @NotNull
    private Float lat;

    @NotNull
    private Float lon;

    @NotNull
    private Float radius;
}