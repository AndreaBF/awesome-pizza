package com.awesomepizza.dto;

import java.math.BigDecimal;
import java.util.List;

import com.awesomepizza.model.CustomerOrder.OrderStatus;

public record CustomerOrderDTO(Long orderId, // ID dell'ordine
		String trackingCode, // codice tracking ordine
		OrderStatus orderStatus, // stato dell'ordine
		String customerName, // Nome del cliente
		String contactNumber, // Contatto telefonico
		List<CustomerOrderPizzaDTO> pizzas // Lista delle pizze nell'ordine
) {
	// Metodo per calcolare il totale dell'ordine basato sulle pizze e i loro
	// ingredienti
	public BigDecimal calculateTotal() {
		return pizzas.stream().map(CustomerOrderPizzaDTO::price) // Calcola il prezzo base di ogni pizza
				.reduce(BigDecimal.ZERO, BigDecimal::add); // Somma tutti i prezzi
	}
}
