package com.example.ShoppingMall.ShoppingMall.shop.repo;

import com.example.ShoppingMall.ShoppingMall.shop.entity.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<ShopEntity, Long> {
}
