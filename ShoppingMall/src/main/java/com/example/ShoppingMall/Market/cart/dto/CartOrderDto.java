package com.example.ShoppingMall.Market.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


// For transmitting items data to be ordered
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//When the user decides to place an order,
// convert from CartOrderDto to OrderItemDto.
public class CartOrderDto {
    private List<CartItemDto> cartItems;
    private Long cartId;

}
