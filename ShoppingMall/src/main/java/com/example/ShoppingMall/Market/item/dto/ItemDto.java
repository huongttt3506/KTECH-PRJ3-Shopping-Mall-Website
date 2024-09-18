package com.example.ShoppingMall.Market.item.dto;

import com.example.ShoppingMall.Market.item.entity.ItemEntity;
import com.example.ShoppingMall.Market.item.entity.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;

    private String name;
    private String description;
    private int price;
    private int stock;
    private ItemStatus itemStatus;
    private Set<CategoryDto> categories;
    private Long shopId;

    public static ItemDto fromEntity(ItemEntity entity) {
        return ItemDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .itemStatus(entity.getItemStatus())
                .categories(entity.getCategory().stream()
                        .map(CategoryDto::fromEntity)
                        .collect(Collectors.toSet()))
                .shopId(entity.getShop().getId())
                .build();
    }
}
