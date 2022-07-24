package com.podiatry.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
//import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.apache.commons.codec.binary.Base64;
import com.podiatry.exceptions.DateException;
import com.podiatry.pojo.ProductData;
import com.podiatry.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
import com.podiatry.model.Citas;
import com.podiatry.model.DateData;
import com.podiatry.model.Product;
import com.podiatry.model.Purchase;
import com.podiatry.model.SuccessCriteria;
import com.podiatry.model.User;
import com.podiatry.pojo.AddressData;
import com.podiatry.repository.AddressRepository;
import com.podiatry.repository.CarSalesRepository;
import com.podiatry.repository.CitasRepository;
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
	
	@Autowired
	private CitasRepository citasRepository;

	@Autowired
	private Environment environment;

	@Autowired
	private UserServices userServices;
	private final Integer SECOND_STEP_WIZARD=1;

	
	private final String APPROVED="approved";
	
	private final String PENDING="pending";
	
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
					.setSuccess(String.format("http://localhost:%s/success/%d/%d", environment.getProperty("local.server.port"),user.get().getId(),address.getDomicilioId())));
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
				redirectAttributes.addFlashAttribute("url", "/controlPanel");
				return "redirect:/success-payment";
			}else return "controlPanel";
		}else return "controlPanel";
		
	}

	@GetMapping("/success-payment")
	public String successPayment(Model model, RedirectAttributes redirectAttributes) {
		return "success_payment";
	}
	
	@GetMapping("/success-payment-cita")
	public String successPaymentCita(Model model, RedirectAttributes redirectAttributes) {
		return "success_payment_cita";	
	}
	
	@GetMapping("/controlPanel")
	public String controlPanel(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
		model.addAttribute("payment_success",true);
		loadResources(model,session);
		return "controlPanel";
	}
	@GetMapping("/citas")
	public String registerDate(Model model, @ModelAttribute("date_data") DateData dateData) {

		model.addAttribute("minDate",LocalDate.now());
		return "register_date";
	}
	@PostMapping("/citas")
	public String registerDate(Model model, HttpSession session,@ModelAttribute("date_data") DateData dateData) {
		try {
			validateDate(dateData);
			Citas cita= new Citas();
			mapDateDateToCita(dateData,cita);
			this.citasRepository.save(cita);
			Preference preference = new Preference();
			preference.setBackUrls(new BackUrls().setFailure("htttp://localhost:8080/cita/faiulure")
					.setPending("http://localhost:8080/pending")
					.setSuccess(String.format("http://localhost:%s/cita/paid/%d", environment.getProperty("local.server.port"),cita.getId())));
			Payer payer = new Payer();
			payer.setEmail(dateData.getEmail());
			try {
				Item item= new Item();
				item.setId(dateData.getPlace()+dateData.getFullName()+cita.getId());
				item.setTitle(dateData.getPlace());
				item.setQuantity(1);
				item.setUnitPrice((float)150);
				preference.appendItem(item);
				preference.save();
				return String.format("redirect:%s", preference.getSandboxInitPoint());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return "controlPanel";
			}
		}catch (DateException message){
			message.printStackTrace();
			model.addAttribute("error", message.getMessage());
			return "controlPanel";
		}
	}

	private void validateDate(DateData dateData) throws DateException {
		final LocalDate date= LocalDate.parse(dateData.getDate());
		if (date.isBefore(LocalDate.now())) throw new DateException("La fecha no puede ser anterior a la fecha actual");
	}

	@GetMapping("/cita/paid/{id}")
	private String citaPaid(@PathVariable("id")Long idCita, @Validated SuccessCriteria successCriteria, RedirectAttributes redirectAttributes) {
		Optional<Citas> cita_pending_= this.citasRepository.findById(idCita);
		if(cita_pending_.isPresent()) {
			Citas cita_pending= cita_pending_.get();
			loadCitaResources(cita_pending, successCriteria);
			cita_pending.setStatus(APPROVED);
			this.citasRepository.save(cita_pending);
			redirectAttributes.addFlashAttribute("id_payment",successCriteria.getPayment_id());
			redirectAttributes.addFlashAttribute("payment_type",successCriteria.getPayment_type().equals("credit_card")? "Tarjeta de crédito":"Tarjeta de débito");
			return "redirect:/success-payment-cita";
		}
		redirectAttributes.addFlashAttribute("status",false);
		redirectAttributes.addFlashAttribute("error_paid", "Hubo un error al recuperar la información");
		return "redirect:/register_date";
	}
	@PostMapping("/user")
	public ResponseEntity<User> saveUser(@RequestBody User user){
		final User user_ = this.userServices.save(user);
		return new ResponseEntity(user_,HttpStatus.CREATED);
	}
	
	
	private void mapDateDateToCita(DateData dateData, Citas cita) {
		cita.setPacientName(dateData.getFullName());
		cita.setLastName(dateData.getLastName());
		cita.setPhone(dateData.getPhone());
		cita.setDate(LocalDate.parse(dateData.getDate()));
		cita.setTime(LocalTime.parse(dateData.getTime()));
		cita.setComments(dateData.getComments());
		cita.setPlace(dateData.getPlace());
		cita.setAddress(dateData.getAddress());
		cita.setEmail(dateData.getEmail());
		cita.setAtendido(false);
		cita.setStatus(PENDING);
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
		purchase.setPayment_id(successCriteria.getPayment_id());
	}
	private void loadCitaResources(Citas cita, SuccessCriteria successCriteria) {
		cita.setCollection_id(successCriteria.getCollection_id());
		cita.setCollection_status(successCriteria.getCollection_status());
		cita.setExternal_reference(successCriteria.getExternal_reference());
		cita.setMerchant_id(successCriteria.getMerchant_id());
		cita.setMerchant_order_id(successCriteria.getMerchant_order_id());
		cita.setPayment_type(successCriteria.getPayment_type());
		cita.setPreference_id(successCriteria.getPreference_id());
		cita.setSite_id(successCriteria.getSite_id());
		cita.setStatus_payment(successCriteria.getStatus());
		cita.setPayment_id(successCriteria.getPayment_id());
	}
	private void loadResources(Model model, HttpSession httpSession) {
		List<ProductData> all_products = loadProducts();
		try {
			final UserDetails userLogged = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			User user= this.userRepository.findByUserName(userLogged.getUsername()).get();

			ObjectMapper car_sales_json= new ObjectMapper();

			car_sales_json.registerModule(new Hibernate5Module());

			List<CarSalesUser> carlesCarSalesUsers= new ArrayList<>();
					user.getCarSales().stream().forEach(item->{
					Product product= this.productRepository.findById(item.getProduct().getId_product()).get();
					carlesCarSalesUsers.add(new CarSalesUser(product, item.getTotal()));
			});

			String json_object=car_sales_json.writeValueAsString(carlesCarSalesUsers);
			model.addAttribute("user_id", user.getId());
			model.addAttribute("user_name",user.getName());
			model.addAttribute("car_sales", json_object);
			model.addAttribute("allProducts", all_products);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private List<ProductData> loadProducts() {
		final List<Product> products =  this.productRepository.findAll();
		return products.stream().map(product -> new ProductData(product.getId_product(), product.getName(), Base64.encodeBase64String(product.getImg()), product.getTotal(), product.getPrice())).collect(Collectors.toList());
	}
}
