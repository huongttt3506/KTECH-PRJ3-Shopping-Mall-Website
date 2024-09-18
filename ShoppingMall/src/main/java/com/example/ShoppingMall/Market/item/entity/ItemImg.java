package com.example.ShoppingMall.Market.item.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Represents an image associated with an item in the shopping mall
public class ItemImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_img_id")
    private Long id;  // Unique identifier for the image

    private String imgName; // The name of the saved image file
    private String oriImgName; // The original name of the uploaded image file
    private String imgUrl; //// The URL path where the image is accessible
    private String repImgYn; //Set the first image as the representative product image (Y/N)

    @ManyToOne(fetch = FetchType.LAZY) // Multiple images can belong to one item
    @JoinColumn(name = "item_id")
    private ItemEntity item; // Reference to the associated item entity

    // Method to update the image details
    public void updateItemImg(
            String oriImgName,
            String imgName,
            String imgUrl
    ) {
        this.oriImgName = oriImgName; //update original img name
        this.imgName = imgName; // update the saved img name
        this.imgUrl = imgUrl; // update the img path
    }

}
