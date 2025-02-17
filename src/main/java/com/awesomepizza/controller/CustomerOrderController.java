package com.awesomepizza.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.awesomepizza.dto.CustomerOrderDTO;
import com.awesomepizza.service.ApiKeyAuthService;
import com.awesomepizza.service.CustomerOrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class CustomerOrderController {

	private final CustomerOrderService customerOrderService;
	private final ApiKeyAuthService authService;

	public CustomerOrderController(CustomerOrderService customerOrderService, ApiKeyAuthService authService) {
		this.customerOrderService = customerOrderService;
		this.authService = authService;
	}

	// ================================================================================
	// API CLIENTE (no auth)
	// ================================================================================

	/**
	 * Creazione di un nuovo ordine
	 * 
	 * POST /api/orders
	 */
	@PostMapping
	public ResponseEntity<CustomerOrderDTO> createOrder(@Valid @RequestBody CustomerOrderDTO orderDTO) {
		CustomerOrderDTO createdOrder = customerOrderService.createOrder(orderDTO);
		return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
	}

	/**
	 * Ottenere lo stato dell’ordine dato il codice di tracking
	 * 
	 * GET /api/orders/{orderCode}
	 */
	@GetMapping("/{orderCode}")
	public ResponseEntity<CustomerOrderDTO> getOrderByCode(@PathVariable String orderCode) {
		CustomerOrderDTO order = customerOrderService.getOrderByCode(orderCode);
		return ResponseEntity.ok(order);
	}

	// ================================================================================
	// API PIZZAIOLO (api key auth)
	// ================================================================================

	/**
	 * Modifica lo stato dell'ordine
	 * 
	 * PUT /api/orders/{orderId}/status
	 */
	@PutMapping("/{orderId}/status")
	public ResponseEntity<CustomerOrderDTO> updateOrderStatus(@RequestHeader("X-API-KEY") String apiKey,
			@PathVariable Long orderId, @RequestBody String status) {

		if (!authService.isValidApiKey(apiKey)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		return ResponseEntity.ok(customerOrderService.updateOrderStatus(orderId, status));
	}

	/**
	 * Recupero prossimo ordine in coda
	 * 
	 * GET /api/orders/next
	 */
	@GetMapping("/next")
	public ResponseEntity<CustomerOrderDTO> getNextOrder(@RequestHeader("X-API-KEY") String apiKey) {

		if (!authService.isValidApiKey(apiKey)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Optional<CustomerOrderDTO> nextOrder = customerOrderService.getNextOrder();
		return nextOrder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build()); // 204 se non ci
																										// sono ordini
	}

}
