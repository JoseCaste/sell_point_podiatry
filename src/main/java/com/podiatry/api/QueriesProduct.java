package com.podiatry.api;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.podiatry.model.Product;
import com.podiatry.repository.ProductRepository;

@RestController
public class QueriesProduct {
	@Autowired
	ProductRepository repository;
	
	@GetMapping("/getProduct")
	public ArrayList<Product> getProduct(@RequestParam(name="name") String name){
		System.out.println(name);
		return repository.getByName(name);
		
		
	}

}
