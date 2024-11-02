package ru.panyukovnn.linkshortener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.panyukovnn.linkshortener.dto.CreateShortLinkRequest;
import ru.panyukovnn.linkshortener.dto.LinkInfoResponse;
import ru.panyukovnn.linkshortener.dto.UpdateShortLinkRequest;
import ru.panyukovnn.linkshortener.property.LinkShortenerProperty;
import ru.panyukovnn.linkshortener.service.LinkInfoService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LinkShortenerAppTest {

    @Autowired
    private LinkInfoService linkInfoService;
    @Autowired
    private LinkShortenerProperty linkShortenerProperty;

    @Test
    void when_createShortLink_then_success() {
        CreateShortLinkRequest request = CreateShortLinkRequest.builder()
            .link("https://google.com")
            .build();

        LinkInfoResponse response = linkInfoService.createLinkInfo(request);
        assertEquals(linkShortenerProperty.shortLinkLength(), response.getShortLink().length());

        LinkInfoResponse byShortLink = linkInfoService.getByShortLink(response.getShortLink());
        assertNotNull(byShortLink);
    }

    @Test
    void when_findByFilter_then_success() {
        CreateShortLinkRequest request = CreateShortLinkRequest.builder()
            .link("https://google.com")
            .description("ABC")
            .endTime(LocalDateTime.now().plusDays(2))
            .build();

        linkInfoService.createLinkInfo(request);

        List<LinkInfoResponse> linkInfoResponses = linkInfoService.findByFilter();

        assertFalse(linkInfoResponses.isEmpty());
    }

    @Test
    void when_update_then_success() {
        CreateShortLinkRequest request = CreateShortLinkRequest.builder()
            .link("https://google.com")
            .endTime(LocalDateTime.now().plusDays(1))
            .description("test description")
            .active(true)
            .build();

        LinkInfoResponse linkInfoResponse = linkInfoService.createLinkInfo(request);

        UpdateShortLinkRequest updateRequest = UpdateShortLinkRequest.builder()
            .id(linkInfoResponse.getId().toString())
            .link("https://ya.ru")
            .endTime(LocalDateTime.now().plusDays(2).toString())
            .description("new description")
            .active(false)
            .build();

        LinkInfoResponse updateResponse = linkInfoService.update(updateRequest);

        assertEquals(updateRequest.getId(), updateResponse.getId().toString());
        assertEquals(updateRequest.getLink(), updateResponse.getLink());
        assertEquals(updateRequest.getEndTime(), updateResponse.getEndTime().toString());
        assertEquals(updateRequest.getDescription(), updateResponse.getDescription());
        assertFalse(updateResponse.getActive());
    }

    @Test
    void when_delete_then_success() {
        CreateShortLinkRequest request = CreateShortLinkRequest.builder()
            .link("https://google.com")
            .endTime(LocalDateTime.now().plusDays(1))
            .description("test description")
            .active(true)
            .build();

        LinkInfoResponse linkInfoResponse = linkInfoService.createLinkInfo(request);

        List<LinkInfoResponse> allResponsesBeforeRemoval = linkInfoService.findByFilter();
        assertThat(allResponsesBeforeRemoval).contains(linkInfoResponse);

        linkInfoService.deleteById(linkInfoResponse.getId());

        List<LinkInfoResponse> allResponsesAfterRemoval = linkInfoService.findByFilter();
        assertThat(allResponsesAfterRemoval).doesNotContain(linkInfoResponse);
    }
}