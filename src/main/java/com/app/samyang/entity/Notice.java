package com.app.samyang.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
public class Notice {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long no;
	
	@Column(nullable = false, length = 200)
	private String title;
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	private LocalDateTime resdate;
	
	private int hits;
	
	@PrePersist
	protected void onCreated() {
		this.resdate = this.resdate == null ? LocalDateTime.now() : this.resdate;
		this.hits = 0;
	}
}