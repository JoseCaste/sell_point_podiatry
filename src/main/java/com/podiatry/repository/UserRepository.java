package com.podiatry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.podiatry.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	@Query(value="SELECT * FROM user WHERE user_name like ?1 and password like ?2", nativeQuery = true) 
	User getUser(String user, String password);

	public Optional<User> findByUserName(String userName);
} 
 