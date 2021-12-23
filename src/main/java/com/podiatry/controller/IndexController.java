package com.podiatry.controller;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mercadopago.MercadoPago;
import com.mercadopago.resources.Preference;
import com.mercadopago.resources.datastructures.preference.BackUrls;
import com.mercadopago.resources.datastructures.preference.Item;
import com.mercadopago.resources.datastructures.preference.Payer;
import com.podiatry.model.Address;
import com.podiatry.model.Product;
import com.podiatry.model.User;
import com.podiatry.pojo.AddressData;
import com.podiatry.repository.AddressRepository;
import com.podiatry.repository.ProductRepository;
import com.podiatry.repository.UserRepository;

@Controller
public class IndexController {

	@Autowired
	private UserRepository userRepository;
	@Autowired 
	private ProductRepository productRepository;
	@Autowired 
	private AddressRepository addressRepository;
	
	private final Integer SECOND_STEP_WIZARD=1;
	
	
	@GetMapping({ "/", "/index" })
	public String index(@RequestParam(value = "name", defaultValue = "World", required = true) String name,
			Model model) {
		model.addAttribute("name", "jose");
		return "index";
	}

	@PostMapping("/session")
	public String session(@ModelAttribute(name = "user") User user, Model model, HttpSession session) {
		user= userRepository.getUser(user.getUserName(), md5(user.getPassword()));
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
	@GetMapping("/cris")
	public String cris(Model model) {
		model.addAttribute("cris", "PRUEBA CRIS");
		return "index2";
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
