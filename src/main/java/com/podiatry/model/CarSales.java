package com.podiatry.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity(name = "car_sales")
public class CarSales implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_car_sales")
	private Long id;
	
	@Column(name="status")
	private Boolean status;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY,optional = false)
	@JoinColumn(name="fk_id_usuario")
	private User user;
	
	@JsonIgnore	
	@ManyToOne(fetch = FetchType.LAZY,optional = false)
	@JoinColumn(name="fk_id_producto")
	private Product product;
	/*@OneToMany(mappedBy = "carSales",cascade = CascadeType.ALL)
	List<CarSalesDetail> carSalesDetails;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="fk_id_user")
	User user;*/
	
	@Column(name="total")
	private Integer total;
	public CarSales() {}
	
	
	public CarSales(Boolean status, Long total, User user) {
		this.status = status;
	}

	public CarSales(Boolean status, Long total) {
		this.status = status;
	}

	public CarSales(Boolean status) {
		this.status = status;
	}

	public CarSales(Boolean status, User user) {
		this.status = status;
		this.user = user;
	}


	public CarSales(Boolean status, Integer total,User user, Product product) {
		this.status = status;
		this.user = user;
		this.product = product;
		this.total = total;
	}
	
	
	
}
