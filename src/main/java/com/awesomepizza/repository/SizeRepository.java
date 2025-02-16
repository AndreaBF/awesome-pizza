package com.awesomepizza.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.awesomepizza.model.Size;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {
	Optional<Size> findByCode(String code);
}
