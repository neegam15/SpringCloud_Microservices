package com.neegam.OrderService.model;

import java.time.Instant;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {

	private long orderId;
	private Instant orderDate;
	private String orderStatus;
	private long amount;
	private ProductDetails productDetails;
	private PaymentDetails paymentDetails;
	
	@Data
	@NoArgsConstructor
	@Builder
	@AllArgsConstructor
	public static class ProductDetails{

		private String productName;
		private long productId;
		private long quantity;
		private long price;
	}
		
		@Data
		@AllArgsConstructor
		@NoArgsConstructor
		@Builder
	 public static class PaymentDetails {

			private long paymentId;
			private String status;
			private PaymentMode paymentMode;
			private long amount;
			private Instant paymentDate;
			private long orderId;
		}

}
