package com.awesomepizza.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class CustomerOrder {

	public enum OrderStatus {
		PENDING, // Ordine in attesa
		IN_PROGRESS, // Ordine in lavorazione
		READY, // Pizza pronta
		COMPLETED, // Ordine completato
		CANCELLED; // Ordine cancellato
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String trackingCode; // Codice di tracking

	@NotNull
	private OrderStatus status;

	@NotNull
	private String customerName;

	@NotNull
	@Size(min = 10, max = 15)
	private String contactNumber; // Numero di telefono

	@OneToMany(mappedBy = "customerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CustomerOrderPizza> pizzas = new ArrayList<CustomerOrderPizza>();

	private LocalDateTime createdAt;

	public CustomerOrder() {
	}

	public CustomerOrder(String trackingCode, OrderStatus status, String customerName, String contactNumber,
			List<CustomerOrderPizza> pizzas) {
		this.trackingCode = trackingCode;
		this.status = status;
		this.customerName = customerName;
		this.contactNumber = contactNumber;
		this.pizzas = pizzas;
		this.createdAt = LocalDateTime.now();
	}

	public BigDecimal calculateTotalPrice() {
		return pizzas.stream().map(CustomerOrderPizza::getFinalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	// Getter e setter

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTrackingCode() {
		return trackingCode;
	}

	public void setTrackingCode(String trackingCode) {
		this.trackingCode = trackingCode;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public List<CustomerOrderPizza> getPizzas() {
		return pizzas;
	}

	public void setPizzas(List<CustomerOrderPizza> pizzas) {
		this.pizzas = pizzas;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void addToPizzas(CustomerOrderPizza pizza) {
		pizza.setCustomerOrder(this);
		this.pizzas.add(pizza);
	}

}