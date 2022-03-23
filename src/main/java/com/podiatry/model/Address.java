package com.podiatry.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity(name="address")
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_address")
	private Long id_address;
	
	@Column(name="clave")
	private String clave;
	
	@Column(name="city")
	private String city;
	
	@Column(name="cp")
	private String cp;
	private Integer number;
	
	@Column(name="colonia")
	private String colonia;
	
	@Column(name="state")
	private String state;

	@Column(name="comentarios")
	private String comentarios;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY,optional = false)
	@JoinColumn(name = "fk_id_user",nullable = false)
	private User user;
	
	@OneToMany(mappedBy = "address")
	List<Purchase> purchaces;
	public Address(String city, String cp, Integer number, String colonia, String state) {
		this.city = city;
		this.cp = cp;
		this.number = number;
		this.colonia = colonia;
		this.state = state;
	}
	

	public Address() {
	}


	public Address(String clave, String city, String cp, Integer number, String colonia, String state,
			String comentarios, User user) {
		super();
		this.clave = clave;
		this.city = city;
		this.cp = cp;
		this.number = number;
		this.colonia = colonia;
		this.state = state;
		this.comentarios = comentarios;
		this.user = user;
	}
	
	
}
