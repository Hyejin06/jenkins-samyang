package com.app.samyang.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JyProduct {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    private String cate;
    private String pname;
    private int price;
    private int rating;
    private int reviewCount;
    private String img1;
}
