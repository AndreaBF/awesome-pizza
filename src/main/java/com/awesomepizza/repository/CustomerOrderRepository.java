package com.awesomepizza.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.awesomepizza.model.CustomerOrder;
import com.awesomepizza.model.CustomerOrder.OrderStatus;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
	// recupero mediante codice tracking
	Optional<CustomerOrder> findByTrackingCode(String trackingCode);

	// query derivata che restituisce il primo ordine con stato specificato,
	// ordinato per creazione (ascendente)
	Optional<CustomerOrder> findFirstByStatusOrderByCreatedAtAsc(OrderStatus status);
}
