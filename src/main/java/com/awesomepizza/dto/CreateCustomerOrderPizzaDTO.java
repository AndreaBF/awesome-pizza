package com.awesomepizza.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

// DTO per le pizze in CREAZIONE ordine
public record CreateCustomerOrderPizzaDTO( //
		@NotNull(message = "Pizza code cannot be null") String pizzaCode, // Nome della pizza
		@NotNull(message = "Crust code cannot be null") String crustCode, // Tipo di impasto
		@NotNull(message = "Size code cannot be null") String sizeCode, // Formato della pizza
		List<String> extraIngredientCodes // Ingredienti aggiunti alla pizza
) {
}