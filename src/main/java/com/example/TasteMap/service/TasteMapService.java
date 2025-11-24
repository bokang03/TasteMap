package com.example.TasteMap.service;

import com.example.TasteMap.api.NaverClient;
import com.example.TasteMap.api.dto.image.SearchImageRequest;
import com.example.TasteMap.api.dto.local.SearchLocalItem;
import com.example.TasteMap.api.dto.local.SearchLocalRequest;
import com.example.TasteMap.api.dto.local.SearchLocalResponse;
import com.example.TasteMap.domain.TasteMapDto;
import com.example.TasteMap.domain.TasteMapEntity;
import com.example.TasteMap.exception.ErrorMessage;
import com.example.TasteMap.exception.ResourceNotFoundException;
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

    public List<TasteMapDto> search(String query) {
        if (isBlank(query)) return java.util.List.of();
        var req = buildLocalRequest(query, 10, 1);
        var res = callLocalSearch(req);
        if (noLocalResults(res)) throw new ResourceNotFoundException(ErrorMessage.NO_SEARCH_RESULT.getMessage());
        return res.getItems().stream().map(this::mapLocalToDto).collect(Collectors.toList());
    }

    private SearchLocalRequest buildLocalRequest(String query, int per, int page) {
        var req = new SearchLocalRequest();
        req.setQuery(query);
        req.setDisplay(per);
        req.setPage(page);
        return req;
    }

    private SearchLocalResponse callLocalSearch(SearchLocalRequest req) {
        return naverClient.searchLocal(req);
    }

    private boolean noLocalResults(SearchLocalResponse res) {
        return res == null || res.getTotal() <= 0 || res.getItems() == null || res.getItems().isEmpty();
    }

    private TasteMapDto mapLocalToDto(SearchLocalItem item) {
        var dto = new TasteMapDto();
        dto.setTitle(item.getTitle());
        dto.setCategory(item.getCategory());
        dto.setAddress(item.getAddress());
        dto.setRoadAddress(item.getRoadAddress());
        try {
            var link = fetchImageLink(item.getTitle().replaceAll("<[^>]*>", ""));
            if (link != null) dto.setImageLink(link);
        } catch (Exception ignored) { }
        return dto;
    }

    private String fetchImageLink(String title) {
        if (isBlank(title)) return null;
        var req = new SearchImageRequest(); req.setQuery(title); req.setDisplay(1);
        var res = naverClient.searchImage(req);
        if (res == null || res.getTotal() <= 0 || res.getItems() == null || res.getItems().isEmpty()) return null;
        return res.getItems().get(0).getLink();
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

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
