package com.example.TasteMap.controller;

import com.example.TasteMap.domain.TasteMapDto;
import com.example.TasteMap.service.TasteMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/taste-map")
@RequiredArgsConstructor
public class TasteMapController {

    private final TasteMapService tasteMapService;

    @GetMapping("/search")
    public TasteMapDto search(@RequestParam String query){
        return tasteMapService.search(query);
    }
}
