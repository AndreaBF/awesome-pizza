package com.awesomepizza.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.awesomepizza.dto.CustomerOrderDTO;
import com.awesomepizza.service.CustomerOrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class CustomerOrderController {

	private final CustomerOrderService customerOrderService;

	public CustomerOrderController(CustomerOrderService customerOrderService) {
		this.customerOrderService = customerOrderService;
	}

	// Creazione di un nuovo ordine
	@PostMapping
	public ResponseEntity<CustomerOrderDTO> createOrder(@Valid @RequestBody CustomerOrderDTO orderDTO) {
		CustomerOrderDTO createdOrder = customerOrderService.createOrder(orderDTO);
		return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
	}

//	// Ottenere un ordine specifico
//	@GetMapping("/{orderId}")
//	public ResponseEntity<CustomerOrderDTO> getOrder(@PathVariable Long orderId) {
//		CustomerOrderDTO orderDTO = customerOrderService.getOrder(orderId);
//		return new ResponseEntity<>(orderDTO, HttpStatus.OK);
//	}
//
//	// Ottenere tutti gli ordini
//	@GetMapping
//	public ResponseEntity<List<CustomerOrderDTO>> getAllOrders() {
//		List<CustomerOrderDTO> orders = customerOrderService.getAllOrders();
//		return new ResponseEntity<>(orders, HttpStatus.OK);
//	}
//
//	// Modifica lo stato dell'ordine
//	@PutMapping("/{orderId}/status")
//	public ResponseEntity<CustomerOrderDTO> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
//		CustomerOrderDTO updatedOrder = customerOrderService.updateOrderStatus(orderId, status);
//		return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
//	}
}
