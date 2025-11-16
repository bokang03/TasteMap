package com.example.TasteMap.controller;

import com.example.TasteMap.domain.TasteMapDto;
import com.example.TasteMap.service.TasteMapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/taste-map")
@RequiredArgsConstructor
public class TasteMapController {

    private final TasteMapService tasteMapService;

    @GetMapping("/search")
    public TasteMapDto search(@RequestParam String query){
        return tasteMapService.search(query);
    }

    @PostMapping("")
    public TasteMapDto add(@RequestBody TasteMapDto tasteMapDto){
        log.info("{}", tasteMapDto);
        return tasteMapService.add(tasteMapDto);
    }

    @GetMapping("/all")
    public List<TasteMapDto> findAll(){
        return tasteMapService.findAll();
    }
}
