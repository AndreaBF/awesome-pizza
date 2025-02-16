package com.awesomepizza.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.awesomepizza.model.CustomerOrder;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
	// Puoi aggiungere qui metodi di ricerca personalizzati se necessario
	// Ad esempio, se volessi cercare ordini per nome cliente:
	// List<CustomerOrder> findByCustomerName(String customerName);
}
