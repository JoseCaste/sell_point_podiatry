package com.podiatry.model;


import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.List;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
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

	@OneToMany(mappedBy = "user")
	private List<CarSales> carSales;

	@JsonBackReference
	@OneToMany(mappedBy = "user",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	private List<Address> addresses;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(
			name = "user_roles",
			joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id_role")
	)
	private Collection<Role> roles;

	public User() {
	}
}
