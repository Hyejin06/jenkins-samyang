package com.app.samyang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.app.samyang.entity.JyReview;

public interface JyReviewRepository extends JpaRepository<JyReview, Long> {

    List<JyReview> findByProductNo(Long productNo);

    @Query("SELECT r FROM JyReview r JOIN FETCH r.product")
    List<JyReview> findAllWithProduct();  // ✅ 전체 리뷰 + 상품 함께
}
