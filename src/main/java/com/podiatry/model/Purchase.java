package com.podiatry.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date date_;
	
	@JoinTable(
			name="contains_",
			joinColumns = @JoinColumn(name="fk_idpurchase",nullable = false),
			inverseJoinColumns = @JoinColumn(name="fk_idproduct",nullable = false)
			)
	@ManyToMany(cascade = CascadeType.ALL)
	private List<Product> products;
	
	@Column(name="total")
	private Double total;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "fk_id_address")
	private Address address;
	
	private Integer collection_id;
	private String collection_status;
	private Integer payment_id;
	private String status;
	private String external_reference;
	private String payment_type;
	private String merchant_order_id;
	private String preference_id;
	private String site_id;
	private String processing_mode;
	private String merchant_id;
	public Purchase() {
	
	}
	
}
