package com.neegam.ProductService.service;

import com.neegam.ProductService.model.ProductRequest;
import com.neegam.ProductService.model.ProductResponse;

public interface ProductService {

	long addProduct(ProductRequest productRequest);

	ProductResponse getProductById(long productId);

	void reduceQuantity(long productId, long quantity);

}
