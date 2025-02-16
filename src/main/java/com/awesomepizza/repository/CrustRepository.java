package com.awesomepizza.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.awesomepizza.model.Crust;

@Repository
public interface CrustRepository extends JpaRepository<Crust, Long> {
	Optional<Crust> findByCode(String code);
}
