package ru.panyukovnn.linkshortener.contoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.panyukovnn.linkshortener.dto.LinkInfoResponse;
import ru.panyukovnn.linkshortener.service.LinkInfoService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/short-link")
public class ShortLinkController {

    private final LinkInfoService linkInfoService;

    @GetMapping("/{shortLink}")
    public ResponseEntity<String> getByShortLink(@PathVariable String shortLink) {
        log.info("Поступил запрос на открытие короткой ссылки: {}", shortLink);

        LinkInfoResponse linkInfoResponse = linkInfoService.getByShortLink(shortLink);

        return ResponseEntity
            .status(HttpStatus.TEMPORARY_REDIRECT)
            .header(HttpHeaders.LOCATION, linkInfoResponse.getLink())
            .build();
    }
}
