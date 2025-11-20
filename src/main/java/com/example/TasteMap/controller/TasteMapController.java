package com.example.TasteMap.controller;

import com.example.TasteMap.api.NaverClient;
import com.example.TasteMap.domain.TasteMapDto;
import com.example.TasteMap.service.TasteMapService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/taste-map")
public class TasteMapController {

    private final NaverClient naverClient;
    private final TasteMapService tasteMapService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public TasteMapController(NaverClient naverClient, TasteMapService tasteMapService) {
        this.naverClient = naverClient;
        this.tasteMapService = tasteMapService;
    }

    @GetMapping("/search")
    public List<TasteMapDto> search(@RequestParam String query, @RequestParam(required = false, defaultValue = "1") int page){
        return tasteMapService.search(query, page);
    }

    @PostMapping
    public ResponseEntity<TasteMapDto> add(@RequestBody TasteMapDto dto) {
        var saved = tasteMapService.add(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<TasteMapDto>> findAll() {
        return ResponseEntity.ok(tasteMapService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        tasteMapService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
