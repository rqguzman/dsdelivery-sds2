package com.devsuperior.dsdeliver.services;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dsdeliver.dto.OrderDTO;
import com.devsuperior.dsdeliver.dto.ProductDTO;
import com.devsuperior.dsdeliver.entities.Order;
import com.devsuperior.dsdeliver.entities.OrderStatus;
import com.devsuperior.dsdeliver.entities.Product;
import com.devsuperior.dsdeliver.repositories.OrderRepository;
import com.devsuperior.dsdeliver.repositories.ProductRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Transactional(readOnly = true)
	public List<OrderDTO> findAll(){
		
		List<Order> list = orderRepository.findOrdersWithProducts();
		
		return list.stream()
				.map(x -> new OrderDTO(x)).collect(Collectors.toList());
		
	}

	@Transactional
	public OrderDTO insert(OrderDTO theOrderDTO){
		
		// Instantiate a new order with data from the received object
		Order order = new Order(null, theOrderDTO.getAddress(), theOrderDTO.getLatitude(),
				theOrderDTO.getLongitude(), Instant.now(),OrderStatus.PENDING);
		
		// associate products with this order(Feed  TB_ORDER_PRODUCT)
		for (ProductDTO productDTO : theOrderDTO.getProducts()) {
			//
			Product product = productRepository.getOne(productDTO.getId());
			order.getProducts().add(product);
		}
		
		order = orderRepository.save(order);
		
		return new OrderDTO(order);
	}
	
	@Transactional
	public OrderDTO setDelivered(Long theId){
		
		Order aNewOrder = orderRepository.getOne(theId);
		aNewOrder.setStatus(OrderStatus.DELIVERED);
		
		aNewOrder = orderRepository.save(aNewOrder);
		
		return new OrderDTO(aNewOrder);
	}
	
	
	
}
