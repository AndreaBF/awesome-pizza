package com.awesomepizza.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class CustomerOrderPizza {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "customer_order_id", nullable = false)
	private CustomerOrder customerOrder;

	@ManyToOne
	@JoinColumn(name = "pizza_id", nullable = false)
	private Pizza pizza;

	@ManyToOne
	@JoinColumn(name = "crust_id", nullable = false)
	private Crust selectedCrust;

	@ManyToOne
	@JoinColumn(name = "size_id", nullable = false)
	private Size selectedSize;

	@ManyToMany
	@JoinTable(name = "order_pizza_extra_ingredient", joinColumns = @JoinColumn(name = "order_pizza_id"), inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
	private List<Ingredient> extraIngredients; // Ingredienti extra selezionati

	private BigDecimal finalPrice; // Prezzo finale calcolato

	public CustomerOrderPizza() {
	}

	public CustomerOrderPizza(CustomerOrder customerOrder, Pizza pizza, Crust selectedCrust, Size selectedSize,
			List<Ingredient> extraIngredients) {
		this.customerOrder = customerOrder;
		this.pizza = pizza;
		this.selectedCrust = selectedCrust;
		this.selectedSize = selectedSize;
		this.extraIngredients = extraIngredients;
	}

	public BigDecimal calculatePrice() {
		BigDecimal price = Optional.ofNullable(pizza.getBasePrice()).orElse(BigDecimal.ZERO)
				.add(Optional.ofNullable(selectedCrust.getPrice()).orElse(BigDecimal.ZERO))
				.add(Optional.ofNullable(selectedSize.getPrice()).orElse(BigDecimal.ZERO));

		if (extraIngredients != null) {
			for (Ingredient ingredient : extraIngredients) {
				price = price.add(Optional.ofNullable(ingredient.getPrice()).orElse(BigDecimal.ZERO));
			}
		}

		return price;
	}

	// Getter e setter

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public Pizza getPizza() {
		return pizza;
	}

	public void setPizza(Pizza pizza) {
		this.pizza = pizza;
	}

	public Crust getSelectedCrust() {
		return selectedCrust;
	}

	public void setSelectedCrust(Crust selectedCrust) {
		this.selectedCrust = selectedCrust;
	}

	public Size getSelectedSize() {
		return selectedSize;
	}

	public void setSelectedSize(Size selectedSize) {
		this.selectedSize = selectedSize;
	}

	public List<Ingredient> getExtraIngredients() {
		return extraIngredients;
	}

	public void setExtraIngredients(List<Ingredient> extraIngredients) {
		this.extraIngredients = extraIngredients;
	}

	public BigDecimal getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(BigDecimal finalPrice) {
		this.finalPrice = finalPrice;
	}

}
