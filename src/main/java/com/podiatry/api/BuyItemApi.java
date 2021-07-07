package com.podiatry.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.podiatry.model.Product;
import com.podiatry.model.Purchase;
import com.podiatry.model.User;
import com.podiatry.repository.ProductRepository;
import com.podiatry.repository.PurchaseRepository;
import com.podiatry.repository.UserRepository;

@RestController
public class BuyItemApi {
	@Autowired
	UserRepository userRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	PurchaseRepository purchaseRepository;

	@GetMapping("/hello")
	ResponseEntity<String> hello() {
	
		HttpHeaders headers = new HttpHeaders();
	    headers.set("Content-type", "text/plain");
	    return new ResponseEntity<>(
	            "Year of birth cannot be in the future", 
	            HttpStatus.BAD_REQUEST);
	}
	
	@PostMapping("/buyItem/{id}")
	public ResponseEntity<String> buyItem(@RequestBody String json, @PathVariable Long id) {
		// System.out.println("id --> "+ id);
		User user = userRepository.findById(id).get();
		Purchase purchase = new Purchase();
		ArrayList<Product> products = new ArrayList<Product>();
		try {
			
			purchase.setUser(user);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			purchase.setDate_(formatter.parse(formatter.format(new Date())));
			

			JSONArray array = new JSONArray(json);
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				/*
				 * System.out.println(object.getString("id"));
				 * System.out.println(object.getString("name"));
				 * System.out.println(object.getInt("total"));
				 */
				Product product=productRepository.findById(Long.parseLong(object.getString("id"))).get();
				products.add(product);
				productRepository.updateProduct(object.getInt("total"), product.getId_product());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new ResponseEntity<>("Algo ocurrió en el proceso", HttpStatus.SERVICE_UNAVAILABLE);
		}
		purchase.setProducts(products);
		purchaseRepository.save(purchase);
		HttpHeaders headers = new HttpHeaders();
	    headers.set("Content-type", "text/plain");
	    return new ResponseEntity<>("Compra realizada con éxito", headers,HttpStatus.OK);
	}
	
	@GetMapping("/totalItem/{id}")
	public Map<String, Integer> totalItem(@PathVariable("id") Long id){
		System.out.println(id);
		//return new ResponseEntity<>(productRepository.findById(id).get().getTotal(), HttpStatus.OK);
		HashMap<String, Integer> map = new HashMap<>();
		map.put("total", productRepository.findById(id).get().getTotal());
		return  map;
	}

}
