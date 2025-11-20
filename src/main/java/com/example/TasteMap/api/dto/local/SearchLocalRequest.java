package com.example.TasteMap.api.dto.local;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Data
@NoArgsConstructor
public class SearchLocalRequest {
    private String query = "";
    private int display = 5;
    private int start = 1;
    private String sort = "random";

    public void setPage(int page) {
        if (page < 1) page = 1;
        this.start = (page - 1) * this.display + 1;
    }

    public MultiValueMap<String, String> getQuery() {
        var map = new LinkedMultiValueMap<String, String>();
        map.add("query", query);
        map.add("display", String.valueOf(display));
        map.add("start", String.valueOf(start));
        map.add("sort", sort);
        return map;
    }
}
