package com.neegam.PaymentService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neegam.PaymentService.model.PaymentRequest;
import com.neegam.PaymentService.model.PaymentResponse;
import com.neegam.PaymentService.service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController{

	@Autowired
	private PaymentService paymentService;
	
	@PostMapping
	public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest){
		return new ResponseEntity<>(
						paymentService.doPayment(paymentRequest),
						HttpStatus.OK);
	}
	
	@GetMapping("/{orderId}")
	public ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(@PathVariable int orderId){
		return new ResponseEntity<>(
				paymentService.getPaymentDetailsByOrderId(orderId),
				HttpStatus.OK);
}
}