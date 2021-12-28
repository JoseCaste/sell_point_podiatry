package com.podiatry.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.podiatry.model.Citas;

public interface CitasRepository extends JpaRepository<Citas, Long> {
	
	public List<Citas> findByDate(LocalDate date);
}
