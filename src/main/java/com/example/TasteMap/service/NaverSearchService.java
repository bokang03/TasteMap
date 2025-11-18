package com.example.TasteMap.service;

import com.example.TasteMap.api.NaverClient;
import com.example.TasteMap.api.dto.image.SearchImageRequest;
import com.example.TasteMap.api.dto.image.SearchImageResponse;
import com.example.TasteMap.api.dto.local.SearchLocalRequest;
import com.example.TasteMap.api.dto.local.SearchLocalResponse;
import com.example.TasteMap.exception.ErrorMessage;
import com.example.TasteMap.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NaverSearchService {

    private final NaverClient naverClient;
    private final ObjectMapper objectMapper;
    private static final String PLACEHOLDER = "https://via.placeholder.com/600x300?text=NO+IMAGE";

    public List<?> safeGetItems(SearchLocalResponse resp) {
        if (resp == null) {
            throw new ResourceNotFoundException(ErrorMessage.NO_SEARCH_RESULT.getMessage());
        }
        var items = resp.getItems();
        if (items == null || items.isEmpty()) {
            throw new ResourceNotFoundException(ErrorMessage.NO_SEARCH_RESULT.getMessage());
        }
        return items;
    }

    public Map<String, Object> convertToMap(Object item) {
        return objectMapper.convertValue(item, new TypeReference<Map<String, Object>>() {});
    }

    public String resolveTitle(Map<String, Object> itemMap) {
        Object titleObj = itemMap.get("title");
        if (titleObj == null) return "";
        return stripHtml(titleObj.toString());
    }

    public String fetchImageLinkForTitle(String title) {
        if (title == null || title.isBlank()) return null;
        try {
            SearchImageRequest req = new SearchImageRequest();
            req.setQuery(title);
            req.setDisplay(1);
            SearchImageResponse resp = naverClient.searchImage(req);
            if (resp == null) return null;
            var items = resp.getItems();
            if (items == null || items.isEmpty()) return null;
            var imgItem = items.get(0);
            if (imgItem.getLink() != null) return imgItem.getLink();
            return imgItem.getThumbnail();
        } catch (Exception e) {
            return null;
        }
    }

    public void attachImageLink(Map<String, Object> itemMap) {
        String title = resolveTitle(itemMap);
        String link = fetchImageLinkForTitle(title);
        if (link == null) {
            itemMap.put("imageLink", PLACEHOLDER);
            return;
        }
        itemMap.put("imageLink", link);
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
        if (s == null) return "";
        return s.replaceAll("\\<.*?\\>", "").trim();
    }

    public List<Map<String, Object>> searchLocalMaps(String query, int page, int desired) {
        var req = createLocalRequest(query, page);
        List<Map<String, Object>> results = new ArrayList<>();
        boolean hasAny = fetchAndCollect(req, results, desired);
        if (!hasAny) return results;

        int nextStart = req.getStart() + req.getDisplay();
        while (results.size() < desired) {
            var nextReq = createLocalRequestWithStart(query, nextStart, desired - results.size());
            boolean ok = fetchAndCollect(nextReq, results, desired);
            if (!ok) break;
            nextStart += nextReq.getDisplay();
        }
        return results;
    }

    private SearchLocalRequest createLocalRequest(String query, int page){
        var req = new SearchLocalRequest();
        req.setQuery(query);
        req.setPage(page);
        return req;
    }

    private SearchLocalRequest createLocalRequestWithStart(String query, int start, int display){
        var req = new SearchLocalRequest();
        req.setQuery(query);
        req.setStart(start);
        req.setDisplay(display);
        return req;
    }

    private boolean fetchAndCollect(SearchLocalRequest req, List<Map<String, Object>> results, int desired) {
        SearchLocalResponse resp = naverClient.searchLocal(req);
        List<?> items = safeGetItems(resp);
        if (items == null || items.isEmpty()) return false;
        collectFromItems(items, results, desired);
        return true;
    }
}
