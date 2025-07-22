package com.app.samyang.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.samyang.entity.JyQna;
import com.app.samyang.repository.JyQnaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor			//생성자 주입
public class JyQnaService {
	private final JyQnaRepository jyQnaRepository;
	
	public List<JyQna> getAllQna() {	//전체 글 목록
		return jyQnaRepository.findAllByOrderByParnoDescNoAsc();
	}
	
	public JyQna findByNo(Long no) {	//답변글 상세보기
		return jyQnaRepository.findById(no).orElse(null);
	}
	
	public JyQna save(JyQna jyQna) {	//글 추가
		JyQna q = jyQnaRepository.save(jyQna);
		if(q.getLevel() == 1) {
			q.setParno(q.getNo());
		}
		return jyQnaRepository.save(jyQna);
	}
	
	public JyQna update(JyQna jyQna) {	//글 수정
		return jyQnaRepository.save(jyQna);
	}
	
	public void delete(Long no) {	//답변 글만 삭제
		jyQnaRepository.deleteById(no);
	}
	
	public void deleteByParno(Long parno) {  //질문 삭제시 해당 답변도 연쇄삭제
		jyQnaRepository.deleteByParno(parno);
	}
	
	public List<JyQna> findReplies(Long parno){	//질문 글 상세보기 -> 해당 답글도 같이 가져옴
		return jyQnaRepository.findByParno(parno);
	}

	public void increaseHits(Long no) {
		JyQna jyQna = findByNo(no);
		jyQna.setHits(jyQna.getHits() + 1);
		jyQnaRepository.save(jyQna);
	}
}

