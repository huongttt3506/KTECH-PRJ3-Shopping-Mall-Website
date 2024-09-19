package com.example.ShoppingMall.Market.cart.entity;

import com.example.ShoppingMall.Market.item.entity.ItemEntity;
import com.example.ShoppingMall.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import javax.transaction.xa.XAResource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="cart")
// Manage user's cart, store UserId, and cartId.
//CartId will be referred to CartItem entity(with cartId)
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

//    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<CartItemEntity> cartItems;

    //Method to create a cart
    public static CartEntity createCart(UserEntity user){
        CartEntity  cart = new CartEntity();
        cart.setUser(user);
        return cart;
    }

}
