package com.app.samyang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.samyang.entity.JyQna;

public interface JyQnaRepository extends JpaRepository<JyQna, Long>{
	List<JyQna> findByLevel(int level);
	List<JyQna> findByParno(Long parno);
	List<JyQna> findAllByOrderByParnoDescNoAsc();
	List<JyQna> deleteByParno(Long parno);
	List<JyQna> findTop3ByLevelOrderByResdateDesc(int level); 
}