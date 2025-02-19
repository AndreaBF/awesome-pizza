package com.awesomepizza.dto;

import java.math.BigDecimal;
import java.util.List;

import com.awesomepizza.model.CustomerOrder.OrderStatus;

// DTO usato in RECUPERO ordine
public record CustomerOrderResponseDTO(//
		Long orderId, // ID dell'ordine
		String trackingCode, // codice tracking ordine
		OrderStatus orderStatus, // stato dell'ordine
		String customerName, // Nome del cliente
		String contactNumber, // Contatto telefonico
		List<CustomerOrderPizzaResponseDTO> pizzas, // Lista delle pizze
		// nell'ordine
		BigDecimal totalPrice // Prezzo base della pizza
) {
}
