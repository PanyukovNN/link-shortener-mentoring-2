package ru.panyukovnn.linkshortener.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.panyukovnn.linkshortener.validation.ValidLocalDateTime;
import ru.panyukovnn.linkshortener.validation.ValidUUID;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShortLinkRequest {

    @ValidUUID
    private String id;
    private String link;
    @ValidLocalDateTime
    private String endTime;
    private String description;
    private Boolean active;
}
