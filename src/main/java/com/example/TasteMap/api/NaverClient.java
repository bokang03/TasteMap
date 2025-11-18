package com.example.TasteMap.api;

import com.example.TasteMap.api.dto.image.SearchImageRequest;
import com.example.TasteMap.api.dto.image.SearchImageResponse;
import com.example.TasteMap.api.dto.local.SearchLocalRequest;
import com.example.TasteMap.api.dto.local.SearchLocalResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class NaverClient {

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @Value("${naver.url.search.local}")
    private String searchLocalUrl;

    @Value("${naver.url.search.image}")
    private String searchImageUrl;

    public SearchLocalResponse searchLocal(SearchLocalRequest searchLocalRequest){
        var builder = UriComponentsBuilder.fromUriString(searchLocalUrl)
                .queryParams(searchLocalRequest.getQuery());
        return getForResponse(builder, new ParameterizedTypeReference<SearchLocalResponse>(){});
    }

    public SearchImageResponse searchImage(SearchImageRequest searchImageRequest){
        var builder = UriComponentsBuilder.fromUriString(searchImageUrl)
                .queryParams(searchImageRequest.getQuery());
        return getForResponse(builder, new ParameterizedTypeReference<SearchImageResponse>(){});
    }

    private HttpHeaders buildHeaders() {
        var headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", clientId);
        headers.add("X-Naver-Client-Secret", clientSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private <T> T getForResponse(UriComponentsBuilder builder, ParameterizedTypeReference<T> responseType) {
        var uri = builder.build().encode().toUri();
        var httpEntity = new HttpEntity<>(buildHeaders());
        var responseEntity = new RestTemplate().exchange(uri, HttpMethod.GET, httpEntity, responseType);
        return responseEntity.getBody();
    }
}
