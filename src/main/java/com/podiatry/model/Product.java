package com.podiatry.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;


@Entity(name = "product")
@Data
@ToString
@AllArgsConstructor
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_product;
	private String name;
	private int total;
	private double price;
	private String img;
	
	@Transient
	@ManyToMany(mappedBy = "products")
	private List<Purchase> purchases;
	
	public Product() {
		
	}
	public Product(String name, int total, double price,String img) {
		this.name = name;
		this.total = total;
		this.price=price;
		this.img = img;
	}
	public Product(Long id_product, int total) {
		super();
		this.id_product = id_product;
		this.total = total;
	}
	
	
	
}
