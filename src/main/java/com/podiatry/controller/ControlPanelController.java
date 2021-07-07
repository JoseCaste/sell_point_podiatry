package com.podiatry.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.podiatry.PodiatryApplication;
import com.podiatry.model.Product;
import com.podiatry.repository.ProductRepository;

@Controller
public class ControlPanelController {
	@Autowired 
	private ProductRepository repository;
	
	@GetMapping("/session")
	public String currentSession(Model model) {
		//saveProduct();
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
		int [] price = {125,500,250,70,1500,180,96}; 
		List<Product> products= new ArrayList<>();
		for (int i=1;i<=7;i++) {
			try {
				String url=String.format("/com/podiatry/stuff/%d.jpg",i);
				String type="jpg";
				BufferedImage img = ImageIO.read(PodiatryApplication.class.getResource(url));
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ImageIO.write(img, type, bos);
	            byte[] imageBytes = bos.toByteArray();
	 
	            //String base64bytes = Base64.encode(imageBytes);
	            Base64.Encoder encoder = Base64.getEncoder();
	            String encoded = encoder.encodeToString(imageBytes);
	            System.out.println(new Product(""+i,i,i,encoded).toString());
	            
				products.add(new Product(names[i],i,price[i],encoded));
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}	
			
		}
		repository.saveAll(products);
		return "Total de productos "+repository.allProducts().size();
	}
}
