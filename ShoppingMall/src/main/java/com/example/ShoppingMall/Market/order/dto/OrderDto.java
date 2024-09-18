package com.example.ShoppingMall.ShoppingMall.order.dto;

import com.example.ShoppingMall.ShoppingMall.order.entity.OrderEntity;
import com.example.ShoppingMall.ShoppingMall.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private Long userId;
    private OrderStatus status;
    private Set<OrderItemDto> orderItems;
    private BigDecimal totalPrice;
    private Long shopId;

    public static OrderDto fromEntity(OrderEntity orderEntity) {
        return OrderDto.builder()
                .id(orderEntity.getId())
                .userId(orderEntity.getUser().getId())
                .status(orderEntity.getStatus())
                .orderItems(orderEntity.getOrderItems().stream()
                        .map(OrderItemDto::fromEntity)
                        .collect(Collectors.toSet()))
                .totalPrice(orderEntity.getTotalPrice())
                .shopId(orderEntity.getShop().getId())
                .build();
    }
}
