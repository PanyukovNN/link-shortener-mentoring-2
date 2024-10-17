package ru.panyukovnn.linkshortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.panyukovnn.linkshortener.property.LinkShortenerProperty;
import ru.panyukovnn.linkshortener.repository.LinkInfoRepository;
import ru.panyukovnn.linkshortener.repository.impl.LinkInfoRepositoryImpl;
import ru.panyukovnn.linkshortener.service.LinkInfoService;
import ru.panyukovnn.linkshortener.service.impl.LinkInfoServiceImpl;
import ru.panyukovnn.linkshortener.service.impl.LinkInfoServiceLoggingProxy;
import ru.panyukovnn.linkshortener.service.impl.LinkInfoServiceTransactionalProxy;

@Configuration
public class LinkShortenerConfig {

    @Bean
    public LinkInfoRepository linkInfoRepository() {
        return new LinkInfoRepositoryImpl();
    }

    @Bean
    public LinkInfoService linkInfoService(LinkInfoRepository linkInfoRepository, LinkShortenerProperty linkShortenerProperty) {
        LinkInfoServiceImpl linkInfoService = new LinkInfoServiceImpl(linkInfoRepository, linkShortenerProperty);
        LinkInfoService linkInfoServiceLoggingProxy = new LinkInfoServiceLoggingProxy(linkInfoService);
        LinkInfoService linkInfoServiceTransactionalProxy = new LinkInfoServiceTransactionalProxy(linkInfoServiceLoggingProxy);

        return linkInfoServiceTransactionalProxy;
    }
}
