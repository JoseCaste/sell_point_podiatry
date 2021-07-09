package com.podiatry.api;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.podiatry.model.Product;
import com.podiatry.repository.ProductRepository;

@RestController
public class CargaMasivaController {

	@Autowired
	private ProductRepository productRepository;
	
	@GetMapping("/uploadImage")
	public ResponseEntity<String> saveProduct() {
		String [] names = new File("c:\\img").list();
		List<Product> products= new ArrayList<>();
		for (String file_ : names) {
			try {
				byte[] imageBytes = Files.readAllBytes(new File("c:\\img\\"+file_).toPath());
	            Base64.Encoder encoder = Base64.getEncoder();
	            String encoded = encoder.encodeToString(imageBytes);
	            Random random= new Random();
				products.add(new Product(file_.substring(0,file_.indexOf(".")),random.nextInt(200-10)+10, random.nextInt(200-10)+10,encoded));
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return ResponseEntity.internalServerError().build();
			}
		}
		
		productRepository.saveAll(products);
		return ResponseEntity.ok("Cargado");
	}
}
