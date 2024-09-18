package com.example.ShoppingMall.ShoppingMall.shop.dto;

import com.example.ShoppingMall.ShoppingMall.shop.entity.ShopCategory;
import com.example.ShoppingMall.ShoppingMall.shop.entity.ShopCloseRequest;
import com.example.ShoppingMall.ShoppingMall.shop.entity.ShopEntity;
import com.example.ShoppingMall.ShoppingMall.shop.entity.ShopStatus;
import com.example.ShoppingMall.user.dto.BusinessRegistrationDto;
import com.example.ShoppingMall.user.entity.BusinessRegistration;
import com.example.ShoppingMall.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopDto {
    private Long id;
    private String name;
    private String description;
    private ShopCategory category;
    private ShopStatus status;
    private LocalDateTime lastPurchased;
    private Long ownerId;
    private String businessNum;
    private List<Long> closeRequestIds;


    public static ShopDto fromEntity(ShopEntity shopEntity) {
        List<Long> closeRequestIds = shopEntity.getCloseRequests() != null ?
                shopEntity.getCloseRequests().stream()
                        .map(ShopCloseRequest::getId)
                        .collect(Collectors.toList()) :
                Collections.emptyList();

        return ShopDto.builder()
                .id(shopEntity.getId())
                .name(shopEntity.getName())
                .description(shopEntity.getDescription())
                .category(shopEntity.getCategory())
                .status(shopEntity.getStatus())
                .lastPurchased(shopEntity.getLastPurchased())
                .ownerId(shopEntity.getOwner().getId())
                .businessNum(shopEntity.getBusinessNum())
                .closeRequestIds(closeRequestIds)
                .build();
    }
}