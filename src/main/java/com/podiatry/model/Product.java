package com.podiatry.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;


@Entity(name = "product")
@Data
@ToString
@AllArgsConstructor
public class Product implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_product;
	private String name;
	private int total;
	private double price;
	private String img;
	
	@JsonIgnore
	@Transient
	@ManyToMany(mappedBy = "products")
	private List<Purchase> purchases;
	
	@JsonIgnore
	@Transient
	@OneToMany(mappedBy = "product",cascade = CascadeType.ALL)
	private List<Product> products;
	
	public Product() {
		
	}
	public Product(String name, int total, double price,String img) {
		this.name = name;
		this.total = total;
		this.price=price;
		this.img = img;
	}
	public Product(Long id_product, int total) {
		this.id_product = id_product;
		this.total = total;
	}
	
	
	
}
