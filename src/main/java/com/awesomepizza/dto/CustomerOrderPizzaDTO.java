package com.awesomepizza.dto;

import java.math.BigDecimal;
import java.util.List;

public record CustomerOrderPizzaDTO( //
		Long pizzaId, // ID della pizza nel menu
		String pizzaCode, // Nome della pizza
		String crustCode, // Tipo di impasto
		String sizeCode, // Formato della pizza
		List<String> extraIngredientCodes, // Ingredienti aggiunti alla pizza
		BigDecimal price // Prezzo base della pizza
) {
}