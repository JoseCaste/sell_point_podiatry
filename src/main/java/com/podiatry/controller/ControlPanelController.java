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
	
	
}
