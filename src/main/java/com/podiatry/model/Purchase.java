package com.podiatry.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class Purchase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_purchase;
	@ManyToOne
	@JoinColumn(name="fk_iduser",nullable = false, updatable = false)
	private User user;
	
	@Temporal(TemporalType.DATE)
	private Date date_;
	
	@JoinTable(
			name="contains_",
			joinColumns = @JoinColumn(name="fk_idpurchase",nullable = false),
			inverseJoinColumns = @JoinColumn(name="fk_idproduct",nullable = false)
			)
	@ManyToMany(cascade = CascadeType.ALL)
	private List<Product> products;

	public Purchase() {
	
	}
	
}
