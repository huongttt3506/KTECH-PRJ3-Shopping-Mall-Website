package com.example.ShoppingMall.Market.cart.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//Use CartDetailDto to display cart information to the user.
public class CartDetailsDto {
    private Long cartId;
    private List<CartItemDto> cartItems; // List items in cart
    private int totalAmount;

    //total amount is calculated base on cart item : sum of (price * quantity)

}
