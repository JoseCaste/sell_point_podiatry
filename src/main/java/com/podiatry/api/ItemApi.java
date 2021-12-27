package com.podiatry.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.podiatry.model.CarSales;
import com.podiatry.model.CarSalesUser;
import com.podiatry.model.Product;
import com.podiatry.model.User;
import com.podiatry.repository.CarSalesRepository;
import com.podiatry.repository.ProductRepository;
import com.podiatry.repository.UserRepository;
import java.util.Objects;

@RestController
public class ItemApi {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired 
	private CarSalesRepository carSalesRepository;
	
	private final Boolean ACTIVE=true;
	
	@GetMapping("/totalItem/{id}")
	public Map<String, Integer> totalItem(@PathVariable("id") Long id){
		HashMap<String, Integer> map = new HashMap<>();
		map.put("total", productRepository.findById(id).get().getTotal());
		return  map;
	}
	@GetMapping("/add/{id}/{idUser}")
	public ResponseEntity<Object> addItem(@PathVariable("id") Long idProduct, @PathVariable("idUser") Long idUser){
		User user= this.userRepository.findById(idUser).get();
		Optional<Product> prodOptional= this.productRepository.findById(idProduct);
		if(prodOptional.isPresent()) {
			Optional <CarSales> carSalesOptional= this.carSalesRepository.findByProductAndUser(prodOptional.get(),user);
			if(carSalesOptional.isPresent()) {
				//carSalesOptional=this.carSalesRepository.findByProduct(prodOptional.get());
				CarSales carSales= carSalesOptional.get();
				carSales.setTotal(carSales.getTotal()+1);
				this.carSalesRepository.save(carSales);
				return new ResponseEntity<>("Aumentado",HttpStatus.OK);
			}else {
				CarSales carSales_= new CarSales(ACTIVE,1, user,prodOptional.get());
				this.carSalesRepository.save(carSales_);
				return new ResponseEntity<>("Agregado",HttpStatus.OK);
			}	
		}else return new ResponseEntity<>("El producto no está disponible o ha sido removido",HttpStatus.NOT_FOUND); 
	}
	@GetMapping("/carSales/{id}")
	public ResponseEntity<?> getCarSales(@PathVariable("id")Long idUser){
		Optional<User> user= this.userRepository.findById(idUser);
		if(user.isPresent() && Objects.nonNull(user.get().getCarSales())) {
			ObjectMapper car_sales_json= new ObjectMapper();
			car_sales_json.registerModule(new Hibernate5Module());
			List<CarSalesUser> carlesCarSalesUsers= new ArrayList<>();
			user.get().getCarSales().stream().forEach(item->{
				Product product= this.productRepository.findById(item.getProduct().getId_product()).get();
				carlesCarSalesUsers.add(new CarSalesUser(product, item.getTotal()));
			});
			return new ResponseEntity<>(carlesCarSalesUsers,HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>("Sin contenido",HttpStatus.NO_CONTENT);
		
	}
	@GetMapping("/plus/{id}/{idUser}")
	public ResponseEntity<Object> increeseItem(@PathVariable("id") Long idProduct, @PathVariable("idUser") Long idUser){
		User user= this.userRepository.findById(idUser).get();
		Optional<Product> prodOptional= this.productRepository.findById(idProduct);
		if(prodOptional.isPresent()) {
			Optional <CarSales> carSalesOptional= this.carSalesRepository.findByProductAndUser(prodOptional.get(),user);
			if(carSalesOptional.isPresent()) {
				CarSales carSales= carSalesOptional.get();
				carSales.setTotal(carSales.getTotal()+1);
				this.carSalesRepository.save(carSales);
				return new ResponseEntity<>("Aumentado",HttpStatus.OK);
			}else return new ResponseEntity<>("El producto no está disponible o ha sido removido del carrito de compras",HttpStatus.NOT_FOUND);
		}else return new ResponseEntity<>("El producto no está disponible o ha sido removido",HttpStatus.NOT_FOUND);
	}
	@GetMapping("/less/{id}/{idUser}")
	public ResponseEntity<Object> decreeseItem(@PathVariable("id") Long idProduct, @PathVariable("idUser") Long idUser){
		User user= this.userRepository.findById(idUser).get();
		Optional<Product> prodOptional= this.productRepository.findById(idProduct);
		if(prodOptional.isPresent()) {
			Optional <CarSales> carSalesOptional= this.carSalesRepository.findByProductAndUser(prodOptional.get(),user);
			if(carSalesOptional.isPresent()) {
				CarSales carSales= carSalesOptional.get();
				carSales.setTotal(carSales.getTotal()-1);
				this.carSalesRepository.save(carSales);
				return new ResponseEntity<>("Decrementado",HttpStatus.OK);
			}else return new ResponseEntity<>("El producto no está disponible o ha sido removido del carrito de compras",HttpStatus.NOT_FOUND);
		}else return new ResponseEntity<>("El producto no está disponible o ha sido removido",HttpStatus.NOT_FOUND);
	}
	@GetMapping("/delete/{id}/{idUser}")
	public ResponseEntity<Object> deleteItem(@PathVariable("id") Long idProduct, @PathVariable("idUser") Long idUser){
		User user= this.userRepository.findById(idUser).get();
		Optional<Product> prodOptional= this.productRepository.findById(idProduct);
		if(prodOptional.isPresent()) {
			Optional <CarSales> carSalesOptional= this.carSalesRepository.findByProductAndUser(prodOptional.get(),user);
			if(carSalesOptional.isPresent()) {
				this.carSalesRepository.delete(carSalesOptional.get());
				return new ResponseEntity<>("Eliminado",HttpStatus.OK);
			}else return new ResponseEntity<>("El producto no está disponible o ha sido removido del carrito de compras",HttpStatus.NOT_FOUND); 
		}else return new ResponseEntity<>("El producto no está disponible o ha sido removido",HttpStatus.NOT_FOUND);
	}
}
