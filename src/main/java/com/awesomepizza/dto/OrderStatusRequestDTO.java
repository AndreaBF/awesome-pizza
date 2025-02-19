package com.awesomepizza.dto;

import com.awesomepizza.model.CustomerOrder.OrderStatus;

public record OrderStatusRequestDTO( //
		OrderStatus status) {
}