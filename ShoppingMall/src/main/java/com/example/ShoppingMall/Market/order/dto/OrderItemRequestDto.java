package com.example.ShoppingMall.Market.order.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class OrderItemRequestDto {
    private Long itemId;
    private int quantity;
}
