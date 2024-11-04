package ru.panyukovnn.linkshortener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.panyukovnn.linkshortener.dto.CreateShortLinkRequest;
import ru.panyukovnn.linkshortener.dto.FilterLinkInfoRequest;
import ru.panyukovnn.linkshortener.dto.UpdateShortLinkRequest;
import ru.panyukovnn.linkshortener.dto.common.CommonRequest;
import ru.panyukovnn.linkshortener.model.LinkInfo;
import ru.panyukovnn.linkshortener.repository.LinkInfoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LinkShortenerAppTest {

    public static final String LINK_INFOS_BASE_URL = "/api/v1/link-infos";

    public static final String TEST_SHORT_LINK = "test_short_link";
    public static final String TEST_DESCRIPTION = "test description";
    public static final String TEST_LINK = "https://google.com";
    public static final LocalDateTime TEST_END_TIME = LocalDateTime.now().plusDays(1);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private LinkInfoRepository linkInfoRepository;

    @Test
    void when_createLinkInfos_then_success() throws Exception {
        CreateShortLinkRequest createShortLinkRequest = CreateShortLinkRequest.builder()
            .link(TEST_LINK)
            .endTime(TEST_END_TIME)
            .description(TEST_DESCRIPTION)
            .active(true)
            .build();

        CommonRequest<CreateShortLinkRequest> request = CommonRequest.<CreateShortLinkRequest>builder()
            .body(createShortLinkRequest)
            .build();

        LinkInfo linkInfo = createMockLinkInfo();

        when(linkInfoRepository.save(any(LinkInfo.class)))
            .thenReturn(linkInfo);

        mockMvc.perform(post(LINK_INFOS_BASE_URL)
            .content(objectMapper.writeValueAsBytes(request))
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.body.id").value(linkInfo.getId().toString()))
            .andExpect(jsonPath("$.body.link").value(TEST_LINK))
            .andExpect(jsonPath("$.body.shortLink").value(TEST_SHORT_LINK))
            .andExpect(jsonPath("$.body.endTime").value(TEST_END_TIME.toString()))
            .andExpect(jsonPath("$.body.description").value(TEST_DESCRIPTION))
            .andExpect(jsonPath("$.body.active").value(linkInfo.getActive()))
            .andExpect(jsonPath("$.body.openingCount").value(linkInfo.getOpeningCount()));
    }

    @ParameterizedTest
    @MethodSource("invalidCreateShortLinkArguments")
    void when_createShortLink_invalidRequest_then_badRequest(CreateShortLinkRequest createShortLinkRequest, String field) throws Exception {
        CommonRequest<CreateShortLinkRequest> request = CommonRequest.<CreateShortLinkRequest>builder()
            .body(createShortLinkRequest)
            .build();

        mockMvc.perform(post(LINK_INFOS_BASE_URL)
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.errorMessage").exists())
            .andExpect(jsonPath("$.validationErrors").isNotEmpty())
            .andExpect(jsonPath("$.validationErrors[?(@.field == '" + field + "')].message").exists());
    }

    @Test
    void when_postFilter_then_success() throws Exception {
        FilterLinkInfoRequest filterLinkInfoRequest = FilterLinkInfoRequest.builder()
            .linkPart("go")
            .build();

        CommonRequest<FilterLinkInfoRequest> request = CommonRequest.<FilterLinkInfoRequest>builder()
            .body(filterLinkInfoRequest)
            .build();

        LinkInfo linkInfo = createMockLinkInfo();

        when(linkInfoRepository.findByFilter(filterLinkInfoRequest.getLinkPart(), null, null, null, null))
            .thenReturn(List.of(linkInfo));

        mockMvc.perform(post(LINK_INFOS_BASE_URL + "/filter")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.body", hasSize(1)))
            .andExpect(jsonPath("$.body[0].link").value(TEST_LINK))
            .andExpect(jsonPath("$.body[0].shortLink").value(TEST_SHORT_LINK))
            .andExpect(jsonPath("$.body[0].endTime").value(TEST_END_TIME.toString()))
            .andExpect(jsonPath("$.body[0].description").value(TEST_DESCRIPTION))
            .andExpect(jsonPath("$.body[0].active").value(linkInfo.getActive()))
            .andExpect(jsonPath("$.body[0].openingCount").value(linkInfo.getOpeningCount()));
    }

    @Test
    void when_patchLinkInfos_then_success() throws Exception {
        LinkInfo linkInfo = createMockLinkInfo();

        UpdateShortLinkRequest updateShortLinkRequest = UpdateShortLinkRequest.builder()
            .id(linkInfo.getId().toString())
            .link("https://ya.ru")
            .endTime(null)
            .build();

        CommonRequest<UpdateShortLinkRequest> request = CommonRequest.<UpdateShortLinkRequest>builder()
            .body(updateShortLinkRequest)
            .build();

        when(linkInfoRepository.findById(linkInfo.getId()))
            .thenReturn(Optional.of(linkInfo));

        mockMvc.perform(patch(LINK_INFOS_BASE_URL)
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.body.id").value(linkInfo.getId().toString()))
            .andExpect(jsonPath("$.body.link").value(updateShortLinkRequest.getLink()))
            .andExpect(jsonPath("$.body.shortLink").value(TEST_SHORT_LINK))
            .andExpect(jsonPath("$.body.endTime").isEmpty())
            .andExpect(jsonPath("$.body.description").value(TEST_DESCRIPTION))
            .andExpect(jsonPath("$.body.active").value(linkInfo.getActive()))
            .andExpect(jsonPath("$.body.openingCount").value(linkInfo.getOpeningCount()));

        verify(linkInfoRepository).save(linkInfo);
    }

    @ParameterizedTest
    @MethodSource("invalidUpdateShortLinkArguments")
    void when_patchLinkInfos_invalidRequest_then_badRequest(UpdateShortLinkRequest updateShortLinkRequest, String field) throws Exception {
        CommonRequest<UpdateShortLinkRequest> request = CommonRequest.<UpdateShortLinkRequest>builder()
            .body(updateShortLinkRequest)
            .build();

        mockMvc.perform(patch(LINK_INFOS_BASE_URL)
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.errorMessage").exists())
            .andExpect(jsonPath("$.validationErrors").isNotEmpty())
            .andExpect(jsonPath("$.validationErrors[?(@.field == '" + field + "')].message").exists());
    }

    @Test
    void when_deleteLinkInfos_then_success() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete(LINK_INFOS_BASE_URL + "/" + id))
            .andExpect(status().isNoContent())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.body").doesNotExist());

        verify(linkInfoRepository).deleteById(id);
    }

    @Test
    void when_getByShortLink_then_success() throws Exception {
        String shortLink = "randomLink";

        LinkInfo linkInfo = createMockLinkInfo();

        when(linkInfoRepository.findActiveShortLink(eq(shortLink), any(LocalDateTime.class)))
            .thenReturn(Optional.of(linkInfo));

        mockMvc.perform(get("/api/v1/short-link/" + shortLink))
            .andExpect(status().isTemporaryRedirect())
            .andExpect(header().string(HttpHeaders.LOCATION, TEST_LINK));

        verify(linkInfoRepository).findActiveShortLink(eq(shortLink), any(LocalDateTime.class));
    }

    private LinkInfo createMockLinkInfo() {
        return LinkInfo.builder()
            .id(UUID.randomUUID())
            .shortLink(TEST_SHORT_LINK)
            .link(TEST_LINK)
            .endTime(TEST_END_TIME)
            .description(TEST_DESCRIPTION)
            .active(true)
            .openingCount(5L)
            .build();
    }

    static Stream<Arguments> invalidCreateShortLinkArguments() {
        return Stream.of(
            Arguments.of(invalidCreateLinkInfoBuilder().link("htt://test.com").build(), "body.link"),
            Arguments.of(invalidCreateLinkInfoBuilder().link("").build(), "body.link"),
            Arguments.of(invalidCreateLinkInfoBuilder().link(null).build(), "body.link"),
            Arguments.of(invalidCreateLinkInfoBuilder().endTime(LocalDateTime.now().minusDays(1)).build(), "body.endTime"),
            Arguments.of(invalidCreateLinkInfoBuilder().description("").build(), "body.description"),
            Arguments.of(invalidCreateLinkInfoBuilder().description(null).build(), "body.description"),
            Arguments.of(invalidCreateLinkInfoBuilder().active(null).build(), "body.active")
        );
    }

    static CreateShortLinkRequest.CreateShortLinkRequestBuilder invalidCreateLinkInfoBuilder() {
        return CreateShortLinkRequest.builder()
            .link(TEST_LINK)
            .endTime(TEST_END_TIME)
            .description(TEST_DESCRIPTION)
            .active(true);
    }

    static Stream<Arguments> invalidUpdateShortLinkArguments() {
        return Stream.of(
            Arguments.of(invalidUpdateLinkInfoBuilder().id("wrong_uuid").build(), "body.id"),
            Arguments.of(invalidUpdateLinkInfoBuilder().id(null).build(), "body.id"),
            Arguments.of(invalidUpdateLinkInfoBuilder().link("htt://test.com").build(), "body.link"),
            Arguments.of(invalidUpdateLinkInfoBuilder().link("").build(), "body.link"),
            Arguments.of(invalidUpdateLinkInfoBuilder().endTime(LocalDateTime.now().minusDays(1)).build(), "body.endTime"),
            Arguments.of(invalidUpdateLinkInfoBuilder().description("").build(), "body.description")
        );
    }

    static UpdateShortLinkRequest.UpdateShortLinkRequestBuilder invalidUpdateLinkInfoBuilder() {
        return UpdateShortLinkRequest.builder()
            .id(UUID.randomUUID().toString())
            .link(TEST_LINK)
            .endTime(TEST_END_TIME)
            .description(TEST_DESCRIPTION)
            .active(true);
    }
}