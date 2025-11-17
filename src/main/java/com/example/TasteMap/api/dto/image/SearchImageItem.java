package com.example.TasteMap.api.dto.image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchImageItem {
    private String title;
    private String link;
    private String thumbnail;
    private String sizeheight;
    private String sizewidth;
}
