package com.example.ShoppingMall.Market.item.repo;

import com.example.ShoppingMall.Market.item.entity.ItemEntity;
import com.example.ShoppingMall.Market.item.entity.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.math.BigDecimal;
import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

    List<ItemEntity> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<ItemEntity> findAllByShopId(Long shopId);
}
