package com.example.ShoppingMall.Market.order.repo;

import com.example.ShoppingMall.Market.order.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

}
