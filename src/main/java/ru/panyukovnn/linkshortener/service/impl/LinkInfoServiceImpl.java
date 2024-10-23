package ru.panyukovnn.linkshortener.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.panyukovnn.linkshortener.annotation.LogExecutionTime;
import ru.panyukovnn.linkshortener.dto.CreateShortLinkRequest;
import ru.panyukovnn.linkshortener.dto.LinkInfoResponse;
import ru.panyukovnn.linkshortener.dto.UpdateShortLinkRequest;
import ru.panyukovnn.linkshortener.exception.NotFoundException;
import ru.panyukovnn.linkshortener.model.LinkInfo;
import ru.panyukovnn.linkshortener.property.LinkShortenerProperty;
import ru.panyukovnn.linkshortener.repository.LinkInfoRepository;
import ru.panyukovnn.linkshortener.service.LinkInfoService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkInfoServiceImpl implements LinkInfoService {

    private final LinkInfoRepository linkInfoRepository;
    private final LinkShortenerProperty linkShortenerProperty;

    @LogExecutionTime(methodName = "создания короткой ссылки")
    public LinkInfoResponse createLinkInfo(CreateShortLinkRequest createShortLinkRequest) {
        String shortLink = RandomStringUtils.randomAlphanumeric(linkShortenerProperty.getShortLinkLength());

        LinkInfo linkInfo = LinkInfo.builder()
            .shortLink(shortLink)
            .link(createShortLinkRequest.getLink())
            .description(createShortLinkRequest.getDescription())
            .endTime(createShortLinkRequest.getEndTime())
            .active(createShortLinkRequest.getActive())
            .openingCount(0L)
            .build();

        LinkInfo savedLinkInfo = linkInfoRepository.save(linkInfo);

        return toResponse(savedLinkInfo);
    }

    @LogExecutionTime
    public LinkInfoResponse getByShortLink(String shortLink) {
        return linkInfoRepository.findByShortLink(shortLink)
            .map(this::toResponse)
            .orElseThrow(() -> new NotFoundException("Не удалось найти сущность по короткой ссылке: " + shortLink));
    }

    @LogExecutionTime
    public List<LinkInfoResponse> findByFilter() {
        return linkInfoRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @LogExecutionTime
    public LinkInfoResponse update(UpdateShortLinkRequest updateShortLinkRequest) {
        LinkInfo linkInfo = linkInfoRepository.findById(updateShortLinkRequest.getId())
            .orElseThrow(() -> new NotFoundException("Не удалось найти сущность по идентификатору: " + updateShortLinkRequest.getId()));

        if (StringUtils.hasText(updateShortLinkRequest.getLink())) {
            linkInfo.setLink(updateShortLinkRequest.getLink());
        }

        if (updateShortLinkRequest.getEndTime() != null) {
            linkInfo.setEndTime(updateShortLinkRequest.getEndTime());
        }

        if (StringUtils.hasText(updateShortLinkRequest.getDescription())) {
            linkInfo.setDescription(updateShortLinkRequest.getDescription());
        }

        if (updateShortLinkRequest.getActive() != null) {
            linkInfo.setActive(updateShortLinkRequest.getActive());
        }

        linkInfoRepository.save(linkInfo);

        return toResponse(linkInfo);
    }

    @LogExecutionTime
    public void deleteById(UUID id) {
        linkInfoRepository.deleteById(id);
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
