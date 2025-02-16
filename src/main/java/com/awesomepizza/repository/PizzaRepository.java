package com.awesomepizza.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.awesomepizza.model.Pizza;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Long> {
	Optional<Pizza> findByCode(String code);
}
