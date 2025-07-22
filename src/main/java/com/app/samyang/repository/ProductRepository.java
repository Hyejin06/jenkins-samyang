package com.app.samyang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.samyang.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{ 
	List<Product> findTop3ByOrderByResdateDesc();
	List<Product> findByCate(String cate);
}