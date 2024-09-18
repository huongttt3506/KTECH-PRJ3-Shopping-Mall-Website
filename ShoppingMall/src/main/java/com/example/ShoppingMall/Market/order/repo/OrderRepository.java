package com.example.ShoppingMall.Market.order.repo;


import com.example.ShoppingMall.Market.order.entity.OrderEntity;
import com.example.ShoppingMall.Market.shop.entity.ShopEntity;
import com.example.ShoppingMall.user.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUserId(Long userId);
    List<OrderEntity> findByShopId(Long shopId, Pageable pageable);

    // Find orders by user
    List<OrderEntity> findByUser(UserEntity user);

    // Find orders by shop
    List<OrderEntity> findByShop(ShopEntity shop);
}
