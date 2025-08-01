package com.example.obs_test.repository;

import com.example.obs_test.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
	Optional<Order> findByOrderNo(String orderNo);
}
