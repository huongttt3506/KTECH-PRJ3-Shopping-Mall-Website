package com.example.ShoppingMall.Market.cart.entity;

import com.example.ShoppingMall.Market.item.entity.ItemEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//This entity is referred to cartId (CartEntity), itemId (ItemEntity).
//Uses to manage cart, which item user put into cart, and how many items?
public class CartItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //Users can put many items in their cart
    @JoinColumn(name = "cart_id")
    private CartEntity cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    private int quantity;
    // Method to create cartItem to be added to the shopping cart
    public static CartItemEntity createCartItem(
            CartEntity cart,
            ItemEntity item,
            int quantity
    ) {
        CartItemEntity cartItem = new CartItemEntity();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setQuantity(quantity);
        return cartItem;
    }
    // Increase the number of items in cart
    public void addQuantity(int quantity){
        this.quantity += quantity;
    }
    // Decrease the number of item in cart
    public void decreaseQuantity(int quantity) {
        if (this.quantity < quantity) {
            throw new IllegalArgumentException("Quantity cannot be less than zero");
        }
        this.quantity -= quantity;
    }
    // Reflect the quantity to be added to the cart
    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }
}
