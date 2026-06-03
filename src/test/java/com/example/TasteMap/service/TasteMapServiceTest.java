package com.example.TasteMap.service;

import com.example.TasteMap.api.NaverClient;
import com.example.TasteMap.api.dto.image.SearchImageItem;
import com.example.TasteMap.api.dto.image.SearchImageResponse;
import com.example.TasteMap.api.dto.local.SearchLocalItem;
import com.example.TasteMap.api.dto.local.SearchLocalResponse;
import com.example.TasteMap.domain.TasteMapDto;
import com.example.TasteMap.domain.TasteMapEntity;
import com.example.TasteMap.exception.ResourceAlreadyExistsException;
import com.example.TasteMap.repository.TasteMapRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TasteMapServiceTest {

    @Mock
    private NaverClient naverClient;

    @Mock
    private TasteMapRepository tasteMapRepository;

    @InjectMocks
    private TasteMapService tasteMapService;

    @Test
    void addSavesTasteMapAndReturnsSavedDto() {
        var request = TasteMapDto.builder()
                .title("Gamjatang House")
                .category("Korean")
                .address("Seoul")
                .roadAddress("Seoul Road")
                .imageLink("https://example.com/image.jpg")
                .build();

        given(tasteMapRepository.findAll()).willReturn(List.of());
        given(tasteMapRepository.save(any(TasteMapEntity.class))).willReturn(TasteMapEntity.builder()
                .id(1)
                .title(request.getTitle())
                .category(request.getCategory())
                .address(request.getAddress())
                .roadAddress(request.getRoadAddress())
                .imageLink(request.getImageLink())
                .build());

        var saved = tasteMapService.add(request);

        assertThat(saved.getId()).isEqualTo(1);
        assertThat(saved.getTitle()).isEqualTo("Gamjatang House");
        assertThat(saved.getAddress()).isEqualTo("Seoul");
    }

    @Test
    void addThrowsWhenSameTitleAndAddressAlreadyExist() {
        var request = TasteMapDto.builder()
                .title("Gamjatang House")
                .address("Seoul")
                .build();
        var existing = TasteMapEntity.builder()
                .title("Gamjatang House")
                .address("Seoul")
                .build();

        given(tasteMapRepository.findAll()).willReturn(List.of(existing));

        assertThatThrownBy(() -> tasteMapService.add(request))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    void findAllReturnsSavedTasteMaps() {
        given(tasteMapRepository.findAll()).willReturn(List.of(
                TasteMapEntity.builder()
                        .id(1)
                        .title("Noodle Bar")
                        .category("Japanese")
                        .address("Busan")
                        .roadAddress("Busan Road")
                        .imageLink("https://example.com/noodle.jpg")
                        .build()
        ));

        var result = tasteMapService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Noodle Bar");
        assertThat(result.get(0).getImageLink()).isEqualTo("https://example.com/noodle.jpg");
    }

    @Test
    void searchMapsLocalResultAndFirstImage() {
        var localItem = new SearchLocalItem(
                "<b>Gamjatang House</b>",
                "",
                "Korean",
                "",
                "",
                "Seoul",
                "Seoul Road",
                "",
                ""
        );
        var localResponse = new SearchLocalResponse("", 1, "1", "1", "Korean", List.of(localItem));
        var imageItem = new SearchImageItem("", "https://example.com/gamjatang.jpg", "", "", "");
        var imageResponse = new SearchImageResponse("", 1, 1, 1, List.of(imageItem));

        given(naverClient.searchLocal(any())).willReturn(localResponse);
        given(naverClient.searchImage(any())).willReturn(imageResponse);

        var result = tasteMapService.search("gamjatang");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("<b>Gamjatang House</b>");
        assertThat(result.get(0).getCategory()).isEqualTo("Korean");
        assertThat(result.get(0).getImageLink()).isEqualTo("https://example.com/gamjatang.jpg");
    }
}
