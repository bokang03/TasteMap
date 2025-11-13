package com.example.TasteMap.api.dto.image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchImageResponse {

    private String lastBuildDate; // 검색 결과를 생성한 시간
    private int total; // 검색 결과 문서의 총 개수
    private int start; // 검색 결과 문서 중, 문서의 시작 위치
    private int display; // 검색된 문서의 개수
    private List<SearchImageItem> items; // 검색 결과 문서 리스트
}
