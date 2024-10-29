package ru.panyukovnn.linkshortener.contoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.panyukovnn.linkshortener.dto.CreateShortLinkRequest;
import ru.panyukovnn.linkshortener.dto.LinkInfoResponse;
import ru.panyukovnn.linkshortener.dto.UpdateShortLinkRequest;
import ru.panyukovnn.linkshortener.dto.common.CommonRequest;
import ru.panyukovnn.linkshortener.dto.common.CommonResponse;
import ru.panyukovnn.linkshortener.service.LinkInfoService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/link-infos")
public class LinkInfoController {

    private final LinkInfoService linkInfoService;

    @PostMapping
    public CommonResponse<LinkInfoResponse> postCreateLinkInfo(@RequestBody CommonRequest<CreateShortLinkRequest> request) {
        log.info("Поступил запрос на создание короткой ссылки: {}", request);

        LinkInfoResponse linkInfoResponse = linkInfoService.createLinkInfo(request.getBody());

        log.info("Короткая ссылка создана успешно: {}", linkInfoResponse);

        return CommonResponse.<LinkInfoResponse>builder()
            .id(UUID.randomUUID())
            .body(linkInfoResponse)
            .build();
    }

    @GetMapping
    public CommonResponse<List<LinkInfoResponse>> getLinkInfos() {
        List<LinkInfoResponse> linkInfoResponses = linkInfoService.findByFilter();

        return CommonResponse.<List<LinkInfoResponse>>builder()
            .id(UUID.randomUUID())
            .body(linkInfoResponses)
            .build();
    }

    @PatchMapping
    public CommonResponse<LinkInfoResponse> patchLinkInfos(@RequestBody CommonRequest<UpdateShortLinkRequest> request) {
        LinkInfoResponse linkInfoResponse = linkInfoService.update(request.getBody());

        return CommonResponse.<LinkInfoResponse>builder()
            .id(UUID.randomUUID())
            .body(linkInfoResponse)
            .build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<?> deleteLinkInfos(@PathVariable String id) {
        linkInfoService.deleteById(UUID.fromString(id));

        return CommonResponse.builder()
            .id(UUID.randomUUID())
            .build();
    }
}
