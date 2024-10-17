package ru.panyukovnn.linkshortener.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import ru.panyukovnn.linkshortener.dto.CreateShortLinkRequest;
import ru.panyukovnn.linkshortener.dto.LinkInfoResponse;
import ru.panyukovnn.linkshortener.dto.UpdateShortLinkRequest;
import ru.panyukovnn.linkshortener.service.LinkInfoService;

import java.util.List;
import java.util.UUID;

@Slf4j
public class LinkInfoServiceLoggingProxy implements LinkInfoService {

    private LinkInfoService linkInfoService;

    public LinkInfoServiceLoggingProxy(LinkInfoService linkInfoService) {
        this.linkInfoService = linkInfoService;
    }

    @Override
    public LinkInfoResponse createLinkInfo(CreateShortLinkRequest createShortLinkRequest) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            return linkInfoService.createLinkInfo(createShortLinkRequest);
        } finally {
            stopWatch.stop();
            log.info("Время выполнения метода createLinkInfo: " + stopWatch.getTotalTimeMillis());
        }
    }

    @Override
    public LinkInfoResponse getByShortLink(String shortLink) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            return linkInfoService.getByShortLink(shortLink);
        } finally {
            stopWatch.stop();
            log.info("Время выполнения метода getByShortLink: " + stopWatch.getTotalTimeMillis());
        }
    }

    @Override
    public List<LinkInfoResponse> findByFilter() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            return linkInfoService.findByFilter();
        } finally {
            stopWatch.stop();
            log.info("Время выполнения метода findByFilter: " + stopWatch.getTotalTimeMillis());
        }
    }

    @Override
    public LinkInfoResponse update(UpdateShortLinkRequest updateShortLinkRequest) {
        return linkInfoService.update(updateShortLinkRequest);
    }

    @Override
    public void deleteById(UUID id) {
        linkInfoService.deleteById(id);
    }
}
