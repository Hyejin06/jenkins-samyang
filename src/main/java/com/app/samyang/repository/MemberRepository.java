package com.app.samyang.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.samyang.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
   //찾을 경우 find, 존재 유무 exists를 접두어로 써야함 뒤에는 By필드명으로 기술하고, 매개변수는 필드명을 넣습니다.
   Optional<Member> findById(String id);   //로그인체크용: select * from member where id=?
   boolean existsById(String id);   //중복체크용 boolean (select * from member where id=?)
   Optional<Member> findByNo(Long no);
boolean existsByEmail(String email);
}

