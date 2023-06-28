package com.neegam.OrderService.service;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.neegam.OrderService.entity.Order;
import com.neegam.OrderService.exception.CustomException;
import com.neegam.OrderService.external.client.PaymentService;
import com.neegam.OrderService.external.client.ProductService;
import com.neegam.OrderService.external.request.PaymentRequest;
import com.neegam.OrderService.external.response.PaymentResponse;
import com.neegam.OrderService.external.response.ProductResponse;
import com.neegam.OrderService.model.OrderRequest;
import com.neegam.OrderService.model.OrderResponse;
import com.neegam.OrderService.model.PaymentMode;
import com.neegam.OrderService.repository.OrderRepository;

@SpringBootTest
public class OrderServiceImplTest {

	@Mock
	private OrderRepository orderRepository;
	
	@Mock
	private ProductService productService;
	
	@Mock
	private PaymentService paymentService;
	
	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	OrderService orderService = new OrderServiceImpl();
	
	
//	---------------------GET ORDER DETAILS(2nd Method in ServiceImpl)---------------------
	
	
	@DisplayName("Get Orders - Success Scenario")
	@Test
	void test_When_Order_Success() {
		//1. MOCKING CALL---------------
		
//		Ist option is done here as in OrderServiceImpl.java 
		Order order = getMockOrder(); //getMockOrder method is created below
		
		when(orderRepository.findById(anyLong()))  //anyLong means any variable of Long type as the id is long
					.thenReturn(Optional.of(order));
		
//		IInd option is now 
		
		when(restTemplate.getForObject("http://PRODUCT-SERVICE/product/"+order.getProductId(),
						ProductResponse.class))
					.thenReturn(getMockProductResponse());  //getMockProductResponse method is created below
		
//		IIIrd option is now 
		
		when(restTemplate.getForObject("http://PAYMENT-SERVICE/payment/"+order.getId(),
					PaymentResponse.class))
					.thenReturn(getMockPaymentResponse());  //getMockPaymentResponse method is created below
		
		//2. ACTUAL CALL to the method--------------
		OrderResponse orderResponse = orderService.getOrderDetails(1);
		
		// 3. VERIFICATION------------
		
//		This is to verify orderRepository.fingById() is called only 1 time
		verify(orderRepository, times(1)).findById(anyLong()); //anyLong means any variable of Long type as the id is long
		
		verify(restTemplate, times(1)).getForObject("http://PRODUCT-SERVICE/product/"+order.getProductId(),
											ProductResponse.class);
		
		verify(restTemplate, times(1)).getForObject("http://PAYMENT-SERVICE/payment/"+order.getId(),
											PaymentResponse.class);
		
		//4. ASERTION----------------
		Assertions.assertNotNull(orderResponse);
		Assertions.assertEquals(order.getId(), orderResponse.getOrderId());
}
	

	@DisplayName("Get Orders - Failure Scenario")
	@Test
	void test_When_Get_Order_NOT_FOUND_then_Not_Found() {
		
		when(orderRepository.findById(anyLong()))  //anyLong means any variable of Long type as the id is long
				.thenReturn(Optional.ofNullable(null));
		
		
//		OrderResponse orderResponse = orderService.getOrderDetails(1);
		
		CustomException exception = Assertions.assertThrows(CustomException.class,
						() -> orderService.getOrderDetails(1));
		
		Assertions.assertEquals("NOT_FOUND", exception.getErrorCode());
		
		Assertions.assertEquals(404, exception.getStatus());
		
		verify(orderRepository, times(1)).findById(anyLong());
		
	}
	
//	---------------------PLACE ORDER(1st Method in ServiceImpl)---------------------
	
	
	
	@DisplayName("Place Order - Success Scenario")
	@Test
	void test_When_Place_Order_Success() {
		
		Order order = getMockOrder();
		OrderRequest orderRequest = getMockOrderRequest();
		
		when(orderRepository.save(any(Order.class)))  //if we r saving anything of class Order to repo then return order
			.thenReturn(order);
		
		when(productService.reduceQuantity(anyLong(),anyLong()))
			.thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
		
		when(paymentService.doPayment(any(PaymentRequest.class)))
		.thenReturn(new ResponseEntity<Long>(1L,HttpStatus.OK));
		
//		Actual Call to the method
		long orderId = orderService.placeOrder(orderRequest);
		
		verify(orderRepository, times(2)).save(any());
		
		verify(productService,times(1)).reduceQuantity(anyLong(),anyLong());
		
		verify(paymentService,times(1)).doPayment(any(PaymentRequest.class));
		
		Assertions.assertEquals(order.getId(),orderId);
		
	}
	
	@DisplayName("Placed Order - Payment Failed Scenario")
	@Test
	void test_When_Place_Order_Fails_then_Order_Placed() {
		
		Order order = getMockOrder();
		OrderRequest orderRequest = getMockOrderRequest();
		
		when(orderRepository.save(any(Order.class)))  //if we r saving anything of class Order to repo then return order
			.thenReturn(order);
		
		when(productService.reduceQuantity(anyLong(),anyLong()))
			.thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
		
		when(paymentService.doPayment(any(PaymentRequest.class)))
		.thenThrow(new RuntimeException());
		
//		Actual Call to the method
		long orderId = orderService.placeOrder(orderRequest);
		
		verify(orderRepository, times(2)).save(any());
		
		verify(productService,times(1)).reduceQuantity(anyLong(),anyLong());
		
		verify(paymentService,times(1)).doPayment(any(PaymentRequest.class));
		
		Assertions.assertEquals(order.getId(),orderId);
		
	}
	
	
	
	
	
	
	
private OrderRequest getMockOrderRequest() {
	return OrderRequest.builder()
				.productId(1)
				.quantity(10)
				.paymentMode(PaymentMode.CASH)
				.totalAmount(100)
				.build();
}


private PaymentResponse getMockPaymentResponse() {
	return PaymentResponse.builder()
			.paymentId(1)
			.paymentMode(PaymentMode.CASH)
			.paymentDate(Instant.now())
			.amount(200)
			.orderId(1)
			.status("ACCEPTED")
			.build();
	}


private ProductResponse getMockProductResponse() {
	return ProductResponse.builder()
			.productId(2)
			.productName("IPhone")
			.price(100)
			.quantity(200)
			.build();
}

private Order getMockOrder() {
	return Order.builder()
			.orderStatus("PLACED")
			.orderDate(Instant.now())
			.id(1)
			.amount(100)
			.quantity(200)
			.productId(2)
			.build();
}
	
}
