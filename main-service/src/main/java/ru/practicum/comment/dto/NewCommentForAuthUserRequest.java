package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentForAuthUserRequest {
    @NotNull(message = "id события к которому относится комментарий должно быть указано")
    private Long eventId;
    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(min = 20, max = 7000, message = "Длинна комментария должна быть от 20 до 7000 символов")
    private String description;
}
