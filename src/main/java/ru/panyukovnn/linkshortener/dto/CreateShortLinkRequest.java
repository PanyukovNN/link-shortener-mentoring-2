package ru.panyukovnn.linkshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateShortLinkRequest {

    private String link;
    private LocalDateTime endTime;
    private String description;
    private Boolean active;
}
