package com.neegam.OrderService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.neegam.OrderService.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
	
}