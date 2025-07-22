package com.app.samyang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.samyang.entity.HjProduct;

public interface HjProductRepository extends JpaRepository<HjProduct, Long> {
	List<HjProduct> findByCate(String cate);
}
