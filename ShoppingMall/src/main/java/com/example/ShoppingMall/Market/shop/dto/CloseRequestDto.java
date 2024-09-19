package com.example.ShoppingMall.Market.shop.dto;

import com.example.ShoppingMall.Market.shop.entity.ShopCloseRequest;
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
