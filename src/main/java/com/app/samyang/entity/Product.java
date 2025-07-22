package com.app.samyang.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long no; // 상품 번호

	@Column(nullable = false, length = 100)
	private String cate; // 상품 카테고리

	@Column(nullable = false, length = 200)
	private String pname; // 상품명

	@Column(columnDefinition = "TEXT")
	private String comment; // 상품 설명

	@Column(nullable = false)
	private int price; // 판매가

	@Builder.Default
	private String img1 = "/images/noimg.jpg"; // 메인 이미지
	
	@Builder.Default
	private int amount = 0;

	@Builder.Default
	private int price1 = 0;

	@Builder.Default
	private int price2 = 0;

	@Builder.Default
	private LocalDateTime resdate = LocalDateTime.now(); // 등록 일시

	@Builder.Default
	private int reviewCount = 0;

	@Builder.Default
	private Double reviewAvg = 0.0;
}