package com.example.TasteMap.domain;

import com.example.TasteMap.db.MemoryDbEntity;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TasteMapEntity extends MemoryDbEntity {
    private String title;
    private String category;
    private String address;
    private String roadAddress;
    private String imageLink;
}
