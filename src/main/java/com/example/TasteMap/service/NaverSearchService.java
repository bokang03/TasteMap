package com.example.TasteMap.service;

import com.example.TasteMap.api.NaverClient;
import com.example.TasteMap.api.dto.image.SearchImageRequest;
import com.example.TasteMap.api.dto.image.SearchImageResponse;
import com.example.TasteMap.api.dto.local.SearchLocalRequest;
import com.example.TasteMap.api.dto.local.SearchLocalResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class NaverSearchService {

    private final NaverClient naverClient;
    private final ObjectMapper objectMapper;

    public List<?> safeGetItems(SearchLocalResponse resp) {
        return resp != null && resp.getItems() != null ? resp.getItems() : Collections.emptyList();
    }

    public void collectFromItems(List<?> items, List<Map<String, Object>> results, int desired) {
        for (Object item : items) {
            if (results.size() >= desired) break;
            Map<String, Object> map = objectMapper.convertValue(item, new TypeReference<Map<String, Object>>() {});
            attachImageLink(map);
            results.add(map);
        }
    }

    public void attachImageLink(Map<String, Object> itemMap) {
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
            // 예외 무시: 기본 이미지로 대체
        }
        itemMap.put("imageLink", "https://via.placeholder.com/600x300?text=NO+IMAGE");
    }

    private String stripHtml(String s) {
        return s == null ? "" : s.replaceAll("\\<.*?\\>", "").trim();
    }

    public List<Map<String, Object>> searchLocalMaps(String query, int page, int desired) {
        var req = new SearchLocalRequest();
        req.setQuery(query);
        req.setPage(page);
        SearchLocalResponse resp = naverClient.searchLocal(req);

        List<?> items = safeGetItems(resp);
        List<Map<String, Object>> results = new ArrayList<>();

        collectFromItems(items, results, desired);

        int nextStart = req.getStart() + req.getDisplay();
        while (results.size() < desired) {
            SearchLocalRequest nextReq = new SearchLocalRequest();
            nextReq.setQuery(query);
            nextReq.setStart(nextStart);
            nextReq.setDisplay(desired - results.size());
            SearchLocalResponse nextResp = naverClient.searchLocal(nextReq);
            List<?> nextItems = safeGetItems(nextResp);
            if (nextItems.isEmpty()) break;
            collectFromItems(nextItems, results, desired);
            nextStart += nextReq.getDisplay();
        }

        return results;
    }
}
