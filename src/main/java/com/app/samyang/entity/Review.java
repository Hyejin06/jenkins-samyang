package com.app.samyang.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating; // 별점 1~5

    @Column(length = 1000)
    private String content; // 리뷰 내용

    private String writer; // 작성자 ID (세션 기반)

    @ManyToOne
    @JoinColumn(name = "product_no") // Product의 no와 매핑
    private Product product;

    private LocalDateTime resdate;

    @PrePersist
    public void onCreate() {
        resdate = LocalDateTime.now();
    }
}
