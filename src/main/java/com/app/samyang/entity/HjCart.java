package com.app.samyang.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HjCart {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String memberId; // 사용자 ID 또는 이메일

	private Long productId;
	private String pname;
	private int price;
	private int quantity;
	private String img1;
}
