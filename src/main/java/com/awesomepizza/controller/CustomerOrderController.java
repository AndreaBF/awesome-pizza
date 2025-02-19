package com.awesomepizza.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.awesomepizza.dto.CreateCustomerOrderDTO;
import com.awesomepizza.dto.CustomerOrderResponseDTO;
import com.awesomepizza.dto.OrderStatusRequestDTO;
import com.awesomepizza.service.ApiKeyAuthService;
import com.awesomepizza.service.CustomerOrderService;
import com.awesomepizza.util.ApiResponseUtil;

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
	public ResponseEntity<CustomerOrderResponseDTO> createOrder(
			@Valid @RequestBody CreateCustomerOrderDTO creteOrderDTO) {
		CustomerOrderResponseDTO createdOrder = customerOrderService.createOrder(creteOrderDTO);
		return ApiResponseUtil.success(createdOrder);
	}

	/**
	 * Ottenere lo stato dell’ordine dato il codice di tracking
	 * 
	 * GET /api/orders/{orderCode}
	 */
	@GetMapping("/{orderCode}")
	public ResponseEntity<CustomerOrderResponseDTO> getOrderByCode(@PathVariable String orderCode) {
		CustomerOrderResponseDTO order = customerOrderService.getOrderByCode(orderCode);
		return ApiResponseUtil.success(order);
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
	public ResponseEntity<CustomerOrderResponseDTO> updateOrderStatus(@RequestHeader("X-API-KEY") String apiKey,
			@PathVariable Long orderId, @RequestBody OrderStatusRequestDTO statusDTO) {

		if (!authService.isValidApiKey(apiKey))
			return ApiResponseUtil.unauthorized(null);

		return ApiResponseUtil.success(customerOrderService.updateOrderStatus(orderId, statusDTO));
	}

	/**
	 * Recupero prossimo ordine in coda
	 * 
	 * GET /api/orders/next
	 */
	@GetMapping("/next")
	public ResponseEntity<CustomerOrderResponseDTO> getNextOrder(@RequestHeader("X-API-KEY") String apiKey) {

		if (!authService.isValidApiKey(apiKey))
			return ApiResponseUtil.unauthorized(null);

		// se non trovo errori, in automatico verrà gestito mediante eccezione
		return ApiResponseUtil.success(customerOrderService.getNextOrder());
	}

}
