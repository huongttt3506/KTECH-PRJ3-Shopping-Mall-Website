package com.example.ShoppingMall.Market.order.dto;

import com.example.ShoppingMall.Market.order.entity.OrderEntity;
import com.example.ShoppingMall.Market.order.entity.OrderStatus;
import lombok.*;
import org.hibernate.annotations.SecondaryRow;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private Long userId;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Set<OrderItemDto> orderItems;
    private int totalAmount;
    private Long shopId;

    public static OrderDto fromEntity(OrderEntity orderEntity) {
        return OrderDto.builder()
                .id(orderEntity.getId())
                .userId(orderEntity.getUser().getId())
                .orderDate(orderEntity.getOrderDate())
                .orderStatus(orderEntity.getOrderStatus())
                .orderItems(orderEntity.getOrderItems().stream()
                        .map(OrderItemDto::fromEntity)
                        .collect(Collectors.toSet()))
                .totalAmount(orderEntity.getTotalAmount())
                .shopId(orderEntity.getShop().getId())
                .build();
    }
}
