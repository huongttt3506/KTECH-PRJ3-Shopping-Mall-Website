package com.example.ShoppingMall.Market.shop.entity;

import com.example.ShoppingMall.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private ShopCategory category;

    @Enumerated(EnumType.STRING)
    private ShopRegStatus status;

    private String declineReason;

    @ManyToOne
    private UserEntity owner;

    private String businessNum;

    @OneToOne
    @JoinColumn(name = "shop_id")
    private ShopEntity shop;



}
