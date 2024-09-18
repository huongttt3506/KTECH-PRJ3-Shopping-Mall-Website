package com.example.ShoppingMall.Market.item.dto;

import com.example.ShoppingMall.Market.item.entity.ItemImg;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// Data Transfer Object for ItemImg entity
public class ItemImgDto {
    private Long id;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repImgYn;

    public static ItemImgDto fromEntity(ItemImg entity) {
        return ItemImgDto.builder()
                .id(entity.getId())
                .imgName(entity.getImgName())
                .oriImgName(entity.getOriImgName())
                .imgUrl(entity.getImgUrl())
                .repImgYn(entity.getRepImgYn())
                .build();
    }
}
