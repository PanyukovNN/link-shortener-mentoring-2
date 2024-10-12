package ru.panyukovnn.linkshortener.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LinkInfoResponse implements Comparable {

    private UUID id;
    private String link;
    private String shortLink;
    private LocalDateTime endTime;
    private String description;
    private Boolean active;
    private Long openingCount;

    @Override
    public int compareTo(Object o) {
        // Если хотим обратный пордок, то аргументы надо переставить
        return this.getLink().compareTo(((LinkInfoResponse) o).getLink());
    }
}
