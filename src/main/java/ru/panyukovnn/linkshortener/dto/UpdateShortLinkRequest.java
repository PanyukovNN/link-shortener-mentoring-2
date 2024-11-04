package ru.panyukovnn.linkshortener.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.panyukovnn.linkshortener.validation.ValidUUID;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShortLinkRequest {

    @ValidUUID
    @NotNull(message = "Идентификатор не может быть пустым")
    private String id;
    @Pattern(regexp = "^http[s]?://.+\\..+$", message = "url не соответствует паттерну")
    private String link;
    @Future(message = "Дата окончания действия короткой ссылки не может быть в прошлом")
    private LocalDateTime endTime;
    @Min(value = 1, message = "Описание не может быть пустым")
    private String description;
    private Boolean active;
}
