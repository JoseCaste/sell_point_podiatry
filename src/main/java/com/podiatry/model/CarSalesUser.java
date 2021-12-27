package com.podiatry.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class CarSalesUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String img;
	private Product product;
	private Integer total;
	public CarSalesUser(Product product, Integer total) {
		this.product = product;
		this.total = total;
	}

	 
}
