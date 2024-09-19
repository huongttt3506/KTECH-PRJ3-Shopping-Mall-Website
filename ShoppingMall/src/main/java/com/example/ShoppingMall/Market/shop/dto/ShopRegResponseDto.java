package com.example.ShoppingMall.Market.shop.dto;

import com.example.ShoppingMall.Market.shop.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopRegResponseDto {
    private Long id;
    private String name;
    private String description;
    private ShopCategory category;
    private ShopRegStatus status;
    private String declinedReason;
    private Long ownerId;
    private String businessNum;


    public static ShopRegResponseDto fromEntity(ShopRegistration entity) {
        return ShopRegResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .status(entity.getStatus())
                .declinedReason(entity.getDeclineReason())
                .ownerId(entity.getOwner().getId())
                .businessNum(entity.getBusinessNum())
                .build();
    }
}

