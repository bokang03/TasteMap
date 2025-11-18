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

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class NaverSearchService {

    private final NaverClient naverClient;
    private final ObjectMapper objectMapper;
    private static final String PLACEHOLDER = "https://via.placeholder.com/600x300?text=NO+IMAGE";

    public List<?> safeGetItems(SearchLocalResponse resp) {
        if (resp == null || resp.getItems() == null || resp.getItems().isEmpty()) {
            throw new com.example.TasteMap.exception.ResourceNotFoundException(
                    com.example.TasteMap.exception.ErrorMessage.NO_SEARCH_RESULT.getMessage()
            );
        }
        return resp.getItems();
    }

    public Map<String, Object> convertToMap(Object item) {
        return objectMapper.convertValue(item, new TypeReference<Map<String, Object>>() {});
    }

    public String resolveTitle(Map<String, Object> itemMap) {
        Object titleObj = itemMap.get("title");
        return titleObj != null ? stripHtml(titleObj.toString()) : "";
    }

    public String fetchImageLinkForTitle(String title) {
        if (title == null || title.isBlank()) return null;
        try {
            SearchImageRequest imgReq = new SearchImageRequest();
            imgReq.setQuery(title);
            imgReq.setDisplay(1);
            SearchImageResponse imgResp = naverClient.searchImage(imgReq);
            if (imgResp != null && imgResp.getItems() != null && !imgResp.getItems().isEmpty()) {
                var imgItem = imgResp.getItems().get(0);
                return imgItem.getLink() != null ? imgItem.getLink() : imgItem.getThumbnail();
            }
        } catch (Exception e) {
            // 무시하고 null 반환
        }
        return null;
    }

    public void attachImageLink(Map<String, Object> itemMap) {
        String title = resolveTitle(itemMap);
        String link = fetchImageLinkForTitle(title);
        if (link != null) {
            itemMap.put("imageLink", link);
        } else {
            itemMap.put("imageLink", PLACEHOLDER);
        }
    }

    public void collectFromItems(List<?> items, List<Map<String, Object>> results, int desired) {
        for (Object item : items) {
            if (results.size() >= desired) break;
            Map<String, Object> map = convertToMap(item);
            attachImageLink(map);
            results.add(map);
        }
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
