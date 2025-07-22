package com.app.samyang.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.samyang.entity.Product;
import com.app.samyang.entity.Review;
import com.app.samyang.repository.ProductRepository;
import com.app.samyang.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ProductRepository productRepo;
    private final ReviewRepository reviewRepo;

    // 리뷰 등록
    public void save(Review review) {
        reviewRepo.save(review);

        Product product = review.getProduct();
        if (product != null) {
            List<Review> reviews = reviewRepo.findByProductNo(product.getNo());

            int totalRating = reviews.stream().mapToInt(Review::getRating).sum();
            int reviewCount = reviews.size();
            double avgRating = reviewCount == 0 ? 0 : (double) totalRating / reviewCount;

            product.setReviewAvg(avgRating);        // ✅ Product 엔티티 기준
            product.setReviewCount(reviewCount);

            productRepo.save(product); // 평균 별점과 수 갱신
        }
    }

    // 특정 상품 리뷰 조회
    public List<Review> findByProduct(Long productNo) {
        return reviewRepo.findByProductNo(productNo);
    }

    // 전체 리뷰 조회 (상품 정보 포함)
    public List<Review> findAll() {
        return reviewRepo.findAllWithProduct();
    }

    public Review findById(Long id) {
        return reviewRepo.findById(id).orElse(null);
    }

    public void delete(Long id) {
        Review review = reviewRepo.findById(id).orElse(null);

        if (review != null) {
            Product product = review.getProduct();
            reviewRepo.deleteById(id); // 먼저 리뷰 삭제

            if (product != null) {
                List<Review> reviews = reviewRepo.findByProductNo(product.getNo());

                int reviewCount = reviews.size();
                double avgRating = reviewCount == 0 ? 0 : 
                    (double) reviews.stream().mapToInt(Review::getRating).sum() / reviewCount;

                product.setReviewAvg(avgRating);
                product.setReviewCount(reviewCount);
                productRepo.save(product);
            }
        }
    }
}
