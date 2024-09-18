package com.example.ShoppingMall.ShoppingMall.shop.dto;

import com.example.ShoppingMall.ShoppingMall.shop.entity.ShopCategory;
import com.example.ShoppingMall.ShoppingMall.shop.entity.ShopCloseRequest;
import com.example.ShoppingMall.ShoppingMall.shop.entity.ShopEntity;
import com.example.ShoppingMall.ShoppingMall.shop.entity.ShopStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloseRequestDto {
    private String reason;

    public static CloseResponseDto fromEntiy(ShopCloseRequest entity) {
        return CloseResponseDto.builder()
                .reason(entity.getReason())
                .build();
    }
}
