package com.example.TasteMap.api;

import com.example.TasteMap.api.dto.image.SearchImageRequest;
import com.example.TasteMap.api.dto.local.SearchLocalRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NaverClientTest {

    @Autowired
    private NaverClient naverClient;

    @Test
    public void searchLocalTest() {
        var search = new SearchLocalRequest();
        search.setQuery("감자탕");

        var result = naverClient.searchLocal(search);
        System.out.println("검색 결과 : " + result);
    }
}