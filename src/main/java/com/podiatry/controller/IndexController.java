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
	private UserRepository userRepository;
	@Autowired 
	private ProductRepository productRepository;
	
	@GetMapping({ "/", "/index" })
	public String index(@RequestParam(value = "name", defaultValue = "World", required = true) String name,
			Model model) {
		return "index";
	}
}
