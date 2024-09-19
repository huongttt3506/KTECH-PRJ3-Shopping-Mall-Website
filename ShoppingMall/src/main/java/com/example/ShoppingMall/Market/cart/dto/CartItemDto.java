package com.example.ShoppingMall.Market.cart.dto;


import com.example.ShoppingMall.Market.cart.entity.CartItemEntity;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class CartItemDto {
    private Long itemId;
    private int quantity;


    public static CartItemDto fromEntity(CartItemEntity cartItemEntity) {
        return CartItemDto.builder()
                .itemId(cartItemEntity.getItem().getId())
                .quantity(cartItemEntity.getQuantity())
                .build();
    }
}
