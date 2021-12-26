package com.podiatry.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mercadopago.MercadoPago;
import com.mercadopago.resources.Preference;
import com.mercadopago.resources.datastructures.preference.BackUrls;
import com.mercadopago.resources.datastructures.preference.Item;
import com.mercadopago.resources.datastructures.preference.Payer;
import com.podiatry.model.CarSales;
import com.podiatry.model.Product;
import com.podiatry.model.Purchase;
import com.podiatry.model.SuccessCriteria;
import com.podiatry.model.User;
import com.podiatry.repository.CarSalesRepository;
import com.podiatry.repository.ProductRepository;
import com.podiatry.repository.PurchaseRepository;
import com.podiatry.repository.UserRepository;

@Controller
public class ControlPanelController {
	@Autowired 
	private ProductRepository repository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PurchaseRepository purchaseRepository;
	@Autowired 
	private CarSalesRepository carSalesRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	private final String APPROVED="approved";
	@GetMapping("/session")
	public String currentSession(Model model) {
		
		loadResources(model);
		return "controlPanel";
	}

	@GetMapping("/logout")
	public String destroySession(Model model, HttpSession session) {
		session.invalidate();
		return "index";
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
		}
		return String.format("redirect:%s", preference.getSandboxInitPoint());
	}
	
	@GetMapping("/buyItem/{id}")
	public String buyItem(@PathVariable("id")Long id) {
		Optional<User> user= this.userRepository.findById(id);
		if(user.isPresent()) {
			Preference preference = new Preference();
			preference.setBackUrls(new BackUrls().setFailure("http://localhost:8080/failure")
					.setPending("http://localhost:8080/pending")
					.setSuccess(String.format("%s/%d", "http://localhost:8080/success",id)));
			Payer payer = new Payer();
			payer.setEmail("jotaguzman08@gmail.com");
			
			try {
				user.get().getCarSales().stream().filter(item->item.getStatus()).forEach(product->{
					Item item= new Item();
					item.setId(String.format("%d", product.getProduct().getId_product()));
					item.setTitle(product.getProduct().getName());
					item.setQuantity(product.getTotal());
					item.setUnitPrice((float)product.getProduct().getPrice());
					preference.appendItem(item);
				});
				
				preference.save();
				return String.format("redirect:%s", preference.getSandboxInitPoint());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return "controlPanel";
			}
		}else return "controlPanel";
	}
	
	@GetMapping("/success/{id}")
	public String success(@PathVariable("id")Long id, @Validated SuccessCriteria successCriteria, Model model, HttpSession httpSession, RedirectAttributes redirectAttributes) {
		if(successCriteria.getStatus().equals(APPROVED)) {
			Optional<User> user= this.userRepository.findById(id);
			if(user.isPresent()) {
				Purchase purchase = new Purchase();
				ArrayList<Product> products = new ArrayList<Product>();
				List<CarSales> list_car_sales=user.get().getCarSales();
				this.carSalesRepository.deleteAll(list_car_sales);
				purchase.setUser(user.get());
				purchase.setDate_(Timestamp.valueOf(LocalDateTime.now()));
				user.get().getCarSales().stream().filter(item->item.getStatus()).forEach(item->{
				products.add(item.getProduct());
				this.productRepository.updateProduct(item.getTotal(), item.getProduct().getId_product());
				});
				purchase.setProducts(products);
				Double totalPrice= user.get().getCarSales().stream().map(item->item.getTotal()*item.getProduct().getPrice()).reduce(0.0, Double::sum);
				purchase.setTotal(totalPrice);
				loadPurchaseResources(purchase,successCriteria);
				purchaseRepository.save(purchase);
				redirectAttributes.addFlashAttribute("id_payment",successCriteria.getPayment_id());
				redirectAttributes.addFlashAttribute("payment_type",successCriteria.getPayment_type().equals("credit_card")? "Tarjeta de crédito":"Tarjeta de débito");
				
				return "redirect:/success-payment";
			}else return "controlPanel";
		}else return "controlPanel";
		
	}

	@GetMapping("/success-payment")
	public String successPayment(Model model, RedirectAttributes redirectAttributes) {
		return "success_payment";
	}
	
	@GetMapping("/controlPanel")
	public String controlPanel(Model model, RedirectAttributes redirectAttributes) {
		model.addAttribute("payment_success",true);
		loadResources(model);
		return "controlPanel";
	}
	
	private void loadPurchaseResources(Purchase purchase, SuccessCriteria successCriteria) {
		purchase.setCollection_id(successCriteria.getCollection_id());
		purchase.setCollection_status(successCriteria.getCollection_status());
		purchase.setExternal_reference(successCriteria.getExternal_reference());
		purchase.setMerchant_id(successCriteria.getMerchant_id());
		purchase.setMerchant_order_id(successCriteria.getMerchant_order_id());
		purchase.setPayment_type(successCriteria.getPayment_type());
		purchase.setPreference_id(successCriteria.getPreference_id());
		purchase.setSite_id(successCriteria.getSite_id());
		purchase.setStatus(successCriteria.getStatus());
	}
	
	private void loadResources(Model model) {
		// TODO Auto-generated method stub
		List<Product> all_products= repository.allProducts();
		model.addAttribute("allProducts", all_products);
	}
}
