package com.example.ShoppingMall.Market.item.dto;

import com.example.ShoppingMall.Market.item.entity.ItemEntity;
import com.example.ShoppingMall.Market.item.entity.ItemStatus;
import com.example.ShoppingMall.Market.shop.entity.ShopEntity;
import lombok.*;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemFormDto {
    private Long id;
    private String name;
    private int price;
    private String description;
    private int stock;
    private ItemStatus itemStatus;
    private Long shopId;

    // List to save item image information when update item after saving it
    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();

    // List to store item image IDs
    // It is empty because there is no image before item registration
    // (image is blank, ID is blank too!)
    // Just for saving the image ID when editing
    private List<Long> itemImgIds = new ArrayList<>();

    // Method to convert ItemFormDto to ItemEntity
    public ItemEntity createItem(ShopEntity shop) {
        return  ItemEntity.builder()
                .name(this.name)
                .price(this.price)
                .description(this.description)
                .stock(this.stock)
                .itemStatus(this.itemStatus)
                .shop(shop)
                .build();
    }
    // method to convert from Item to ItemFormDto
    public static ItemFormDto fromEntity(ItemEntity entity) {
        return ItemFormDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .description(entity.getDescription())
                .stock(entity.getStock())
                .itemStatus(entity.getItemStatus())
                .shopId(entity.getShop().getId())
                .build();
    }
}
