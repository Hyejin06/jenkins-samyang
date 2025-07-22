package com.app.samyang.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JyMember {
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long no;
   
   @Column(unique = true, nullable = false, length = 100)
   private String id;
   
   @Column(nullable = false)
   private String pw;   //암호화
   
   @Column(nullable = false)
   private String name;

   @Column(nullable = false, length = 15)
   private String tel;
   
   @Column(nullable = false)
   private String email;
   
   @Column(nullable = false, length = 20)
   private String grade;
   
   @Column
   private String gradeProgress;
   
   @Column
   private Integer amountToVip;
}

