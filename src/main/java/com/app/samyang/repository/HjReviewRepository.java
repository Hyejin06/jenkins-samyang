package com.app.samyang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.samyang.entity.HjReview;

public interface HjReviewRepository extends JpaRepository<HjReview, Long> {

    // ✅ product.no를 기준으로 리뷰 목록 조회
    @Query("SELECT r FROM HjReview r WHERE r.product.no = :productNo")
    List<HjReview> findByProductNo(@Param("productNo") Long productNo);

    // ✅ 리뷰 + 상품 정보를 한번에 로딩 (JOIN FETCH)
    @Query("SELECT r FROM HjReview r JOIN FETCH r.product")
    List<HjReview> findAllWithProduct();
}