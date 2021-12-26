package com.podiatry.controller;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.podiatry.model.Product;
import com.podiatry.model.User;
import com.podiatry.repository.ProductRepository;
import com.podiatry.repository.UserRepository;

@Controller
public class IndexController {

	@Autowired
	private UserRepository repository;
	@Autowired ProductRepository productRepository;
	@GetMapping({ "/", "/index" })
	public String index(@RequestParam(value = "name", defaultValue = "World", required = true) String name,
			Model model) {
		model.addAttribute("name", "jose");
		return "index";
	}

	@PostMapping("/session")
	public String session(@ModelAttribute(name = "user") User user, Model model, HttpSession session) {
		user= repository.getUser(user.getUserName(), md5(user.getPassword()));
		if ( user!= null) {
			List<Product> all_products= productRepository.allProducts();
			session.setAttribute("user", user.getName());
			session.setAttribute("id", user.getId());
			model.addAttribute("allProducts", all_products);
			return "controlPanel";
		}else {
			model.addAttribute("error", "Credenciales no válidas");
			return "index";
		}
		
	}
	public String md5(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(password.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String hashtext = number.toString(16);

			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
