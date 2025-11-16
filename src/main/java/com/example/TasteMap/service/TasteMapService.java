package com.example.TasteMap.service;

import com.example.TasteMap.api.NaverClient;
import com.example.TasteMap.api.dto.image.SearchImageRequest;
import com.example.TasteMap.api.dto.local.SearchLocalRequest;
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
        // 지역검색
        var searchLocalReqest = new SearchLocalRequest();
        searchLocalReqest.setQuery(query);
        var searchLocalResponse = naverClient.searchLocal(searchLocalReqest);

        int total = 0;
        try {
            total = Integer.parseInt(searchLocalResponse.getTotal());
        } catch (NumberFormatException e) {
            total = 0;
        }

        if (total > 0) {
            var localItem = searchLocalResponse.getItems().stream().findFirst().get();
            var imageQuery = localItem.getTitle().replaceAll("<[^>]*>", "");
            var searchImageRequest = new SearchImageRequest();
            searchImageRequest.setQuery(imageQuery);

            // 이미지 검색
            var searchImageResponse = naverClient.searchImage(searchImageRequest);

            if(searchImageResponse.getTotal() > 0){
                var imageItem = searchImageResponse.getItems().stream().findFirst().get();

                // 결과를 리턴
                var result = new TasteMapDto();
                result.setTitle(localItem.getTitle());
                result.setCategory(localItem.getCategory());
                result.setAddress(localItem.getAddress());
                result.setRoadAddress(localItem.getRoadAddress());
                result.setHomePageLink(localItem.getLink());
                result.setImageLink(imageItem.getLink());
                return result;
            }
        }

        return new TasteMapDto();
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