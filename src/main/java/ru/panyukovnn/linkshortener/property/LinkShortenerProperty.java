package ru.panyukovnn.linkshortener.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("link-shortener")
public record LinkShortenerProperty(
    Integer shortLinkLength
) {
}
