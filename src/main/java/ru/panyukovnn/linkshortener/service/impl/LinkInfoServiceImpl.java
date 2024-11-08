package ru.panyukovnn.linkshortener.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.panyukovnn.linkshortener.dto.*;
import ru.panyukovnn.linkshortener.exception.NotFoundException;
import ru.panyukovnn.linkshortener.exception.NotFoundShortLinkException;
import ru.panyukovnn.linkshortener.mapper.LinkInfoMapper;
import ru.panyukovnn.linkshortener.model.LinkInfo;
import ru.panyukovnn.linkshortener.property.LinkShortenerProperty;
import ru.panyukovnn.linkshortener.repository.LinkInfoRepository;
import ru.panyukovnn.linkshortener.service.LinkInfoService;
import ru.panyukovnn.loggingstarter.annotation.LogExecutionTime;

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
        LinkInfo linkInfo = linkInfoRepository.findActiveShortLink(shortLink, LocalDateTime.now())
            .orElseThrow(() -> new NotFoundShortLinkException("Не удалось найти сущность по короткой ссылке: " + shortLink));

        linkInfoRepository.incrementOpeningCountByShortLink(shortLink);

        return linkInfoMapper.toResponse(linkInfo);
    }

    @LogExecutionTime
    public List<LinkInfoResponse> findByFilter(FilterLinkInfoRequest filterRequest) {
        PageableRequest page = filterRequest.getPage();

        Pageable pageable = mapPageable(page);

        return linkInfoRepository.findByFilter(
                filterRequest.getLinkPart(),
                filterRequest.getEndTimeFrom(),
                filterRequest.getEndTimeTo(),
                filterRequest.getDescriptionPart(),
                filterRequest.getActive(),
                pageable
            ).stream()
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

        linkInfo.setEndTime(updateShortLinkRequest.getEndTime());

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

    private Pageable mapPageable(PageableRequest page) {
        List<Sort.Order> sorts = page.getSorts().stream()
            .map(sort -> new Sort.Order(
                Sort.Direction.valueOf(sort.getDirection()),
                sort.getField()
            ))
            .toList();

        return PageRequest.of(page.getNumber() - 1, page.getSize(), Sort.by(sorts));
    }
}
