package com.awesomepizza.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.awesomepizza.dto.CustomerOrderDTO;
import com.awesomepizza.dto.CustomerOrderPizzaDTO;
import com.awesomepizza.dto.OrderStatusRequestDTO;
import com.awesomepizza.exception.NoOrdersAvailableException;
import com.awesomepizza.model.Crust;
import com.awesomepizza.model.CustomerOrder;
import com.awesomepizza.model.CustomerOrder.OrderStatus;
import com.awesomepizza.model.Ingredient;
import com.awesomepizza.model.Pizza;
import com.awesomepizza.model.Size;
import com.awesomepizza.repository.CrustRepository;
import com.awesomepizza.repository.CustomerOrderRepository;
import com.awesomepizza.repository.IngredientRepository;
import com.awesomepizza.repository.PizzaRepository;
import com.awesomepizza.repository.SizeRepository;

import jakarta.persistence.EntityNotFoundException;

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
	// CREAZIONE ORDINE VALIDO
	// ================================================================================

	@Test
	public void testCreateOrder() {
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

		// Crea due pizze di esempio con DTO
		CustomerOrderPizzaDTO pizzaDTO1 = new CustomerOrderPizzaDTO(null, "BUFALINA", "NORMAL", "STANDARD",
				List.of("MUSHROOMS"), null);
		CustomerOrderPizzaDTO pizzaDTO2 = new CustomerOrderPizzaDTO(null, "PEPPERONI", "MULTI_GRAIN", "BABY", List.of(),
				null);

		// Crea il DTO di input
		CustomerOrderDTO inputOrderDTO = new CustomerOrderDTO(null, null, null, "John Doe", "1234567890",
				List.of(pizzaDTO1, pizzaDTO2));

		// Richiamo creazione ordine su service
		CustomerOrderDTO createdOrderDTO = customerOrderService.createOrder(inputOrderDTO);

		// Verifica del risultato
		assertNotNull(createdOrderDTO);
		assertNotNull(createdOrderDTO.trackingCode(), "Tracking code should be generated");
		assertEquals("John Doe", createdOrderDTO.customerName());
		assertEquals("1234567890", createdOrderDTO.contactNumber());
		assertEquals(2, createdOrderDTO.pizzas().size());

		// Mappiamo le pizze per verificare i dettagli
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

		// Verifica delle chiamate al repository per pizze, impasti, formati e
		// ingredienti
		verify(pizzaRepository, times(1)).findByCode("BUFALINA");
		verify(pizzaRepository, times(1)).findByCode("PEPPERONI");
		verify(crustRepository, times(1)).findByCode("NORMAL");
		verify(crustRepository, times(1)).findByCode("MULTI_GRAIN");
		verify(sizeRepository, times(1)).findByCode("STANDARD");
		verify(sizeRepository, times(1)).findByCode("BABY");
		verify(ingredientRepository, times(1)).findByCode("MUSHROOMS");
	}

	// ================================================================================
	// CREAZIONE ORDINE CON PIZZA MANCANTE
	// ================================================================================

	@Test
	public void testCreateOrderWithNoPizzas() {
		// Crea il DTO di input con nessuna pizza
		CustomerOrderDTO inputOrderDTO = new CustomerOrderDTO(null, null, null, "John Doe", "1234567890", List.of());

		// Verifica che venga lanciata un'eccezione quando non ci sono pizze
		assertThrows(IllegalArgumentException.class, () -> {
			customerOrderService.createOrder(inputOrderDTO);
		});
	}

	// ================================================================================
	// RECUPERO ORDINE DA TRACKING CODE VALIDO
	// ================================================================================

	@Test
	public void testGetOrderByCode() {
		// Preparazione dell'ordine esistente
		CustomerOrder pizzaOrder = new CustomerOrder("tracking123", OrderStatus.PENDING, "John Doe", "1234567890",
				List.of());

		// simulo il ritorno di un ordine valido quando viene fatta la ricerca per
		// tracking code
		when(customerOrderRepository.findByTrackingCode(pizzaOrder.getTrackingCode()))
				.thenReturn(Optional.of(pizzaOrder));

		// Recupera l'ordine tramite tracking code
		CustomerOrderDTO orderByCodeDTO = customerOrderService.getOrderByCode(pizzaOrder.getTrackingCode());

		// Verifica che l'ordine recuperato sia valido
		assertNotNull(orderByCodeDTO);
		assertNotNull(orderByCodeDTO.trackingCode(), "Tracking code should be generated");
	}

	// ================================================================================
	// RECUPERO ORDINE DA TRACKING CODE INESISTENTE
	// ================================================================================

	@Test
	public void testGetOrderByCodeNotFound() {
		// Simula il comportamento di non trovare l'ordine
		when(customerOrderRepository.findByTrackingCode("INVALID_TRACKING_CODE")).thenReturn(Optional.empty());

		// Recupera l'ordine tramite un codice di tracking inesistente
		assertThrows(EntityNotFoundException.class, () -> {
			customerOrderService.getOrderByCode("INVALID_TRACKING_CODE");
		});
	}

	// ================================================================================
	// TEST PER L'API UPDATE ORDER STATUS
	// ================================================================================

	@Test
	public void testUpdateOrderStatus() {
		// Preparazione dell'ordine esistente
		CustomerOrder pizzaOrder = new CustomerOrder("tracking123", OrderStatus.IN_PROGRESS, "John Doe", "1234567890",
				List.of());
		pizzaOrder.setId(1L);

		// Mock per il comportamento del servizio che aggiorna lo stato dell'ordine
		when(customerOrderRepository.findById(1L)).thenReturn(Optional.of(pizzaOrder));
		when(customerOrderRepository.save(pizzaOrder)).thenAnswer(invocation -> {
			CustomerOrder capturedOrder = invocation.getArgument(0); // Cattura l'oggetto passato
			capturedOrder.setStatus(OrderStatus.COMPLETED);
			return capturedOrder; // Ritorna l'oggetto con l'ID
		});

		// Richiamo il metodo di aggiornamento dello stato dell'ordine nel servizio
		CustomerOrderDTO updatedOrder = customerOrderService.updateOrderStatus(1L,
				new OrderStatusRequestDTO(OrderStatus.COMPLETED));

		// Verifica che l'ordine sia stato aggiornato correttamente
		assertNotNull(updatedOrder);
		assertEquals(OrderStatus.COMPLETED, updatedOrder.orderStatus());

		// Verifica che il repository per aggiornare lo stato sia stato chiamato
		verify(customerOrderRepository, times(1)).save(any(CustomerOrder.class));
	}

	// ================================================================================
	// TEST PER L'API GET NEXT ORDER (ORDINE SUCCESSIVO)
	// ================================================================================

	@Test
	public void testGetNextOrder() {
		// Preparazione dell'ordine esistente
		CustomerOrder pizzaOrder = new CustomerOrder("tracking123", OrderStatus.PENDING, "John Doe", "1234567890",
				List.of());

		// Mock per il comportamento del servizio che restituisce il prossimo ordine
		when(customerOrderRepository.findFirstByStatusOrderByCreatedAtAsc(OrderStatus.PENDING))
				.thenReturn(Optional.of(pizzaOrder));

		// Richiamo il metodo del servizio
		CustomerOrderDTO nextOrderResult = customerOrderService.getNextOrder();

		// Verifica che il risultato sia presente e contenga i dati dell'ordine
		assertNotNull(nextOrderResult);
		assertEquals("tracking123", nextOrderResult.trackingCode());
		assertEquals(OrderStatus.PENDING, nextOrderResult.orderStatus());

		// Verifica che il metodo per ottenere il prossimo ordine sia stato chiamato
		verify(customerOrderRepository, times(1)).findFirstByStatusOrderByCreatedAtAsc(OrderStatus.PENDING);
	}

	// ================================================================================
	// TEST PER IL SERVIZIO GET NEXT ORDER (nessun ordine disponibile)
	// ================================================================================

	@Test
	public void testGetNextOrderNoOrders() {
		// Mock del servizio che restituisce nessun ordine
		when(customerOrderRepository.findFirstByStatusOrderByCreatedAtAsc(OrderStatus.PENDING))
				.thenReturn(Optional.empty());

		// Richiamo il metodo del servizio
		assertThrows(NoOrdersAvailableException.class, () -> customerOrderService.getNextOrder());

		// Verifica che il metodo per ottenere il prossimo ordine sia stato chiamato
		verify(customerOrderRepository, times(1)).findFirstByStatusOrderByCreatedAtAsc(OrderStatus.PENDING);
	}

	// ================================================================================
	// METODI DI MOCK
	// ================================================================================

	// Metodo di mock per la pizza
	private void mockPizza(String code, String description) {
		Pizza mockPizza = new Pizza();
		mockPizza.setCode(code);
		mockPizza.setDescription(description);
		when(pizzaRepository.findByCode(code)).thenReturn(Optional.of(mockPizza));
	}

	// Metodo di mock per le dimensioni
	private void mockSize(String code, String description) {
		Size mockSize = new Size();
		mockSize.setCode(code);
		mockSize.setDescription(description);
		when(sizeRepository.findByCode(code)).thenReturn(Optional.of(mockSize));
	}

	// Metodo di mock per l'impasto
	private void mockCrust(String code, String description) {
		Crust mockCrust = new Crust();
		mockCrust.setCode(code);
		mockCrust.setDescription(description);
		when(crustRepository.findByCode(code)).thenReturn(Optional.of(mockCrust));
	}

	// Metodo di mock per gli ingredienti
	private void mockIngredient(String code, String description) {
		Ingredient mockIngredient = new Ingredient();
		mockIngredient.setCode(code);
		mockIngredient.setDescription(description);
		when(ingredientRepository.findByCode(code)).thenReturn(Optional.of(mockIngredient));
	}

}
