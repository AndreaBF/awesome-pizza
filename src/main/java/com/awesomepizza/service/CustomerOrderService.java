package com.awesomepizza.service;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.awesomepizza.dto.CustomerOrderDTO;
import com.awesomepizza.dto.CustomerOrderPizzaDTO;
import com.awesomepizza.model.CustomerOrder;
import com.awesomepizza.model.CustomerOrder.OrderStatus;
import com.awesomepizza.model.CustomerOrderPizza;
import com.awesomepizza.repository.CrustRepository;
import com.awesomepizza.repository.CustomerOrderRepository;
import com.awesomepizza.repository.IngredientRepository;
import com.awesomepizza.repository.PizzaRepository;
import com.awesomepizza.repository.SizeRepository;

@Service
public class CustomerOrderService {

	@Autowired
	private CustomerOrderRepository customerOrderRepository;
	@Autowired
	private PizzaRepository pizzaRepository;
	@Autowired
	private SizeRepository sizeRepository;
	@Autowired
	private CrustRepository crustRepository;
	@Autowired
	private IngredientRepository ingredientRepository;

	// ================================================================================
	// CREAZIONE ORDINE
	// ================================================================================

	public CustomerOrderDTO createOrder(CustomerOrderDTO orderDTO) {

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

			customerOrderPizza.setExtraIngredients(orderPizzaDTO.extraIngredientCodes().stream()
					.map(ingredientCode -> ingredientRepository.findByCode(ingredientCode)
							.orElseThrow(() -> new RuntimeException(
									String.format("Ingredient with code '%s' not found", ingredientCode))))
					.collect(Collectors.toList()));

			customerOrder.addToPizzas(customerOrderPizza);
		}

		// Salvataggio dell'ordine nel database
		customerOrder = customerOrderRepository.save(customerOrder);

		// Restituire il DTO dell'ordine creato (utilizzando il costruttore automatico
		// del record)

		// TODO verificare se viene fuori prezzo totale dell'ordine

		return new CustomerOrderDTO(customerOrder.getId(), customerOrder.getTrackingCode(), customerOrder.getStatus(),
				customerOrder.getCustomerName(), customerOrder.getContactNumber(),
				customerOrder.getPizzas().stream().map(this::convertPizzaToDTO).collect(Collectors.toList()));
	}

	private String generateTrackingCode() {
		// Generazione del codice di tracking usando data + UUID
		return "ORDER-" + LocalDate.now().toString() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
	}

	// Metodo di supporto per la mappatura di una Pizza a PizzaDTO
	private CustomerOrderPizzaDTO convertPizzaToDTO(CustomerOrderPizza customerOrderPizza) {
		return new CustomerOrderPizzaDTO(customerOrderPizza.getPizza().getId(), customerOrderPizza.getPizza().getCode(),
				customerOrderPizza.getSelectedCrust().getCode(), customerOrderPizza.getSelectedSize().getCode(),
				customerOrderPizza.getExtraIngredients().stream().map(ing -> ing.getCode())
						.collect(Collectors.toList()),
				customerOrderPizza.getFinalPrice());
	}

//	// Metodo per ottenere un ordine completo con le pizze
//	public CustomerOrderDTO getCustomerOrder(Long orderId) {
//		// Recupera l'entità dell'ordine dal database
//		CustomerOrder order = repositories.getCustomerOrderRepository().findById(orderId)
//				.orElseThrow(() -> new RuntimeException("Order not found"));
//
//		// Mappa l'entità CustomerOrder a CustomerOrderDTO
//		List<CustomerOrderPizzaDTO> pizzaDTOs = order.getPizzas().stream().map(this::convertPizzaToDTO) // Mappa
//																										// ciascuna
//																										// pizza a
//				// PizzaDTO
//				.collect(Collectors.toList());
//
//		// Crea il CustomerOrderDTO con la lista di PizzaDTO
//		return new CustomerOrderDTO(order.getId(), order.getCustomerName(), order.getContactNumber(), pizzaDTOs);
//	}
//

	// Recuperare tutti gli ordini
	// TODO non dovrebbe essere esposta al di fuori del pizzaiolo sta cosa
//	public List<CustomerOrderDTO> getAllOrders() {
//		return customerOrderRepository.findAll().stream().map(CustomerOrderDTO::new).collect(Collectors.toList());
//	}
//
//	// Aggiornare lo stato dell'ordine
//	public CustomerOrderDTO updateOrderStatus(Long orderId, String status) {
//		Optional<CustomerOrder> order = customerOrderRepository.findById(orderId);
//		if (order.isPresent()) {
//			order.get().setStatus(status);
//			CustomerOrder updatedOrder = customerOrderRepository.save(order.get());
//			return new CustomerOrderDTO(updatedOrder);
//		} else {
//			throw new OrderNotFoundException("Order not found with id " + orderId);
//		}
//	}
}
