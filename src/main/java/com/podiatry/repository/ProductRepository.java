package com.podiatry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.podiatry.model.Product;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query(value="SELECT * FROM product p where p.total>0 ", nativeQuery = true)
	List<Product> allProducts();
	
	@Modifying
	@Transactional
	@Query(value="UPDATE product p SET p.total=p.total- :total WHERE p.id_product = :id ", nativeQuery=true)
	int updateProduct (@Param("total") int total, @Param("id") Long id);

	@Query(value="SELECT * FROM product p where p.name like %?1%", nativeQuery = true)
	ArrayList<Product> getByName(String name);
}
