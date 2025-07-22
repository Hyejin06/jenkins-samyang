package com.app.samyang.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.samyang.entity.JyMember;
import com.app.samyang.repository.JyMemberRepository;
//기능 구현은 서비스에서 
@Service
public class JyMemberService {
   private final JyMemberRepository jyMemberRepository;
   private final BCryptPasswordEncoder bCryptPasswordEncoder; 
   
   public JyMemberService(JyMemberRepository jyMemberRepository, 
         BCryptPasswordEncoder bCryptPasswordEncoder) {
      this.jyMemberRepository = jyMemberRepository;
      this.bCryptPasswordEncoder = bCryptPasswordEncoder;
   }
   
   public JyMember join(JyMember jyMember) {
	    jyMember.setPw(bCryptPasswordEncoder.encode(jyMember.getPw()));

	    if (jyMember.getGrade() == null || jyMember.getGrade().isBlank()) {
	        jyMember.setGrade("family"); // 기본 등급
	    }

	    return jyMemberRepository.save(jyMember);
	}
   
   public JyMember login(String id, String pw) { //로그인 - 아이디와 비밀번호가 일치하는 회원 정보를 반환
      return jyMemberRepository.findById(id)
            .filter(m -> bCryptPasswordEncoder.matches(pw, m.getPw()))
            .orElse(null);
   }
   
    public JyMember getMember(String id) {   //마이페이지 회원 정보
        return jyMemberRepository.findById(id).orElse(null);
    }

    public JyMember getMemberByNo(Long no) {   //회원 번호로 검색
        return jyMemberRepository.findByNo(no).orElse(null);
    }
    
    public JyMember update(JyMember jyMember) {
        return jyMemberRepository.save(jyMember); // 암호화는 Controller에서 이미 했으므로 그대로 저장
    }

    public void updatePassword(JyMember jyMember) {   //비밀번호 변경
        jyMember.setPw(bCryptPasswordEncoder.encode(jyMember.getPw()));
        jyMemberRepository.save(jyMember);
    }
    
    public void delete(Long no) {   //회원 탈퇴
        jyMemberRepository.deleteById(no);
    }

    public List<JyMember> getMemberList() {   //관리자 회원 목록
        return jyMemberRepository.findAll();
    }
    
    public boolean idCheck(String id) {
       return jyMemberRepository.existsById(id);
    }

public boolean emailCheck(String email) {
      return jyMemberRepository.existsByEmail(email);
   }
   
}
