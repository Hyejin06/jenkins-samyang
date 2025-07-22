package com.app.samyang.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.samyang.entity.Member;
import com.app.samyang.repository.MemberRepository;
//기능 구현은 서비스에서 
@Service
public class MemberService {
   private final MemberRepository memberRepository;
   private final BCryptPasswordEncoder bCryptPasswordEncoder; 
   
   public MemberService(MemberRepository memberRepository, 
         BCryptPasswordEncoder bCryptPasswordEncoder) {
      this.memberRepository = memberRepository;
      this.bCryptPasswordEncoder = bCryptPasswordEncoder;
   }
   
   public Member join(Member member) {
	    member.setPw(bCryptPasswordEncoder.encode(member.getPw()));

	    if (member.getGrade() == null || member.getGrade().isBlank()) {
	        member.setGrade("family"); // 기본 등급
	    }

	    return memberRepository.save(member);
	}
   
   public Member login(String id, String pw) { //로그인 - 아이디와 비밀번호가 일치하는 회원 정보를 반환
      return memberRepository.findById(id)
            .filter(m -> bCryptPasswordEncoder.matches(pw, m.getPw()))
            .orElse(null);
   }
   
    public Member getMember(String id) {   //마이페이지 회원 정보
        return memberRepository.findById(id).orElse(null);
    }

    public Member getMemberByNo(Long no) {   //회원 번호로 검색
        return memberRepository.findByNo(no).orElse(null);
    }
    
    public Member update(Member member) {
        return memberRepository.save(member); // 암호화는 Controller에서 이미 했으므로 그대로 저장
    }

    public void updatePassword(Member member) {   //비밀번호 변경
        member.setPw(bCryptPasswordEncoder.encode(member.getPw()));
        memberRepository.save(member);
    }
    
    public void delete(Long no) {   //회원 탈퇴
        memberRepository.deleteById(no);
    }

    public List<Member> getMemberList() {   //관리자 회원 목록
        return memberRepository.findAll();
    }
    
    public boolean idCheck(String id) {
       return memberRepository.existsById(id);
    }

public boolean emailCheck(String email) {
      return memberRepository.existsByEmail(email);
   }
   
}
