package com.podiatry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mercadopago.MercadoPago;


@SpringBootApplication
public class PodiatryApplication implements CommandLineRunner{
	
	public static void main(String[] args) {	
		SpringApplication.run(PodiatryApplication.class, args);
		
	}

	@Override
	public void run(String... args) throws Exception {
		MercadoPago.SDK.configure("TEST-2907327363456926-122016-ff4c3e130dafd4857504c8decced8a77-1043363108");
	}

}
