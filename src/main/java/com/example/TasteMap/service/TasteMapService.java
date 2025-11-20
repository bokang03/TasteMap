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

    public List<TasteMapDto> search(String query, int startPage){
        final int MAX_RESULTS = 100;
        final int PER_REQUEST = 5; // 네이버가 실제로 5개만 반환하는 경우에 맞춤
        var results = new java.util.ArrayList<TasteMapDto>();
        int page = Math.max(1, startPage);

        while (results.size() < MAX_RESULTS) {
            var req = new SearchLocalRequest();
            req.setQuery(query);
            req.setDisplay(PER_REQUEST);
            req.setPage(page);

            var res = naverClient.searchLocal(req);
            if (res == null || res.getTotal() <= 0 || res.getItems() == null || res.getItems().isEmpty()) break;

            for (var localItem : res.getItems()) {
                var dto = new TasteMapDto();
                dto.setTitle(localItem.getTitle());
                dto.setCategory(localItem.getCategory());
                dto.setAddress(localItem.getAddress());
                dto.setRoadAddress(localItem.getRoadAddress());

                try {
                    var imageQuery = localItem.getTitle().replaceAll("<[^>]*>", "");
                    var imgReq = new SearchImageRequest();
                    imgReq.setQuery(imageQuery);
                    imgReq.setDisplay(1);

                    var imgRes = naverClient.searchImage(imgReq);
                    if (imgRes != null && imgRes.getTotal() > 0 && imgRes.getItems() != null && !imgRes.getItems().isEmpty()) {
                        dto.setImageLink(imgRes.getItems().get(0).getLink());
                    }
                } catch (Exception ignored) { }

                results.add(dto);
                if (results.size() >= MAX_RESULTS) break;
            }

            if (res.getItems().size() < PER_REQUEST) break; // 마지막 페이지 도달
            if (results.size() >= res.getTotal()) break; // 전체 결과 수 도달
            page++;
        }

        return results;
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
