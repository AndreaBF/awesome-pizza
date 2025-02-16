package com.awesomepizza.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.awesomepizza.dto.CustomerOrderDTO;
import com.awesomepizza.dto.CustomerOrderPizzaDTO;
import com.awesomepizza.model.Crust;
import com.awesomepizza.model.CustomerOrder;
import com.awesomepizza.model.Ingredient;
import com.awesomepizza.model.Pizza;
import com.awesomepizza.model.Size;
import com.awesomepizza.repository.CrustRepository;
import com.awesomepizza.repository.CustomerOrderRepository;
import com.awesomepizza.repository.IngredientRepository;
import com.awesomepizza.repository.PizzaRepository;
import com.awesomepizza.repository.SizeRepository;

@ExtendWith(MockitoExtension.class)
public class CustomerOrderServiceTest {

	// ================================================================================
	// REPOSITORY CON MOCK
	// ================================================================================

	@Mock
	private CustomerOrderRepository customerOrderRepository;
	@Mock
	private PizzaRepository pizzaRepository;
	@Mock
	private SizeRepository sizeRepository;
	@Mock
	private CrustRepository crustRepository;
	@Mock
	private IngredientRepository ingredientRepository;

	// ================================================================================
	// SERVIZIO
	// ================================================================================

	@InjectMocks
	private CustomerOrderService customerOrderService;

	// ================================================================================
	// TEST
	// ================================================================================

	@BeforeEach
	void setUp() {
		// Mock delle pizze del test
		mockPizza("BUFALINA", "Pizza with bufala cheese");
		mockPizza("PEPPERONI", "Delicious pepperoni pizza");

		// Mock formati
		mockSize("BABY", "Baby format");
		mockSize("STANDARD", "Standard format");

		// Mock impasti
		mockCrust("NORMAL", "Normal crust");
		mockCrust("MULTI_GRAIN", "Multi-grain crust");

		// Mock ingredienti
		mockIngredient("MUSHROOMS", "Extra mushrooms");

		// Mock per replicare comportamento al salvataggio ordine
		when(customerOrderRepository.save(any(CustomerOrder.class))).thenAnswer(invocation -> {
			CustomerOrder capturedOrder = invocation.getArgument(0); // Cattura l'oggetto passato
			capturedOrder.setId(1L); // Imposta l'ID
			return capturedOrder; // Ritorna l'oggetto con l'ID
		});
	}

	@Test
	public void testCreateOrder() {
		// TODO separare DTO?

		CustomerOrderPizzaDTO pizzaDTO1 = new CustomerOrderPizzaDTO(null, "BUFALINA", // Nome della pizza
				"NORMAL", // Tipo di impasto
				"STANDARD", // Formato della pizza
				List.of("MUSHROOMS"), // Ingredienti extra
				null);

		CustomerOrderPizzaDTO pizzaDTO2 = new CustomerOrderPizzaDTO(null, "PEPPERONI", // Nome della pizza
				"MULTI_GRAIN", // Tipo di impasto
				"BABY", // Formato della pizza
				List.of(), // Ingredienti extra
				null);

		// Crea il DTO di input
		CustomerOrderDTO inputOrderDTO = new CustomerOrderDTO(null, null, null, "John Doe", "1234567890",
				List.of(pizzaDTO1, pizzaDTO2));

		// Richiamo creazione ordine su service
		CustomerOrderDTO createdOrderDTO = customerOrderService.createOrder(inputOrderDTO);

		// Verifica del risultato
		assertNotNull(createdOrderDTO);
		// Verifica che il codice di tracking sia stato generato
		assertNotNull(createdOrderDTO.trackingCode(), "Tracking code should be generated");
		assertEquals("John Doe", createdOrderDTO.customerName());
		assertEquals("1234567890", createdOrderDTO.contactNumber());
		assertEquals(2, createdOrderDTO.pizzas().size());
		Map<String, CustomerOrderPizzaDTO> pizzasMap = createdOrderDTO.pizzas().stream()
				.collect(Collectors.toMap(CustomerOrderPizzaDTO::pizzaCode, Function.identity()));
		CustomerOrderPizzaDTO pizza1 = pizzasMap.get("BUFALINA");
		assertNotNull(pizza1);
		assertEquals("NORMAL", pizza1.crustCode());
		assertEquals("STANDARD", pizza1.sizeCode());
		assertEquals(1, pizza1.extraIngredientCodes().size());
		assertEquals("MUSHROOMS", pizza1.extraIngredientCodes().get(0));
		CustomerOrderPizzaDTO pizza2 = pizzasMap.get("PEPPERONI");
		assertNotNull(pizza2);
		assertEquals("MULTI_GRAIN", pizza2.crustCode());
		assertEquals("BABY", pizza2.sizeCode());
		assertTrue(pizza2.extraIngredientCodes().isEmpty());

		// Verifica che il metodo di salvataggio sia stato chiamato
		verify(customerOrderRepository, times(1)).save(any(CustomerOrder.class));
		// Verifica query su repository
		verify(pizzaRepository, times(1)).findByCode("BUFALINA");
		verify(pizzaRepository, times(1)).findByCode("PEPPERONI");
		verify(crustRepository, times(1)).findByCode("NORMAL");
		verify(crustRepository, times(1)).findByCode("MULTI_GRAIN");
		verify(sizeRepository, times(1)).findByCode("STANDARD");
		verify(sizeRepository, times(1)).findByCode("BABY");
		verify(ingredientRepository, times(1)).findByCode("MUSHROOMS");
	}

	// ================================================================================
	// UTILS
	// ================================================================================

	private void mockPizza(String code, String description) {
		Pizza mockPizza = new Pizza();
		mockPizza.setCode(code);
		mockPizza.setDescription(description);

		when(pizzaRepository.findByCode(code)).thenReturn(Optional.of(mockPizza));
	}

	private void mockSize(String code, String description) {
		Size mockSize = new Size();
		mockSize.setCode(code);
		mockSize.setDescription(description);

		when(sizeRepository.findByCode(code)).thenReturn(Optional.of(mockSize));
	}

	private void mockCrust(String code, String description) {
		Crust mockCrust = new Crust();
		mockCrust.setCode(code);
		mockCrust.setDescription(description);

		when(crustRepository.findByCode(code)).thenReturn(Optional.of(mockCrust));
	}

	private void mockIngredient(String code, String description) {
		Ingredient mockIngredient = new Ingredient();
		mockIngredient.setCode(code);
		mockIngredient.setDescription(description);

		when(ingredientRepository.findByCode(code)).thenReturn(Optional.of(mockIngredient));
	}

}
