package com.example.ShoppingMall.ShoppingMall.shop.dto;

import com.example.ShoppingMall.ShoppingMall.shop.entity.ShopCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ShopEssentialDto {
    private String name;
    private String description;
    private ShopCategory category;
}
