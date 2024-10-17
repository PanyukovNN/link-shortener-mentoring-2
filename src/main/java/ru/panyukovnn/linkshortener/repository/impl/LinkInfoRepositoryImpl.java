package ru.panyukovnn.linkshortener.repository.impl;

import org.springframework.stereotype.Service;
import ru.panyukovnn.linkshortener.model.LinkInfo;
import ru.panyukovnn.linkshortener.repository.LinkInfoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class LinkInfoRepositoryImpl implements LinkInfoRepository {

    private final ConcurrentMap<String, LinkInfo> storage = new ConcurrentHashMap<>();

    @Override
    public LinkInfo save(LinkInfo linkInfo) {
        if (linkInfo.getId() == null) {
            linkInfo.setId(UUID.randomUUID());
        }

        storage.put(linkInfo.getShortLink(), linkInfo);

        return linkInfo;
    }

    @Override
    public Optional<LinkInfo> findByShortLink(String shortLink) {
        return Optional.ofNullable(storage.get(shortLink));
    }

    @Override
    public List<LinkInfo> findAll() {
        return storage.values()
            .stream().toList();
    }

    @Override
    public void deleteById(UUID id) {
        findById(id)
            .ifPresent(linkInfo -> storage.remove(linkInfo.getShortLink()));
    }

    @Override
    public Optional<LinkInfo> findById(UUID id) {
        return storage.values().stream()
            .filter(linkInfo -> linkInfo.getId().equals(id))
            .findAny();
    }
}
