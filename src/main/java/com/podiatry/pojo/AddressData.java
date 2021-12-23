package com.podiatry.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddressData {
	
	private Long idUser;
	private Long domicilioId;
	private String claveAddress;
	private String calle;
	private String entreCalle;
	private String ciudad;
	private String cp;
	private Integer numero;
	private String colonia;
	private String estado;
	private String comentarios;
}
