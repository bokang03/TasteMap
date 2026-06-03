package com.example.TasteMap.api;

import com.example.TasteMap.api.dto.image.SearchImageRequest;
import com.example.TasteMap.api.dto.local.SearchLocalRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NaverClientTest {

    @Test
    void searchLocalRequestContainsExpectedQueryParams() {
        var search = new SearchLocalRequest();
        search.setQuery("gamjatang");
        search.setDisplay(10);
        search.setStart(2);

        var query = search.getQuery();

        assertThat(query.getFirst("query")).isEqualTo("gamjatang");
        assertThat(query.getFirst("display")).isEqualTo("10");
        assertThat(query.getFirst("start")).isEqualTo("2");
        assertThat(query.getFirst("sort")).isEqualTo("random");
    }

    @Test
    void searchImageRequestContainsExpectedQueryParams() {
        var search = new SearchImageRequest();
        search.setQuery("gamjatang");
        search.setDisplay(1);

        var query = search.getQuery();

        assertThat(query.getFirst("query")).isEqualTo("gamjatang");
        assertThat(query.getFirst("display")).isEqualTo("1");
        assertThat(query.getFirst("start")).isEqualTo("1");
        assertThat(query.getFirst("sort")).isEqualTo("sim");
        assertThat(query.getFirst("filter")).isEqualTo("all");
    }
}
