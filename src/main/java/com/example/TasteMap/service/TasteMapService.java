package com.example.TasteMap.service;

import com.example.TasteMap.api.NaverClient;
import com.example.TasteMap.api.dto.image.SearchImageRequest;
import com.example.TasteMap.api.dto.local.SearchLocalRequest;
import com.example.TasteMap.domain.TasteMapDto;
import com.example.TasteMap.domain.TasteMapEntity;
import com.example.TasteMap.exception.ErrorMessage;
import com.example.TasteMap.exception.ResourceAlreadyExistsException;
import com.example.TasteMap.repository.TasteMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TasteMapService {

    private final NaverClient naverClient;
    private final TasteMapRepository tasteMapRepository;

    public List<TasteMapDto> search(String query, int page){
        var searchLocalReq = new SearchLocalRequest();
        searchLocalReq.setQuery(query);
        searchLocalReq.setDisplay(100);
        searchLocalReq.setPage(page);

        var searchLocalRes = naverClient.searchLocal(searchLocalReq);
        if (searchLocalRes == null || searchLocalRes.getTotal() <= 0 || searchLocalRes.getItems() == null) {
            return List.of();
        }

        return searchLocalRes.getItems().stream()
                .limit(100)
                .map(localItem -> {
                    var dto = new TasteMapDto();
                    dto.setTitle(localItem.getTitle());
                    dto.setCategory(localItem.getCategory());
                    dto.setAddress(localItem.getAddress());
                    dto.setRoadAddress(localItem.getRoadAddress());

                    try {
                        var imageQuery = localItem.getTitle().replaceAll("<[^>]*>", "");
                        var searchImageReq = new SearchImageRequest();
                        searchImageReq.setQuery(imageQuery);
                        searchImageReq.setDisplay(1);

                        var searchImageRes = naverClient.searchImage(searchImageReq);
                        if (searchImageRes != null && searchImageRes.getTotal() > 0 && searchImageRes.getItems() != null && !searchImageRes.getItems().isEmpty()) {
                            dto.setImageLink(searchImageRes.getItems().get(0).getLink());
                        }
                    } catch (Exception ignored) {
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public TasteMapDto add(TasteMapDto dto){
        ensureNotDuplicate(dto);
        var entity = dtoToEntity(dto);
        var saved = tasteMapRepository.save(entity);
        return entityToDto(saved);
    }

    private void ensureNotDuplicate(TasteMapDto dto) {
        boolean exists = tasteMapRepository.findAll()
                .stream()
                .anyMatch(e -> safeEquals(e.getTitle(), dto.getTitle()) && safeEquals(e.getAddress(), dto.getAddress()));
        if (exists) {
            throw new ResourceAlreadyExistsException(ErrorMessage.INVALID_TASTE_MAP_DUPLICATE.getMessage());
        }
    }

    private boolean safeEquals(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
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
        dto.setImageLink(tasteMapEntity.getImageLink());
        return dto;
    }
}
