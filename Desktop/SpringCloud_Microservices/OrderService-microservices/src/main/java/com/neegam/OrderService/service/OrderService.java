package com.neegam.OrderService.service;

import com.neegam.OrderService.model.OrderRequest;
import com.neegam.OrderService.model.OrderResponse;

public interface OrderService{

	long placeOrder(OrderRequest orderRequest);

	OrderResponse getOrderDetails(long id);
	
}