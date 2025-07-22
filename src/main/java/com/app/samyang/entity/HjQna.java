package com.app.samyang.entity;

import java.time.LocalDateTime;

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
import lombok.ToString;

@Entity
@Getter 
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HjQna {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long no;
    
    @Builder.Default
	private int level = 1;	//1:질문, 2:답변
    
	private Long parno;	//질문글이 0이면, 해당 답변글도 0임
	
	@Column(nullable = false, length = 200)
	private String title;
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	@Column(nullable = true)
	private String author;
	
	@Builder.Default
	private LocalDateTime resdate = LocalDateTime.now();
	
	@Builder.Default
	private int hits = 0;
}

