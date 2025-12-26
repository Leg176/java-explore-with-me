package ru.practicum.comment.dto;

import jakarta.validation.constraints.Email;
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
public class NewCommentRequest {
    @NotNull(message = "id события к которому относится комментарий должно быть указано")
    private Long eventId;
    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(min = 20, max = 7000, message = "Длинна комментария должна быть от 20 до 7000 символов")
    private String description;
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 250, message = "Длинна имени должна быть от 2 до 250 символов")
    private String name;
    @NotBlank(message = "email не может быть пустым")
    @Email(message = "Неверный формат электронной почты")
    @Size(min = 6, max = 254, message = "Длинна email должна быть от 6 до 254 символов")
    private String email;
}
