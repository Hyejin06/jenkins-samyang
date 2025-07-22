package com.app.samyang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.samyang.entity.JyProduct;

public interface JyProductRepository extends JpaRepository<JyProduct, Long> {
	List<JyProduct> findTop4ByOrderByNoDesc();
	List<JyProduct> findTop4ByCateOrderByNoDesc(String cate);
}
