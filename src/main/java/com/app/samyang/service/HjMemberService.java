package com.app.samyang.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.samyang.entity.HjMember;
import com.app.samyang.repository.HjMemberRepository;
//기능 구현은 서비스에서 
@Service
public class HjMemberService {
   private final HjMemberRepository hjMemberRepository;
   private final BCryptPasswordEncoder bCryptPasswordEncoder; 
   
   public HjMemberService(HjMemberRepository hjMemberRepository, 
         BCryptPasswordEncoder bCryptPasswordEncoder) {
      this.hjMemberRepository = hjMemberRepository;
      this.bCryptPasswordEncoder = bCryptPasswordEncoder;
   }
   
   public HjMember join(HjMember hjMember) {
	    hjMember.setPw(bCryptPasswordEncoder.encode(hjMember.getPw()));

	    if (hjMember.getGrade() == null || hjMember.getGrade().isBlank()) {
	        hjMember.setGrade("family"); // 기본 등급
	    }

	    return hjMemberRepository.save(hjMember);
	}
   
   public HjMember login(String id, String pw) { //로그인 - 아이디와 비밀번호가 일치하는 회원 정보를 반환
      return hjMemberRepository.findById(id)
            .filter(m -> bCryptPasswordEncoder.matches(pw, m.getPw()))
            .orElse(null);
   }
   
    public HjMember getMember(String id) {   //마이페이지 회원 정보
        return hjMemberRepository.findById(id).orElse(null);
    }

    public HjMember getMemberByNo(Long no) {   //회원 번호로 검색
        return hjMemberRepository.findByNo(no).orElse(null);
    }
    
    public HjMember update(HjMember hjMember) {
        return hjMemberRepository.save(hjMember); // 암호화는 Controller에서 이미 했으므로 그대로 저장
    }

    public void updatePassword(HjMember hjMember) {   //비밀번호 변경
        hjMember.setPw(bCryptPasswordEncoder.encode(hjMember.getPw()));
        hjMemberRepository.save(hjMember);
    }
    
    public void delete(Long no) {   //회원 탈퇴
        hjMemberRepository.deleteById(no);
    }

    public List<HjMember> getMemberList() {   //관리자 회원 목록
        return hjMemberRepository.findAll();
    }
    
    public boolean idCheck(String id) {
       return hjMemberRepository.existsById(id);
    }

public boolean emailCheck(String email) {
      return hjMemberRepository.existsByEmail(email);
   }
   
}
