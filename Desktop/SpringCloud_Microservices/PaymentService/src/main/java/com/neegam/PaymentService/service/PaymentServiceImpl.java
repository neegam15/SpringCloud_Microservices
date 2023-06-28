package com.neegam.PaymentService.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neegam.PaymentService.entity.TransactionDetails;
import com.neegam.PaymentService.model.PaymentMode;
import com.neegam.PaymentService.model.PaymentRequest;
import com.neegam.PaymentService.model.PaymentResponse;
import com.neegam.PaymentService.repository.TransactionDetailsRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private TransactionDetailsRepository transRepo;
	
	@Override
	public long doPayment(PaymentRequest paymentRequest) {
		log.info("Recording Payment Details : {}", paymentRequest);

		TransactionDetails transactionDetails = 
				TransactionDetails.builder()
						.paymentDate(Instant.now())
						.paymentMode(paymentRequest.getPaymentMode().name())
						.paymentStatus("SUCCESS")
						.orderId(paymentRequest.getOrderId())
						.referenceNumber(paymentRequest.getReferenceNumber())
						.amount(paymentRequest.getAmount())
						.build();
			
		transRepo.save(transactionDetails);
		
		log.info("Transaction completed with Id : {}", transactionDetails.getId());
		
		return transactionDetails.getId();
	}

	@Override
	public PaymentResponse getPaymentDetailsByOrderId(int orderId) {
		
		log.info("Getting Payment Details for orderId :" + orderId);
		
		TransactionDetails transactionDetails = transRepo.findByOrderId(Long.valueOf(orderId));
		
		PaymentResponse paymentResponse = PaymentResponse
											.builder()
											.paymentId(transactionDetails.getId())
											.paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
											.orderId(transactionDetails.getOrderId())
											.status(transactionDetails.getPaymentStatus())
											.amount(transactionDetails.getAmount())
											.build();
		
		
		
		
		return paymentResponse;
	}

}
