package com.podiatry.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.podiatry.model.CarSales;
import com.podiatry.model.Product;
import com.podiatry.model.User;

public interface CarSalesRepository extends JpaRepository<CarSales,Long> {

	Optional<CarSales> findByUser(User user);
	Optional<CarSales> findByProduct(Product product);
	Optional<CarSales> findByProductAndUser(Product product, User user);
}
