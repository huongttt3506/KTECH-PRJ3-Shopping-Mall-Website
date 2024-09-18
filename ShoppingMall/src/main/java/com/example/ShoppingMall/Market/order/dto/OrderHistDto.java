package com.example.ShoppingMall.Market.order.dto;

import com.example.ShoppingMall.Market.order.OrderService;
import com.example.ShoppingMall.Market.order.entity.OrderStatus;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

//dto to contain order information
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistDto {
    private Long orderId;
    private String orderDate;
    private OrderStatus orderStatus;

    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    // order item list
    public void addOrderItemDto(OrderItemDto orderItemDto) {
        orderItemDtoList.add(orderItemDto);
    }
}
