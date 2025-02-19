package com.awesomepizza.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.awesomepizza.dto.CustomerOrderDTO;
import com.awesomepizza.dto.CustomerOrderPizzaDTO;
import com.awesomepizza.dto.OrderStatusRequestDTO;
import com.awesomepizza.exception.NoOrdersAvailableException;
import com.awesomepizza.model.CustomerOrder;
import com.awesomepizza.model.CustomerOrder.OrderStatus;
import com.awesomepizza.model.CustomerOrderPizza;
import com.awesomepizza.repository.CrustRepository;
import com.awesomepizza.repository.CustomerOrderRepository;
import com.awesomepizza.repository.IngredientRepository;
import com.awesomepizza.repository.PizzaRepository;
import com.awesomepizza.repository.SizeRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CustomerOrderService {

	private static final Logger logger = LoggerFactory.getLogger(CustomerOrderService.class);

	// repository
	private final CustomerOrderRepository customerOrderRepository;
	private final PizzaRepository pizzaRepository;
	private final SizeRepository sizeRepository;
	private final CrustRepository crustRepository;
	private final IngredientRepository ingredientRepository;

	public CustomerOrderService(CustomerOrderRepository customerOrderRepository, PizzaRepository pizzaRepository,
			SizeRepository sizeRepository, CrustRepository crustRepository, IngredientRepository ingredientRepository) {
		this.customerOrderRepository = customerOrderRepository;
		this.pizzaRepository = pizzaRepository;
		this.sizeRepository = sizeRepository;
		this.crustRepository = crustRepository;
		this.ingredientRepository = ingredientRepository;
	}

	// ================================================================================
	// CREAZIONE ORDINE
	// ================================================================================

	public CustomerOrderDTO createOrder(CustomerOrderDTO orderDTO) {
		logger.info("Requested order creation");

		if (orderDTO.pizzas().isEmpty()) {
			throw new IllegalArgumentException("An order must contain at least one pizza.");
		}

		// TODO ci vorrebbe gestione di accettazione dell'ordine in base all'orario
		// probabilmente, ma ora per primo sprint accettiamo tutto e impostiamo tutto in
		// PENDING

		// Logica per creare l'ordine
		CustomerOrder customerOrder = new CustomerOrder();
		// Impostare i valori dell'ordine da orderDTO
		customerOrder.setCustomerName(orderDTO.customerName());
		customerOrder.setContactNumber(orderDTO.contactNumber());
		customerOrder.setStatus(OrderStatus.PENDING);
		customerOrder.setTrackingCode(generateTrackingCode());

		for (CustomerOrderPizzaDTO orderPizzaDTO : orderDTO.pizzas()) {
			CustomerOrderPizza customerOrderPizza = new CustomerOrderPizza();

			customerOrderPizza.setPizza(
					pizzaRepository.findByCode(orderPizzaDTO.pizzaCode()).orElseThrow(() -> new RuntimeException(
							String.format("Pizza with code '%s' not found", orderPizzaDTO.pizzaCode()))));

			customerOrderPizza.setSelectedCrust(
					crustRepository.findByCode(orderPizzaDTO.crustCode()).orElseThrow(() -> new RuntimeException(
							String.format("Crust with code '%s' not found", orderPizzaDTO.crustCode()))));

			customerOrderPizza.setSelectedSize(
					sizeRepository.findByCode(orderPizzaDTO.sizeCode()).orElseThrow(() -> new RuntimeException(
							String.format("Size with code '%s' not found", orderPizzaDTO.sizeCode()))));

			if (orderPizzaDTO.extraIngredientCodes() != null)
				customerOrderPizza.setExtraIngredients(orderPizzaDTO.extraIngredientCodes().stream()
						.map(ingredientCode -> ingredientRepository.findByCode(ingredientCode)
								.orElseThrow(() -> new RuntimeException(
										String.format("Ingredient with code '%s' not found", ingredientCode))))
						.collect(Collectors.toList()));

			customerOrder.addToPizzas(customerOrderPizza);
		}

		// Salvataggio dell'ordine nel database
		customerOrder = customerOrderRepository.save(customerOrder);

		logger.info("Order created");

		// Restituire il DTO dell'ordine creato (utilizzando il costruttore automatico
		// del record)

		// TODO verificare se viene fuori prezzo totale dell'ordine

		return toDTO(customerOrder);
	}

	private String generateTrackingCode() {
		// Generazione del codice di tracking usando data + UUID
		return "ORDER-" + LocalDate.now().toString() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
	}

	// ================================================================================
	// RECUPERO ORDINE DA TRACKING CODE
	// ================================================================================

	public CustomerOrderDTO getOrderByCode(String orderCode) {
		logger.info(String.format("Requested tracking info for order (%s)", orderCode));

		CustomerOrder order = customerOrderRepository.findByTrackingCode(orderCode)
				.orElseThrow(() -> new EntityNotFoundException(
						String.format("Order with tracking number (%s) not found", orderCode)));

		return toDTO(order);
	}

	// ================================================================================
	// AGGIORNAMENTO STATO ORDINE
	// ================================================================================

	public CustomerOrderDTO updateOrderStatus(Long orderId, OrderStatusRequestDTO statusDTO) {
		logger.info(String.format("Requested update order id (%s) to status (%s)", orderId,
				statusDTO != null ? statusDTO.status() : "-"));

		CustomerOrder order = customerOrderRepository.findById(orderId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Order with id (%s) not found", orderId)));

		if (statusDTO == null || statusDTO.status() == null)
			throw new IllegalArgumentException("Invalid order status");

		order.setStatus(statusDTO.status());

		customerOrderRepository.save(order);

		logger.info(String.format("Order id (%s) updated with status (%s)", orderId, statusDTO.status()));

		return toDTO(order);
	}

	// ================================================================================
	// RECUPERO PROSSIMO ORDINE
	// ================================================================================

	public CustomerOrderDTO getNextOrder() {
		logger.info("Fetching next order in queue...");

		return customerOrderRepository.findFirstByStatusOrderByCreatedAtAsc(OrderStatus.PENDING).map(this::toDTO)
				.orElseThrow(() -> new NoOrdersAvailableException("No orders are currently in progress."));
	}

	// ================================================================================
	// MAPPATURE ENTITY -> DTO (fatte in controller per semplicità)
	// ================================================================================

	public CustomerOrderDTO toDTO(CustomerOrder order) {
		return new CustomerOrderDTO(order.getId(), order.getTrackingCode(), order.getStatus(), order.getCustomerName(),
				order.getContactNumber(), order.getPizzas().stream().map(this::toDTO).collect(Collectors.toList()));
	}

	public CustomerOrderPizzaDTO toDTO(CustomerOrderPizza customerOrderPizza) {
		return new CustomerOrderPizzaDTO(customerOrderPizza.getPizza().getId(), customerOrderPizza.getPizza().getCode(),
				customerOrderPizza.getSelectedCrust().getCode(), customerOrderPizza.getSelectedSize().getCode(),
				(customerOrderPizza.getExtraIngredients() == null ? List.of()
						: customerOrderPizza.getExtraIngredients().stream().map(ing -> ing.getCode())
								.collect(Collectors.toList())),
				customerOrderPizza.getFinalPrice());
	}

}
