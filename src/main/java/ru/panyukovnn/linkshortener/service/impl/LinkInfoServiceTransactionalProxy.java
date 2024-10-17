package ru.panyukovnn.linkshortener.service.impl;

import lombok.extern.slf4j.Slf4j;
import ru.panyukovnn.linkshortener.dto.CreateShortLinkRequest;
import ru.panyukovnn.linkshortener.dto.LinkInfoResponse;
import ru.panyukovnn.linkshortener.dto.UpdateShortLinkRequest;
import ru.panyukovnn.linkshortener.service.LinkInfoService;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
public class LinkInfoServiceTransactionalProxy implements LinkInfoService {

    private LinkInfoService linkInfoService;

    public LinkInfoServiceTransactionalProxy(LinkInfoService linkInfoService) {
        this.linkInfoService = linkInfoService;
    }

    @Override
    public LinkInfoResponse createLinkInfo(CreateShortLinkRequest createShortLinkRequest) {
        return executeInTransaction(() -> linkInfoService.createLinkInfo(createShortLinkRequest));
    }

    @Override
    public LinkInfoResponse getByShortLink(String shortLink) {
        return executeInTransaction(() -> linkInfoService.getByShortLink(shortLink));
    }

    @Override
    public List<LinkInfoResponse> findByFilter() {
        return executeInTransaction(() -> linkInfoService.findByFilter());
    }

    @Override
    public LinkInfoResponse update(UpdateShortLinkRequest updateShortLinkRequest) {
        return linkInfoService.update(updateShortLinkRequest);
    }

    @Override
    public void deleteById(UUID id) {
        linkInfoService.deleteById(id);
    }

    private <T> T executeInTransaction(Supplier<T> supplier) {
        log.info("Транзакция открыта");

        try {
            return supplier.get();
        } finally {
            log.info("Транзакция закрыта");
        }
    }
}
