package com.example.ShoppingMall.Market.shop.dto;

import com.example.ShoppingMall.Market.shop.entity.ShopCategory;
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
