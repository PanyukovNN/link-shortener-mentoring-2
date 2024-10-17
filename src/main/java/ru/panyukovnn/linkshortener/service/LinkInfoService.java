package ru.panyukovnn.linkshortener.service;

import ru.panyukovnn.linkshortener.dto.CreateShortLinkRequest;
import ru.panyukovnn.linkshortener.dto.LinkInfoResponse;
import ru.panyukovnn.linkshortener.dto.UpdateShortLinkRequest;

import java.util.List;
import java.util.UUID;

public interface LinkInfoService {

    LinkInfoResponse createLinkInfo(CreateShortLinkRequest createShortLinkRequest);

    LinkInfoResponse getByShortLink(String shortLink);

    List<LinkInfoResponse> findByFilter();

    LinkInfoResponse update(UpdateShortLinkRequest updateShortLinkRequest);

    void deleteById(UUID id);
}
