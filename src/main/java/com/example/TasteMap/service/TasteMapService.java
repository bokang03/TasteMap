package com.example.TasteMap.service;

import com.example.TasteMap.api.NaverClient;
import com.example.TasteMap.api.dto.image.SearchImageItem;
import com.example.TasteMap.api.dto.image.SearchImageRequest;
import com.example.TasteMap.api.dto.local.SearchLocalItem;
import com.example.TasteMap.api.dto.local.SearchLocalRequest;
import com.example.TasteMap.api.dto.local.SearchLocalResponse;
import com.example.TasteMap.domain.TasteMapDto;
import com.example.TasteMap.domain.TasteMapEntity;
import com.example.TasteMap.repository.TasteMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TasteMapService {

    private final NaverClient naverClient;
    private final TasteMapRepository tasteMapRepository;

    public TasteMapDto search(String query){
        var localResp = searchLocal(query);
        int total = parseTotal(localResp);
        if (total <= 0) return new TasteMapDto();

        Optional<SearchLocalItem> optLocal = findFirstLocalItem(localResp);
        if (optLocal.isEmpty()) return new TasteMapDto();

        var localItem = optLocal.get();
        String imageQuery = buildImageQuery(localItem);

        Optional<SearchImageItem> optImage = searchImage(imageQuery);
        if (optImage.isEmpty()) return new TasteMapDto();

        return toDto(localItem, optImage.get());
    }

    private SearchLocalResponse searchLocal(String query) {
        var req = new SearchLocalRequest();
        req.setQuery(query);
        return naverClient.searchLocal(req);
    }

    private int parseTotal(SearchLocalResponse resp) {
        if (resp == null || resp.getTotal() == null) return 0;
        try {
            return Integer.parseInt(resp.getTotal());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Optional<SearchLocalItem> findFirstLocalItem(SearchLocalResponse resp) {
        if (resp == null || resp.getItems() == null || resp.getItems().isEmpty()) return Optional.empty();
        return Optional.of(resp.getItems().get(0));
    }

    private String buildImageQuery(SearchLocalItem localItem) {
        if (localItem == null || localItem.getTitle() == null) return "";
        return localItem.getTitle().replaceAll("<[^>]*>", "");
    }

    private Optional<SearchImageItem> searchImage(String imageQuery) {
        if (imageQuery == null || imageQuery.isBlank()) return Optional.empty();
        var req = new SearchImageRequest();
        req.setQuery(imageQuery);
        var resp = naverClient.searchImage(req);
        if (resp == null) return Optional.empty();
        if (resp.getTotal() > 0 && resp.getItems() != null && !resp.getItems().isEmpty()) {
            return Optional.of(resp.getItems().get(0));
        }
        return Optional.empty();
    }

    private TasteMapDto toDto(SearchLocalItem localItem, SearchImageItem imageItem) {
        var result = new TasteMapDto();
        result.setTitle(localItem.getTitle());
        result.setCategory(localItem.getCategory());
        result.setAddress(localItem.getAddress());
        result.setRoadAddress(localItem.getRoadAddress());
        result.setHomePageLink(localItem.getLink());
        result.setImageLink(imageItem.getLink());
        return result;
    }

    public TasteMapDto add(TasteMapDto dto){
        var entity = dtoToEntity(dto);
        var saved = tasteMapRepository.save(entity);
        return entityToDto(saved);
    }

    public List<TasteMapDto> findAll(){
        return tasteMapRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public void delete(int id){
        tasteMapRepository.deleteById(id);
    }

    private TasteMapEntity dtoToEntity(TasteMapDto tasteMapDto){
        var entity = new TasteMapEntity();
        entity.setId(tasteMapDto.getId());
        entity.setTitle(tasteMapDto.getTitle());
        entity.setCategory(tasteMapDto.getCategory());
        entity.setAddress(tasteMapDto.getAddress());
        entity.setRoadAddress(tasteMapDto.getRoadAddress());
        entity.setHomePageLink(tasteMapDto.getHomePageLink());
        entity.setImageLink(tasteMapDto.getImageLink());
        return entity;
    }

    private TasteMapDto entityToDto(TasteMapEntity tasteMapEntity){
        var dto = new TasteMapDto();
        dto.setId(tasteMapEntity.getId());
        dto.setTitle(tasteMapEntity.getTitle());
        dto.setCategory(tasteMapEntity.getCategory());
        dto.setAddress(tasteMapEntity.getAddress());
        dto.setRoadAddress(tasteMapEntity.getRoadAddress());
        dto.setHomePageLink(tasteMapEntity.getHomePageLink());
        dto.setImageLink(tasteMapEntity.getImageLink());
        return dto;
    }
}
