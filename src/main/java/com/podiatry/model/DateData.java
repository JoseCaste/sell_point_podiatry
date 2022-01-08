package com.podiatry.model;

import java.io.Serializable;

import org.springframework.format.annotation.DateTimeFormat;

import com.mercadopago.core.annotations.validation.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DateData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@NotNull
	private String fullName;
	
	@NotNull
	private String lastName;
	
	private String email;
	
	@NotNull
	private String phone;
	
	private String address;
	
	private String comments;
	
	@DateTimeFormat(pattern = "yyyy/mm/dd")
	private String date;
	
	@NotNull
	private String time;
	
	@NotNull
	private String place;

}
