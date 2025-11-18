package com.example.TasteMap.service;

import com.example.TasteMap.api.NaverClient;
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
