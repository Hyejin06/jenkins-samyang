package com.app.samyang.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.samyang.entity.Qna;

public interface QnaRepository extends JpaRepository<Qna, Long>{
	List<Qna> findByLevel(int level);
	List<Qna> findByParno(Long parno);
	List<Qna> findAllByOrderByParnoDescNoAsc();
	List<Qna> deleteByParno(Long parno);
	List<Qna> findTop3ByLevelOrderByResdateDesc(int level); 
	List<Qna> findAllByCategory(String category);
	Page<Qna> findByCategory(String category, Pageable pageable);
	List<Qna> findByCategoryAndLevel(String category, int level);
}