package com.example.TasteMap.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "taste_map")
public class TasteMapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String title;

    @Column
    private String category;

    @Column
    private String address;

    @Column
    private String roadAddress;

    @Column
    private String imageLink;

    public TasteMapEntity(String title, String category, String address, String roadAddress, String imageLink) {
        this.title = title;
        this.category = category;
        this.address = address;
        this.roadAddress = roadAddress;
        this.imageLink = imageLink;
    }
}
