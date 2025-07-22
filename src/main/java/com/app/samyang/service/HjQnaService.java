package com.app.samyang.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.samyang.entity.HjQna;
import com.app.samyang.repository.HjQnaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor			//생성자 주입
public class HjQnaService {
	private final HjQnaRepository hjqnaRepository;
	
	public List<HjQna> getAllQna() {	//전체 글 목록
		return hjqnaRepository.findTop3ByOrderByResdateDesc();
	}
	
	public HjQna findByNo(Long no) {	//답변글 상세보기
		return hjqnaRepository.findById(no).orElse(null);
	}
	
	public HjQna save(HjQna qna) {	//글 추가
		HjQna q = hjqnaRepository.save(qna);
		if(q.getLevel() == 1) {
			q.setParno(q.getNo());
		}
		return hjqnaRepository.save(qna);
	}
	
	public HjQna update(HjQna qna) {	//글 수정
		return hjqnaRepository.save(qna);
	}
	
	public void delete(Long no) {	//답변 글만 삭제
		hjqnaRepository.deleteById(no);
	}
	
    @Transactional
    public void deleteByParno(Long parno) {
        hjqnaRepository.deleteByParno(parno);
    }
	
	public List<HjQna> findReplies(Long parno){	//질문 글 상세보기 -> 해당 답글도 같이 가져옴
		return hjqnaRepository.findByParno(parno);
	}

	public void increaseHits(Long no) {
		HjQna qna = findByNo(no);
		qna.setHits(qna.getHits() + 1);
		hjqnaRepository.save(qna);
	}
	
	public List<HjQna> getLatestQnas() {
	    return hjqnaRepository.findTop3ByOrderByResdateDesc();
	}
	
	public Page<HjQna> getAllQna(Pageable pageable) {
		return hjqnaRepository.findAll(PageRequest.of(
			pageable.getPageNumber(),
			pageable.getPageSize(),
			Sort.by(Sort.Direction.DESC, "no")  // 최신순 정렬
		));
	}
	
	public Page<HjQna> getAllQuestions(Pageable pageable) {
	    return hjqnaRepository.findByLevelOrderByParnoDescNoAsc(1, pageable);
	}
	
	public Page<HjQna> getQnaList(Pageable pageable) {
	    return hjqnaRepository.findByLevelOrderByParnoDescNoAsc(1, pageable);
	}
}

