package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentDto {
    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(min = 2, max = 2000, message = "Длина комментария должна быть от 2 до 2000 символов")
    private String text;
}