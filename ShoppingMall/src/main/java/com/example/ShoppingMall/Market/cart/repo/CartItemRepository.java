package com.example.ShoppingMall.Market.cart.repo;

import com.example.ShoppingMall.Market.cart.dto.CartDetailsDto;
import com.example.ShoppingMall.Market.cart.entity.CartEntity;
import com.example.ShoppingMall.Market.cart.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    CartItemEntity findByCartIdAndItemId(Long cartId, Long itemId);

    @Query("SELECT new com.example.ShoppingMall.Market.cart.dto.CartDetailDto(" +
            "ci.id, i.name, i.price, ci.quantity, i.imgUrl) " +
            "FROM CartItemEntity ci " +
            "JOIN ci.item i " +
            "WHERE ci.cart.id = :cartId")
    List<CartDetailsDto> findCartDetailDtoList(Long id);

    List<CartItemEntity> findByCart(CartEntity cart);


    void deleteByCart(CartEntity cart);
}
