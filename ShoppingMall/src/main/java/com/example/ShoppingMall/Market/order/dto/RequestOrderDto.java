package com.example.ShoppingMall.Market.order.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Data
public class RequestOrderDto {
    private Set<OrderItemRequestDto> orderItems;

}
