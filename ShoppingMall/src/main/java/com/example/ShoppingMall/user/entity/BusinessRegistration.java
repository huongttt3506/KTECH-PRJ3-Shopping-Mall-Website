package com.example.ShoppingMall.user.entity;

import com.example.ShoppingMall.ShoppingMall.shop.entity.ShopEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Foreign key to UserEntity
    private UserEntity user;

    @Column(nullable = false)
    private String businessNum;

    // One business registration corresponds to one shop
    @OneToOne
    @JoinColumn(name = "shop_id")
    private ShopEntity shop;
}
