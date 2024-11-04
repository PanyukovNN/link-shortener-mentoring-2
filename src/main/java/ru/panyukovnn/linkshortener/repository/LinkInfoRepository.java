package ru.panyukovnn.linkshortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.panyukovnn.linkshortener.model.LinkInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LinkInfoRepository extends JpaRepository<LinkInfo, UUID> {

    @Query("""
        FROM LinkInfo
        WHERE shortLink = :shortLink
          AND active = true
          AND (endTime IS NULL OR endTime >= :endTime)
        """)
    Optional<LinkInfo> findActiveShortLink(String shortLink, LocalDateTime endTime);

    @Query("""
        UPDATE LinkInfo
        SET openingCount = openingCount + 1
        WHERE shortLink = :shortLink
        """)
    @Modifying
    @Transactional
    void incrementOpeningCountByShortLink(String shortLink);

    @Query(value = """
        SELECT *
        FROM link_info
        WHERE (:linkPart IS NULL OR lower(link) LIKE lower(concat('%', :linkPart, '%')))
          AND (cast(:endTimeFrom AS DATE) IS NULL OR end_time >= :endTimeFrom)
          AND (cast(:endTimeTo AS DATE) IS NULL OR end_time <= :endTimeTo)
          AND (:descriptionPart IS NULL OR lower(description) LIKE lower(concat('%', :descriptionPart, '%')))
          AND (:active IS NULL OR active = :active)
        """,
        nativeQuery = true)
    List<LinkInfo> findByFilter(String linkPart,
                                LocalDateTime endTimeFrom,
                                LocalDateTime endTimeTo,
                                String descriptionPart,
                                Boolean active);
}
