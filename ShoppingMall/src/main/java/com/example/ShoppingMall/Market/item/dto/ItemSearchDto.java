package com.example.ShoppingMall.Market.item.dto;

import com.example.ShoppingMall.Market.item.entity.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemSearchDto {
    private Long shopId;  // shopId
    private String name; //name of item
    private ItemStatus status; // ON_SALE, SOLD_OUT
    private Long categoryId; //id of category
}
