package ru.panyukovnn.linkshortener.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.panyukovnn.linkshortener.dto.CreateShortLinkRequest;
import ru.panyukovnn.linkshortener.dto.LinkInfoResponse;
import ru.panyukovnn.linkshortener.model.LinkInfo;

@Mapper(componentModel = "spring")
public interface LinkInfoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "openingCount", constant = "0L")
    LinkInfo fromCreateRequest(CreateShortLinkRequest request, String shortLink);

    LinkInfoResponse toResponse(LinkInfo linkInfo);
}
