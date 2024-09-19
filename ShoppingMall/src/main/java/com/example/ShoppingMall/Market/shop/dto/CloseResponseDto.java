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
public class CloseResponseDto {
    private Long id;
    private String reason;
    private Long shopId;
    private Long ownerId;

    public static CloseResponseDto fromEntity(ShopCloseRequest entity) {
        return CloseResponseDto.builder()
                .id(entity.getId())
                .reason(entity.getReason())
                .shopId(entity.getShop() != null ? entity.getShop().getId() : null) // Lấy ID cửa hàng
                .ownerId(entity.getOwner() != null ? entity.getOwner().getId() : null) // Lấy ID chủ sở hữu
                .build();
    }
}
