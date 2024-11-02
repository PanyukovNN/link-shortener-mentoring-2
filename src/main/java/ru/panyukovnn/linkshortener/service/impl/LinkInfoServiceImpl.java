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
import ru.panyukovnn.linkshortener.mapper.LinkInfoMapper;
import ru.panyukovnn.linkshortener.model.LinkInfo;
import ru.panyukovnn.linkshortener.property.LinkShortenerProperty;
import ru.panyukovnn.linkshortener.repository.LinkInfoRepository;
import ru.panyukovnn.linkshortener.service.LinkInfoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkInfoServiceImpl implements LinkInfoService {

    private final LinkInfoMapper linkInfoMapper;
    private final LinkInfoRepository linkInfoRepository;
    private final LinkShortenerProperty linkShortenerProperty;

    @LogExecutionTime(methodName = "создания короткой ссылки")
    public LinkInfoResponse createLinkInfo(CreateShortLinkRequest createShortLinkRequest) {
        String shortLink = RandomStringUtils.randomAlphanumeric(linkShortenerProperty.shortLinkLength());

        LinkInfo linkInfo = linkInfoMapper.fromCreateRequest(createShortLinkRequest, shortLink);

        LinkInfo savedLinkInfo = linkInfoRepository.save(linkInfo);

        return linkInfoMapper.toResponse(savedLinkInfo);
    }

    @LogExecutionTime
    public LinkInfoResponse getByShortLink(String shortLink) {
        return linkInfoRepository.findByShortLink(shortLink)
            .map(linkInfoMapper::toResponse)
            .orElseThrow(() -> new NotFoundException("Не удалось найти сущность по короткой ссылке: " + shortLink));
    }

    @LogExecutionTime
    public List<LinkInfoResponse> findByFilter() {
        return linkInfoRepository.findAll().stream()
            .map(linkInfoMapper::toResponse)
            .toList();
    }

    @LogExecutionTime
    public LinkInfoResponse update(UpdateShortLinkRequest updateShortLinkRequest) {
        LinkInfo linkInfo = linkInfoRepository.findById(UUID.fromString(updateShortLinkRequest.getId()))
            .orElseThrow(() -> new NotFoundException("Не удалось найти сущность по идентификатору: " + updateShortLinkRequest.getId()));

        if (StringUtils.hasText(updateShortLinkRequest.getLink())) {
            linkInfo.setLink(updateShortLinkRequest.getLink());
        }

        if (updateShortLinkRequest.getEndTime() != null) {
            linkInfo.setEndTime(LocalDateTime.parse(updateShortLinkRequest.getEndTime()));
        }

        if (StringUtils.hasText(updateShortLinkRequest.getDescription())) {
            linkInfo.setDescription(updateShortLinkRequest.getDescription());
        }

        if (updateShortLinkRequest.getActive() != null) {
            linkInfo.setActive(updateShortLinkRequest.getActive());
        }

        linkInfoRepository.save(linkInfo);

        return linkInfoMapper.toResponse(linkInfo);
    }

    @LogExecutionTime
    public void deleteById(UUID id) {
        linkInfoRepository.deleteById(id);
    }
}
