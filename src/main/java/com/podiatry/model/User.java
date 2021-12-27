package com.podiatry.model;


import java.io.Serializable;
import java.util.List;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString
@AllArgsConstructor
public class User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	@Column(name = "lastname")
	private String lastName;
	private String userName;
	private String password;
	
	@JsonIgnore
	@Transient
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private List<Purchase> purchase;
	
	/*@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
	private CarSales carSales;*/
	@JsonIgnore
	@OneToMany(mappedBy = "user")
	private List<CarSales> carSales;
	
	@OneToMany(mappedBy = "user",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	private List<Address> addresses;
	public User() {
	}
}
