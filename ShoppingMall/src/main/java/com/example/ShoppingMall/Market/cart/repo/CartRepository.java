package com.example.ShoppingMall.Market.cart.repo;

import com.example.ShoppingMall.Market.cart.entity.CartEntity;
import com.example.ShoppingMall.Market.cart.entity.CartItemEntity;
import com.example.ShoppingMall.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByUser(UserEntity user);

    @Query("SELECT ci FROM CartItemEntity ci WHERE ci.cart.id = :cartId")
    List<CartItemEntity> findCartItemsByCartId(Long cartId);

}
