package com.example.ShoppingMall.ShoppingMall.order.repo;


import com.example.ShoppingMall.ShoppingMall.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUser_Id(Long userId);
    List<OrderEntity> findByShop_Id(Long shopId);
}
