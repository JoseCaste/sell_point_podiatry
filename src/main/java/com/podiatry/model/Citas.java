package com.podiatry.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Entity
@AllArgsConstructor
public class Citas implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_cita")
	private Long id;
	
	private LocalDate date;
	
	private LocalTime time;
	
	private String status;
	
	private String place;
	
	private Boolean paid;
	
	@Column(name="full_name")
	private String pacientName;
	
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="email")
	private String email;
	
	@Column(name="phone")
	private String phone;
	
	@Column(name="comments")
	private String comments;
	
	private Integer collection_id;
	private String collection_status;
	private Integer payment_id;
	private String status_payment;
	private String external_reference;
	private String payment_type;
	private String merchant_order_id;
	private String preference_id;
	private String site_id;
	private String processing_mode;
	private String merchant_id;

	public Citas() {}
	
	
}
