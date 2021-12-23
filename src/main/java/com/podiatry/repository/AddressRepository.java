package com.podiatry.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.podiatry.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
