package com.example.TasteMap.api.dto.local;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchLocalItem {
    private String title; // 업체명
    private String link; // 업체 상세 URL
    private String category; // 업체 분류
    private String description; // 업체 설명
    private String telephone; // 업체 전화번호
    private String address; // 업체 주소
    private String roadAddress; // 업체 도로명 주소
    private String mapx; // 업체 X 좌표
    private String mapy; // 업체 Y 좌표


}

