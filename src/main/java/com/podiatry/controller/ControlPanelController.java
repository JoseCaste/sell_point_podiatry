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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.mercadopago.MercadoPago;
import com.mercadopago.resources.Preference;
import com.mercadopago.resources.datastructures.preference.BackUrls;
import com.mercadopago.resources.datastructures.preference.Item;
import com.mercadopago.resources.datastructures.preference.Payer;
import com.podiatry.model.Address;
import com.podiatry.model.CarSales;
import com.podiatry.model.CarSalesUser;
import com.podiatry.model.Product;
import com.podiatry.model.Purchase;
import com.podiatry.model.SuccessCriteria;
import com.podiatry.model.User;
import com.podiatry.pojo.AddressData;
import com.podiatry.repository.AddressRepository;
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
	
	@Autowired
	private AddressRepository addressRepository;

	private final Integer SECOND_STEP_WIZARD=1;

	
	private final String APPROVED="approved";
	
	@GetMapping("/session")
	public String currentSession(Model model,HttpSession httpSession) {
		
		loadResources(model,httpSession);
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

	@PostMapping("/buyItem")
	public String buyItem(Model model, @ModelAttribute AddressData address) {
		Optional<User> user= this.userRepository.findById(address.getIdUser());
		if(user.isPresent()) {
			Preference preference = new Preference();
			preference.setBackUrls(new BackUrls().setFailure("htttp://localhost:8080/failure")
					.setPending("http://localhost:8080/pending")
					.setSuccess(String.format("%s/%d/%d", "http://localhost:8080/success",user.get().getId(),address.getDomicilioId())));
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
			
			//return "HI";
		}else return "controlPanel";
	}

	@GetMapping("/wizard/{id}")
	public String formWizard(Model model, @PathVariable("id") Long idUser, @ModelAttribute("address") AddressData addressPojo) {
		Optional<User> userOptional=this.userRepository.findById(idUser);
		if(userOptional.isPresent()) {
			User user= userOptional.get();
			addressPojo.setIdUser(user.getId());
			model.addAttribute("idUser", user.getId());
			model.addAttribute("userName",String.format("%s %s" , user.getName(),user.getLastName()));
			model.addAttribute("address_founded",user.getAddresses());
		}
		return "form-wizard";
	}
	@PostMapping("/address/save/")
	public String saveAddress(Model model, @ModelAttribute("address") AddressData addressPojo, RedirectAttributes redirectAttributes) {
		Optional<User> userOptional=this.userRepository.findById(addressPojo.getIdUser());
		if(userOptional.isPresent()) {
			Address address = new Address(addressPojo.getClaveAddress(), addressPojo.getCiudad(), addressPojo.getCp(), addressPojo.getNumero(), addressPojo.getColonia(), addressPojo.getEstado(), addressPojo.getComentarios(), userOptional.get());
			addressRepository.save(address);
			redirectAttributes.addFlashAttribute("address_saved", "Domicilio agregado");
			redirectAttributes.addFlashAttribute("page", SECOND_STEP_WIZARD);
		}
		return String.format("redirect:/wizard/%d", userOptional.get().getId());
	}
	@GetMapping("/success/{id}/{idAddress}")
	public String success(@PathVariable("id")Long id, @PathVariable("idAddress") Long idAddress,@Validated SuccessCriteria successCriteria, Model model, HttpSession httpSession, RedirectAttributes redirectAttributes) {
		if(successCriteria.getStatus().equals(APPROVED)) {
			Optional<User> user= this.userRepository.findById(id);
			Optional<Address> addresOptional= this.addressRepository.findById(idAddress);
			if(user.isPresent() && addresOptional.isPresent()) {
				Purchase purchase = new Purchase();
				ArrayList<Product> products = new ArrayList<Product>();
				List<CarSales> list_car_sales=user.get().getCarSales();
				//list_car_sales.forEach(item-> item.setStatus(false));
				this.carSalesRepository.deleteAll(list_car_sales);
				purchase.setUser(user.get());
				purchase.setAddress(addresOptional.get());
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
	public String controlPanel(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
		model.addAttribute("payment_success",true);
		loadResources(model,session);
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
	
	private void loadResources(Model model, HttpSession httpSession) {
		// TODO Auto-generated method stub
		List<Product> all_products= repository.allProducts();
		//Optional<CarSales> car_sales_userOptinal= this.carSalesRepository.findByUser(this.userRepository.getById((long)httpSession.getAttribute("id")));
		try {
			User user= this.userRepository.findById((long)httpSession.getAttribute("id")).get();
			ObjectMapper car_sales_json= new ObjectMapper();
			car_sales_json.registerModule(new Hibernate5Module());
			List<CarSalesUser> carlesCarSalesUsers= new ArrayList<>();
					user.getCarSales().stream().forEach(item->{
					Product product= this.productRepository.findById(item.getProduct().getId_product()).get();
					carlesCarSalesUsers.add(new CarSalesUser(product, item.getTotal()));
			});
			String json_object=car_sales_json.writeValueAsString(carlesCarSalesUsers);	
			model.addAttribute("car_sales", json_object);
			model.addAttribute("allProducts", all_products);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
