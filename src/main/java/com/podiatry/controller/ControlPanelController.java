package com.podiatry.controller;


import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.podiatry.model.Product;
import com.podiatry.repository.ProductRepository;

@Controller
public class ControlPanelController {
	@Autowired 
	private ProductRepository repository;
	
	@GetMapping("/session")
	public String currentSession(Model model) {
		
		List<Product> all_products= repository.allProducts();
		model.addAttribute("allProducts", all_products);
		return "controlPanel";
	}
	
	@GetMapping("/logout")
	public String destroySession(Model model, HttpSession session) {
		session.invalidate();
		return "index";
	}
	
	@GetMapping("/uploadImage")
	public String saveProduct() {
		String [] names = {"Pinza","Crema verde","Aceite","Plantilla","Soporte","Kit","Exfoliante"};
		List<Product> products= new ArrayList<>();
		for (int i=0;i<7;i++) {
			try {
				File file = new File(String.format("C:\\img\\%d.jpg",i));
				byte[] imageBytes = Files.readAllBytes(file.toPath());
	            Base64.Encoder encoder = Base64.getEncoder();
	            String encoded = encoder.encodeToString(imageBytes);
	            Random random= new Random();
				products.add(new Product(names[i],random.nextInt(200-10)+10, random.nextInt(200-10)+10,encoded));
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}	
			
		}
		repository.saveAll(products);
		return "index";
	}
}
