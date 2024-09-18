package com.example.ShoppingMall.Market.item.dto;

import com.example.ShoppingMall.Market.item.entity.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainItemDto {
    private Long id;
    private String name;
    private int price;
    private String imgUrl;
    private ItemStatus itemStatus;
    private Long shopId;
}
