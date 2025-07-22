package com.app.samyang.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.samyang.entity.HjNotice;
import com.app.samyang.entity.HjQna;

public interface HjQnaRepository extends JpaRepository<HjQna, Long>{
	List<HjQna> findByLevel(int level);
	List<HjQna> findByParno(Long parno);
	List<HjQna> findTop3ByOrderByResdateDesc();
	List<HjQna> deleteByParno(Long parno);
	List<HjQna> findTop3ByLevelOrderByResdateDesc(int level);
	Page<HjQna> findAllByOrderByParnoDescNoAsc(Pageable pageable);
	Page<HjQna> findByLevelOrderByParnoDescNoAsc(int level, Pageable pageable);
}