package com.awesomepizza.model;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Pizza {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	@NotBlank
	private String code; // Identificativo per ogni pizza

	private String description; // eventuale descrizione se un domani volessimo riportarla nel menu

	@NotNull
	private BigDecimal basePrice; // Prezzo base di questa pizza, senza aggiunte

	@ManyToMany
	@JoinTable(name = "pizza_ingredient", joinColumns = @JoinColumn(name = "pizza_id"), inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
	private List<Ingredient> defaultIngredients; // Ingredienti predefiniti della pizza

	@ManyToMany
	@JoinTable(name = "pizza_size", joinColumns = @JoinColumn(name = "pizza_id"), inverseJoinColumns = @JoinColumn(name = "size_id"))
	private List<Size> availableSizes; // formati disponibili per questa pizza

	@ManyToMany
	@JoinTable(name = "pizza_crust", joinColumns = @JoinColumn(name = "pizza_id"), inverseJoinColumns = @JoinColumn(name = "crust_id"))
	private List<Crust> availableCrusts; // impasti disponibili per questa pizza

	public Pizza() {
	}

	public Pizza(String code, String description, BigDecimal basePrice, List<Ingredient> defaultIngredients,
			List<Size> availableSizes, List<Crust> availableCrusts) {
		this.code = code;
		this.description = description;
		this.basePrice = basePrice;
		this.defaultIngredients = defaultIngredients;
		this.availableSizes = availableSizes;
		this.availableCrusts = availableCrusts;
	}

	// Getter e setter

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	public List<Ingredient> getDefaultIngredients() {
		return defaultIngredients;
	}

	public void setDefaultIngredients(List<Ingredient> defaultIngredients) {
		this.defaultIngredients = defaultIngredients;
	}

	public List<Size> getAvailableSizes() {
		return availableSizes;
	}

	public void setAvailableSizes(List<Size> availableSizes) {
		this.availableSizes = availableSizes;
	}

	public List<Crust> getAvailableCrusts() {
		return availableCrusts;
	}

	public void setAvailableCrusts(List<Crust> availableCrusts) {
		this.availableCrusts = availableCrusts;
	}

}
