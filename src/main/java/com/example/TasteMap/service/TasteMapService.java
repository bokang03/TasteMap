package com.example.TasteMap.service;

import com.example.TasteMap.api.NaverClient;
import com.example.TasteMap.api.dto.image.SearchImageRequest;
import com.example.TasteMap.api.dto.local.SearchLocalRequest;
import com.example.TasteMap.domain.TasteMapDto;
import com.example.TasteMap.repository.TasteMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TasteMapService {

    private final NaverClient naverClient;
    private final TasteMapRepository tasteMapRepository;

    public TasteMapDto search(String query){

        var searchLocalRequest = new SearchLocalRequest();
        searchLocalRequest.setQuery(query);
        var searchLocalResponse = naverClient.searchLocal(searchLocalRequest);

        int total = 0;
        try {
            total = Integer.parseInt(searchLocalResponse.getTotal());
        } catch (NumberFormatException e) {
            total = 0;
        }

        if (total > 0) {
            var localItem = searchLocalResponse.getItems().stream().findFirst().get();
            var imageQuery = localItem.getTitle().replaceAll("<[^>]*>", "");
            var searchImageRequest = new SearchImageRequest();
            searchImageRequest.setQuery(imageQuery);

            var searchImageResponse = naverClient.searchImage(searchImageRequest);

            if(searchImageResponse.getTotal() > 0){
                var imageItem = searchImageResponse.getItems().stream().findFirst().get();

                var result = new TasteMapDto();
                result.setTitle(localItem.getTitle());
                result.setCategory(localItem.getCategory());
                result.setAddress(localItem.getAddress());
                result.setRoadAddress(localItem.getRoadAddress());
                result.setHomePageLink(localItem.getLink());
                result.setImageLink(imageItem.getLink());
                return result;
            }
        }

        return new TasteMapDto();
    }

    public void add(TasteMapDto dto){
    }

    public void findAll(){
    }

    public void findById(int id){
    }

    public void delete(int id){
    }
}