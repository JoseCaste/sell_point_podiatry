package com.podiatry.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.podiatry.model.User;
import com.podiatry.repository.UserRepository;

@RestController
public class UserQueries {

	@Autowired 
	UserRepository userRepository;
		
	@PostMapping("/signin")
	public ResponseEntity<User> signin(@RequestBody User user) {
		System.out.println(user.toString());
		User user_find= userRepository.getUser(user.getUserName(), user.getPassword());
		if(user_find!=null) 
			return new ResponseEntity<User>(user_find, HttpStatus.ACCEPTED);
		else return new ResponseEntity<User>(user_find, HttpStatus.NOT_FOUND);
		
	}
}
