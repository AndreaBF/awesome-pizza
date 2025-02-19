package com.awesomepizza.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO usato in CREAZIONE ordine
public record CreateCustomerOrderDTO(//
		@NotBlank(message = "Customer name is required") String customerName, // Nome cliente
		@NotBlank(message = "Contact number is required") String contactNumber, // Contatto telefonico
		@NotNull(message = "Pizza list cannot be null") @Valid List<CreateCustomerOrderPizzaDTO> pizzas // Lista delle
																										// pizze
																										// nell'ordine
) {
}
