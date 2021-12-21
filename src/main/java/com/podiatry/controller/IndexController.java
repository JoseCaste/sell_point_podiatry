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

import com.mercadopago.MercadoPago;
import com.mercadopago.resources.Preference;
import com.mercadopago.resources.datastructures.preference.BackUrls;
import com.mercadopago.resources.datastructures.preference.Item;
import com.mercadopago.resources.datastructures.preference.Payer;
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
	@GetMapping("/pago")
	public String pago(Model model) {
		Preference preference = new Preference();
		preference.setBackUrls(new BackUrls().setFailure("htttp://localhost:8080/failure")
				.setPending("http://localhost:8080/pending")
				.setSuccess("http://localhost:8080/success"));
		try {
			MercadoPago.SDK.configure("TEST-2907327363456926-122016-ff4c3e130dafd4857504c8decced8a77-1043363108");


			Item item = new Item();
			item.setId("1234")
			    .setTitle("Blue shirt")
			    .setQuantity(1)
			    .setCategoryId("MXN")
			    .setUnitPrice((float) 20);
			
			Payer payer = new Payer();
			payer.setEmail("jotaguzman08@gmail.com");
			
			preference.setPayer(payer);
			preference.appendItem(item);
			preference.save();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			System.out.println("----------> "+preference.getSandboxInitPoint());
		}
		return String.format("redirect:%s", preference.getSandboxInitPoint());
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
