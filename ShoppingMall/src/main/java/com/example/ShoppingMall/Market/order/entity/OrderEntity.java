package com.example.ShoppingMall.Market.order.entity;
import com.example.ShoppingMall.Market.shop.entity.ShopEntity;
import com.example.ShoppingMall.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
//OrderEntity manage general order information
//customer information, shop, order status, and total order value
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private ShopEntity shop;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    private int totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> orderItems = new ArrayList<>();



    public void addOrderItem(OrderItemEntity orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public static OrderEntity createOrder(
            UserEntity user,
            List<OrderItemEntity> orderItemList
    ) {
        OrderEntity order = new OrderEntity();
        order.setUser(user);
        for(OrderItemEntity orderItem : orderItemList) {
            order.addOrderItem(orderItem);
            //Item page, only can order 1 item
            // but users can oder multiple items in the shopping cart.
            //So, we need to receive parameter values in the form of a list
            // so that we can put multiple products in the shopping cart.
            //parameter is orderItem
        }
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        return  order;
    }

    //Total order amount
    public int getTotalAmount() {
        int totalAmount = 0;
        for(OrderItemEntity orderItem : orderItems){
            totalAmount += orderItem.getTotalPrice();
        }
        return totalAmount;
    }

    // Change order status to "CANCELED"
    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCELED;
        for (OrderItemEntity orderItem : orderItems) {
            orderItem.cancel();
        }
    }
}