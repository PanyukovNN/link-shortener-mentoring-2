package ru.panyukovnn.linkshortener.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import ru.panyukovnn.linkshortener.annotation.LogExecutionTime;
import ru.panyukovnn.linkshortener.dto.CreateShortLinkRequest;
import ru.panyukovnn.linkshortener.dto.LinkInfoResponse;
import ru.panyukovnn.linkshortener.exception.NotFoundException;
import ru.panyukovnn.linkshortener.model.LinkInfo;
import ru.panyukovnn.linkshortener.repository.LinkInfoRepository;
import ru.panyukovnn.linkshortener.service.LinkInfoService;

import java.util.List;

import static ru.panyukovnn.linkshortener.util.Constants.SHORT_LINK_LENGTH;

@RequiredArgsConstructor
public class LinkInfoServiceImpl implements LinkInfoService {

    private final LinkInfoRepository linkInfoRepository;

    @Override
    @LogExecutionTime(methodName = "Создание короткой ссылки")
    public LinkInfoResponse createLinkInfo(CreateShortLinkRequest createShortLinkRequest) {
        String shortLink = RandomStringUtils.randomAlphanumeric(SHORT_LINK_LENGTH);

        LinkInfo linkInfo = LinkInfo.builder()
            .shortLink(shortLink)
            .link(createShortLinkRequest.getLink())
            .description(createShortLinkRequest.getDescription())
            .endTime(createShortLinkRequest.getEndTime())
            .active(createShortLinkRequest.getActive())
            .openingCount(0L)
            .build();

        LinkInfo savedLinkInfo = linkInfoRepository.save(linkInfo);

        return toResponse(linkInfo);
    }

    @Override
    public LinkInfoResponse getByShortLink(String shortLink) {
        return linkInfoRepository.findByShortLink(shortLink)
            .map(it -> toResponse(it))
            .orElseThrow(() -> new NotFoundException("Не удалось найти сущность по короткой ссылке: " + shortLink));
    }

    @Override
    public List<LinkInfoResponse> findByFilter() {
        return linkInfoRepository.findAll().stream()
            .map(it -> toResponse(it))
            .toList();
    }

    private LinkInfoResponse toResponse(LinkInfo linkInfo) {
        return LinkInfoResponse.builder()
            .id(linkInfo.getId())
            .link(linkInfo.getLink())
            .shortLink(linkInfo.getShortLink())
            .endTime(linkInfo.getEndTime())
            .description(linkInfo.getDescription())
            .active(linkInfo.getActive())
            .openingCount(linkInfo.getOpeningCount())
            .build();
    }
}
