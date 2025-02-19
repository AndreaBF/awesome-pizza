package com.awesomepizza.dto;

import com.awesomepizza.model.CustomerOrder.OrderStatus;

import jakarta.validation.constraints.NotBlank;

// DTO usato in aggiornamento stato ordine
public record OrderStatusRequestDTO( //
		@NotBlank(message = "Status cannot be null") OrderStatus status) {
}