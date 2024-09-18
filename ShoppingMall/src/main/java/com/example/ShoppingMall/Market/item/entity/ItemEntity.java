package com.example.ShoppingMall.Market.item.entity;

import com.example.ShoppingMall.Market.item.dto.ItemFormDto;
import com.example.ShoppingMall.Market.shop.entity.ShopEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name; // item name

    @Column(nullable = false)
    private int price;

    private int stock;

    private String description;

    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "item_category",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<CategoryEntity> category = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="shop")
    private ShopEntity shop;

    //item update constructor
    public void updateItem(ItemFormDto itemFormDto) {
        this.name = itemFormDto.getName();
        this.price = itemFormDto.getPrice();
        this.stock = itemFormDto.getStock();
        this.description = itemFormDto.getDescription();
        this.itemStatus = itemFormDto.getItemStatus();
    }

    // Order item -> Create logic to reduce product inventory
    //orderQuantity: Quantity of goods ordered.
    //restStock: Quantity of remaining inventory after ordering
    public void removeStock(int orderQuantity) {
        int restStock = this.stock - orderQuantity;
        if (restStock < 0) {
//           throw new OutOfStockException("The remaining quantity is not enough."+
//                   "(Current stock quantity:" + this.stock + ")");
           throw new RuntimeException("The remaining quantity is not enough."+
                   "(Current stock quantity:" + this.stock + ")");
        }
        this.stock = restStock;
    }
    //Increase the number of items when canceling an order
    public void addStock(int orderQuantity) {
        this.stock += orderQuantity;
    }
}
