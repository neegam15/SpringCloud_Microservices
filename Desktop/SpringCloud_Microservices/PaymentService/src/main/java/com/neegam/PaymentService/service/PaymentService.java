package com.neegam.PaymentService.service;

import com.neegam.PaymentService.model.PaymentRequest;
import com.neegam.PaymentService.model.PaymentResponse;

public interface PaymentService{

	long doPayment(PaymentRequest paymentRequest);

	PaymentResponse getPaymentDetailsByOrderId(int orderId);
	
}