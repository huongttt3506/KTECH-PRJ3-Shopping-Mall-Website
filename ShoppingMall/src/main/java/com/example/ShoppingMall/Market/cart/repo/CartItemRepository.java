package com.example.ShoppingMall.Market.cart.repo;

import com.example.ShoppingMall.Market.cart.dto.CartDetailsDto;
import com.example.ShoppingMall.Market.cart.entity.CartEntity;
import com.example.ShoppingMall.Market.cart.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    CartItemEntity findByCartIdAndItemId(Long cartId, Long itemId);


    List<CartItemEntity> findByCart(CartEntity cart);


    void deleteByCart(CartEntity cart);
}
