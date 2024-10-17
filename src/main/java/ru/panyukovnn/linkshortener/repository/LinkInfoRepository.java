package ru.panyukovnn.linkshortener.repository;

import ru.panyukovnn.linkshortener.model.LinkInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LinkInfoRepository {

    LinkInfo save(LinkInfo linkInfo);

    Optional<LinkInfo> findByShortLink(String shortLink);

    List<LinkInfo> findAll();

    Optional<LinkInfo> findById(UUID id);

    void deleteById(UUID id);
}
