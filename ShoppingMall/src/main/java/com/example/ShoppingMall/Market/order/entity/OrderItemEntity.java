package com.example.ShoppingMall.Market.order.entity;

import com.example.ShoppingMall.Market.item.entity.ItemEntity;
import com.example.ShoppingMall.Market.shop.entity.ShopEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor

//OrderItem: Manage specific products in an order.
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private ShopEntity shop;

    private int quantity;
    private int itemPrice;
    private int price;

    public int getTotalPrice() {
        return price * quantity;
    }
    public static OrderItemEntity createOrderItem(ItemEntity item, int quantity){

        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setItem(item);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(item.getPrice());
        item.removeStock(quantity);
        return orderItem;
    }
    public void cancel() {
        this.getItem().addStock(quantity);
    }
}
