package com.app.samyang.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.app.samyang.entity.Qna;
import com.app.samyang.repository.QnaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // 생성자 주입
public class QnaService {
	private final QnaRepository qnaRepository;

	public List<Qna> getAllQna() { // 전체 글 목록
		return qnaRepository.findAllByOrderByParnoDescNoAsc();
	}

	public Qna findByNo(Long no) { // 답변글 상세보기
		return qnaRepository.findById(no).orElse(null);
	}

	public Qna save(Qna qna) {
		if (qna.getLevel() == 1) {
			// 일단 저장하여 ID(no) 생성
			Qna saved = qnaRepository.save(qna);
			saved.setParno(saved.getNo()); // 자기 자신을 부모로 지정
			return qnaRepository.save(saved); // 다시 저장하여 parno 반영
		} else {
			// 답변일 경우 그냥 저장
			return qnaRepository.save(qna);
		}
	}
	
	public List<Qna> findRepliesAll() {
	    return qnaRepository.findByLevel(2);
	}

	public Qna update(Qna qna) { // 글 수정
		return qnaRepository.save(qna);
	}

	public void delete(Long no) { // 답변 글만 삭제
		qnaRepository.deleteById(no);
	}

	public void deleteByParno(Long parno) { // 질문 삭제시 해당 답변도 연쇄삭제
		qnaRepository.deleteByParno(parno);
	}

	public List<Qna> findReplies(Long parno) { // 질문 글 상세보기 -> 해당 답글도 같이 가져옴
		return qnaRepository.findByParno(parno);
	}

	public void increaseHits(Long no) {
		Qna qna = findByNo(no);
		qna.setHits(qna.getHits() + 1);
		qnaRepository.save(qna);
	}

	public List<Qna> findByCategory(String category) {
		return qnaRepository.findAllByCategory(category);
	}
	
	public Page<Qna> getQnaList(Pageable pageable) {
        return qnaRepository.findAll(pageable);
    }
	
	public Page<Qna> findByCategoryPaged(String category, Pageable pageable) {
	    return qnaRepository.findByCategory(category, pageable);
	}
	
	public List<Qna> findByCategoryAndLevel(String category, int level) {
		return qnaRepository.findByCategoryAndLevel(category, level);
	}
}
