package com.example.TasteMap.controller;

import com.example.TasteMap.api.NaverClient;
import com.example.TasteMap.api.dto.image.SearchImageRequest;
import com.example.TasteMap.api.dto.image.SearchImageResponse;
import com.example.TasteMap.api.dto.local.SearchLocalRequest;
import com.example.TasteMap.api.dto.local.SearchLocalResponse;
import com.example.TasteMap.domain.TasteMapDto;
import com.example.TasteMap.service.TasteMapService;
import com.fasterxml.jackson.core.type.TypeReference;
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
    public ResponseEntity<List<?>> searchLocal(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "1") int page) {

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        final int desired = 6;

        var req = new SearchLocalRequest();
        req.setQuery(query);
        req.setPage(page);
        SearchLocalResponse resp = naverClient.searchLocal(req);

        List<?> items = resp != null && resp.getItems() != null ? resp.getItems() : Collections.emptyList();
        List<Map<String, Object>> results = new ArrayList<>();

        for (Object item : items) {
            if (results.size() >= desired) break;
            Map<String, Object> map = objectMapper.convertValue(item, new TypeReference<Map<String, Object>>() {});
            attachImageLink(map);
            results.add(map);
        }

        int nextStart = req.getStart() + req.getDisplay();
        while (results.size() < desired) {
            SearchLocalRequest nextReq = new SearchLocalRequest();
            nextReq.setQuery(query);
            nextReq.setStart(nextStart);
            nextReq.setDisplay(desired - results.size());
            SearchLocalResponse nextResp = naverClient.searchLocal(nextReq);
            if (nextResp == null || nextResp.getItems() == null || nextResp.getItems().isEmpty()) break;
            for (Object item : nextResp.getItems()) {
                if (results.size() >= desired) break;
                Map<String, Object> map = objectMapper.convertValue(item, new TypeReference<Map<String, Object>>() {});
                attachImageLink(map);
                results.add(map);
            }
            nextStart += nextReq.getDisplay();
        }

        return ResponseEntity.ok(results);
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

    private void attachImageLink(Map<String, Object> itemMap) {
        try {
            Object titleObj = itemMap.get("title");
            String title = titleObj != null ? stripHtml(titleObj.toString()) : "";
            if (!title.isBlank()) {
                SearchImageRequest imgReq = new SearchImageRequest();
                imgReq.setQuery(title);
                imgReq.setDisplay(1);
                SearchImageResponse imgResp = naverClient.searchImage(imgReq);
                if (imgResp != null && imgResp.getItems() != null && !imgResp.getItems().isEmpty()) {
                    var imgItem = imgResp.getItems().get(0);
                    itemMap.put("imageLink", imgItem.getLink() != null ? imgItem.getLink() : imgItem.getThumbnail());
                    return;
                }
            }
        } catch (Exception e) {
            // 무시
        }
        itemMap.put("imageLink", "https://via.placeholder.com/600x300?text=NO+IMAGE");
    }

    private String stripHtml(String s) {
        return s == null ? "" : s.replaceAll("\\<.*?\\>", "").trim();
    }
}
