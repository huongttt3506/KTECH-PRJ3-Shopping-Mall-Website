package com.example.ShoppingMall.Market.shop.repo;

import com.example.ShoppingMall.Market.shop.entity.ShopCloseRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopCloseRepository extends JpaRepository<ShopCloseRequest, Long> {
    boolean existsByOwnerId(Long ownerId);

    boolean existsByShopId(Long id);

    List<ShopCloseRequest> findAllByShopId(Long shopId);
}
