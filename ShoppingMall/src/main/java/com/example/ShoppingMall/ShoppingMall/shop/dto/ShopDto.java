package com.example.ShoppingMall.ShoppingMall.shop.dto;

import com.example.ShoppingMall.ShoppingMall.shop.entity.ShopCategory;
import com.example.ShoppingMall.ShoppingMall.shop.entity.ShopEntity;
import com.example.ShoppingMall.ShoppingMall.shop.entity.ShopStatus;
import com.example.ShoppingMall.user.dto.BusinessRegistrationDto;
import com.example.ShoppingMall.user.entity.BusinessRegistration;
import com.example.ShoppingMall.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Long ownerId;
    private BusinessRegistrationDto businessRegistration;


    public static ShopDto fromEntity(ShopEntity entity) {
        return ShopDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .status(entity.getStatus())
                .ownerId(entity.getOwner().getId())
                .businessRegistration(
                        BusinessRegistrationDto
                                .fromEntity(entity.getBusinessRegistration()))
                .build();
    }
}
