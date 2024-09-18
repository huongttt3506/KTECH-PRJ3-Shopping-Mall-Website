package com.example.ShoppingMall.Market.order.dto;

import com.example.ShoppingMall.Market.order.entity.OrderItemEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {
    private Long id;
    private Long itemId;
    private Long shopId;
    private int quantity;
    private int itemPrice;

    public static OrderItemDto fromEntity(OrderItemEntity orderItemEntity) {
        return OrderItemDto.builder()
                .id(orderItemEntity.getId())
                .itemId(orderItemEntity.getItem().getId())
                .shopId(orderItemEntity.getShop().getId())
                .quantity(orderItemEntity.getQuantity())
                .itemPrice(orderItemEntity.getItemPrice())
                .build();
    }
}
