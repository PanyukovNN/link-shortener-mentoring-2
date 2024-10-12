package ru.panyukovnn.linkshortener;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.panyukovnn.linkshortener.annotation.LogExecutionTime;
import ru.panyukovnn.linkshortener.dto.CreateShortLinkRequest;
import ru.panyukovnn.linkshortener.dto.LinkInfoResponse;
import ru.panyukovnn.linkshortener.repository.impl.LinkInfoRepositoryImpl;
import ru.panyukovnn.linkshortener.service.LinkInfoService;
import ru.panyukovnn.linkshortener.service.impl.LinkInfoServiceImpl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.panyukovnn.linkshortener.util.Constants.SHORT_LINK_LENGTH;

class LinkShortenerAppTest {

    LinkInfoService linkInfoService = new LinkInfoServiceImpl(new LinkInfoRepositoryImpl());

    @Test
    void when_createShortLink_then_success() {
        CreateShortLinkRequest request = CreateShortLinkRequest.builder()
            .link("https://google.com")
            .build();

        LinkInfoResponse response = linkInfoService.createLinkInfo(request);
        assertEquals(SHORT_LINK_LENGTH, response.getShortLink().length());

        LinkInfoResponse byShortLink = linkInfoService.getByShortLink(response.getShortLink());
        assertNotNull(byShortLink);
    }

    @Test
    void when_findByFilter_then_success() {
        CreateShortLinkRequest request1 = CreateShortLinkRequest.builder()
            .link("https://google.com")
            .description("ABC")
            .endTime(LocalDateTime.now().plusDays(2))
            .build();
        CreateShortLinkRequest request2 = CreateShortLinkRequest.builder()
            .link("https://ya.ru")
            .description("DEF")
            .endTime(LocalDateTime.now().plusDays(1))
            .build();
        CreateShortLinkRequest request3 = CreateShortLinkRequest.builder()
            .link("https://youtube.coma")
            .description("ABC")
            .endTime(LocalDateTime.now().plusDays(3))
            .build();

        linkInfoService.createLinkInfo(request1);
        linkInfoService.createLinkInfo(request2);
        linkInfoService.createLinkInfo(request3);

        // Можем самостоятельно реализовать сравнение через Comparable
        linkInfoService.findByFilter().stream()
            .sorted()
            .forEach(System.out::println);

        System.out.println();

        // Либо можем указать компаратор
        linkInfoService.findByFilter().stream()
            .sorted(Comparator.comparing(it -> it.getDescription()))
            .forEach(System.out::println);

        System.out.println();

        linkInfoService.findByFilter().stream()
            .sorted(Comparator.<LinkInfoResponse, String>comparing(it -> it.getDescription())
                .thenComparing(it -> it.getEndTime()))
            .forEach(System.out::println);
    }

    @Test
    void test() {
        testSneakyThrows();
    }

    @Test
    void annotation() {
        Method[] methods = LinkInfoServiceImpl.class.getMethods();

        Arrays.stream(methods)
            .filter(method -> method.getAnnotation(LogExecutionTime.class) != null)
            .forEach(method -> {
                System.out.println(method.getAnnotation(LogExecutionTime.class).methodName());
                System.out.println(method.getName());
            });
    }

    @SneakyThrows
    private void testSneakyThrows() {
        throw new IOException();
    }
}