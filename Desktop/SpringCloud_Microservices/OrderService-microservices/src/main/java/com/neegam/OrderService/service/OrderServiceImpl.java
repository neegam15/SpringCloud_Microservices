package com.neegam.OrderService.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.neegam.OrderService.entity.Order;
import com.neegam.OrderService.exception.CustomException;
import com.neegam.OrderService.external.client.PaymentService;
import com.neegam.OrderService.external.client.ProductService;
import com.neegam.OrderService.external.request.PaymentRequest;
import com.neegam.OrderService.model.OrderRequest;
import com.neegam.OrderService.model.OrderResponse;
import com.neegam.OrderService.model.OrderResponse.ProductDetails;
import com.neegam.OrderService.external.response.PaymentResponse;
import com.neegam.OrderService.external.response.ProductResponse;
import com.neegam.OrderService.repository.OrderRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public long placeOrder(OrderRequest orderRequest) {
		// 1. Order Entity -> Save the data with Status Order Created
		// 2. Product Service -> Block Products (Reduce the Quantity) ---- called using feign client
		// 3. Payment Service -> Payments -> Success -> Complete, Else -> Cancelled
		
		log.info("Placing order request: " + orderRequest);
		
//	2. option 2nd is implemented here.....first we called the product service to reduce the quantity then took the order
		productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());
		
//		1st option is implemented then here
		
		log.info("Creating Order with status CREATED");
		
		Order order = Order.builder()
						.amount(orderRequest.getTotalAmount())
						.orderStatus("CREATED")
						.productId(orderRequest.getProductId())
						.orderDate(Instant.now())
						.quantity(orderRequest.getQuantity())
						.build();
		
		order = orderRepository.save(order);
		
//		3rd option i.e. Payment Service is called from here. To do the payment.
		
		log.info("Calling Payment Service to complete the payment");
		
		PaymentRequest paymentRequest =
							PaymentRequest.builder()
								.orderId(order.getId())
								.paymentMode(orderRequest.getPaymentMode())
								.amount(orderRequest.getTotalAmount())
								.build();
		
		String orderStatus = null;
		try {
			paymentService.doPayment(paymentRequest);
			log.info("Payment done successfully. Changing the Order status to PLACED.");
			orderStatus = "PLACED";
		}catch(Exception e) {
			log.info("Error occured in Payment. Changing the Order status to FAILED.");
			orderStatus = "FAILED";
		}
		
		order.setOrderStatus(orderStatus);
		orderRepository.save(order);
		
		log.info("Order Placed successfully with order id: " + order.getId());
		
		return order.getId();
	}

	@Override
	public OrderResponse getOrderDetails(long id) {
		log.info("Get order details for OrderId: {}", id);
		
//		I
		Order order = orderRepository.findById(id).orElseThrow(
								() -> new CustomException("Order not found for the id:"+id,
										"NOT_FOUND",
										404));
		
//		II
//		Invoking Product Service to get the Product Information
		log.info("Invoking Product Service to fetch the product for id:" + order.getProductId());
		
		ProductResponse productResponse = restTemplate
											.getForObject("http://PRODUCT-SERVICE/product/"+order.getProductId(),
													ProductResponse.class);
		
//		III
//		Invoking Payment Service to get the Payment Information
		log.info("Getting Payment Information from the Payment Service for order Id:" + order.getId());
		
		PaymentResponse paymentResponse = restTemplate
											.getForObject("http://PAYMENT-SERVICE/payment/"+order.getId(),
													PaymentResponse.class);
		
//		IV
		OrderResponse.ProductDetails productDetails 
								= OrderResponse.ProductDetails
									.builder()
									.productName(productResponse.getProductName())
									.productId(productResponse.getProductId())
									.quantity(productResponse.getQuantity())
									.price(productResponse.getPrice())
									.build();
		
//		V
		OrderResponse.PaymentDetails paymentDetails
					= OrderResponse.PaymentDetails
						.builder()
						.paymentId(paymentResponse.getPaymentId())
						.status(paymentResponse.getStatus())
						.paymentDate(paymentResponse.getPaymentDate())
						.paymentMode(paymentResponse.getPaymentMode())
						.amount(paymentResponse.getAmount())
						.orderId(paymentResponse.getOrderId())
						.build();
		
		
//		VI
		OrderResponse orderResponse = OrderResponse.builder()
										.orderId(order.getId())
										.orderStatus(order.getOrderStatus())
										.amount(order.getAmount())
										.orderDate(order.getOrderDate())
										.productDetails(productDetails)
										.paymentDetails(paymentDetails)
										.build();
		
		return orderResponse;
	}

}
