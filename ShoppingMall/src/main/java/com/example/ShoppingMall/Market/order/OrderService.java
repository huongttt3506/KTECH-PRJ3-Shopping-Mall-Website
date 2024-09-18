package com.example.ShoppingMall.ShoppingMall.order;
import com.example.ShoppingMall.AuthenticationFacade;
import com.example.ShoppingMall.ShoppingMall.item.entity.ItemEntity;
import com.example.ShoppingMall.ShoppingMall.item.repo.ItemRepository;
import com.example.ShoppingMall.ShoppingMall.order.dto.OrderDto;
import com.example.ShoppingMall.ShoppingMall.order.dto.OrderItemDto;
import com.example.ShoppingMall.ShoppingMall.order.entity.OrderEntity;
import com.example.ShoppingMall.ShoppingMall.order.entity.OrderItemEntity;
import com.example.ShoppingMall.ShoppingMall.order.entity.OrderStatus;
import com.example.ShoppingMall.ShoppingMall.order.repo.OrderRepository;
import com.example.ShoppingMall.ShoppingMall.shop.entity.ShopEntity;
import com.example.ShoppingMall.ShoppingMall.shop.repo.ShopRepository;
import com.example.ShoppingMall.user.entity.UserEntity;
import com.example.ShoppingMall.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final AuthenticationFacade facade;

    public OrderDto createOrder(Set<OrderItemDto> orderItemDto) {
        UserEntity currentUser = facade.getCurrentUserEntity();

        BigDecimal totalPrice = BigDecimal.ZERO;
        ShopEntity shop = null;

        for (OrderItemDto dto : orderItemDto) {
            ItemEntity item = itemRepository.findById(dto.getItemId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item not found"));

            if (shop == null) {
                shop = item.getShop();
            }

            if (!item.getShop().equals(shop)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Items must be from the same shop");
            }

            item.removeStock(dto.getQuantity());
            totalPrice = totalPrice.add(dto.getItemPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        }

        OrderEntity order = OrderEntity.builder()
                .user(currentUser)
                .status(OrderStatus.PENDING)
                .totalPrice(totalPrice)
                .shop(shop)
                .build();

        order = orderRepository.save(order);

        for (OrderItemDto dto : orderItemDto) {
            ItemEntity item = itemRepository.findById(dto.getItemId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item not found"));

            OrderItemEntity orderItem = OrderItemEntity.builder()
                    .order(order)
                    .item(item)
                    .shop(shop)
                    .quantity(dto.getQuantity())
                    .itemPrice(dto.getItemPrice())
                    .build();

            order.getOrderItems().add(orderItem);
        }

        orderRepository.save(order);
        return OrderDto.fromEntity(order);
    }

    public void cancelOrder(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending orders can be canceled");
        }

        for (OrderItemEntity orderItem : order.getOrderItems()) {
            ItemEntity item = orderItem.getItem();
            item.addStock(orderItem.getQuantity());
            itemRepository.save(item);
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    public void markOrderAsPaid(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending orders can be marked as paid");
        }

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }

    public void markOrderAsDelivered(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (order.getStatus() != OrderStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only paid orders can be marked as delivered");
        }

        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Delivered orders cannot be deleted");
        }

        orderRepository.delete(order);
    }

    public List<OrderDto> viewOrdersByUser(Long userId) {
        return orderRepository.findByUser_Id(userId).stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<OrderDto> viewOrdersByShop(Long shopId) {
        return orderRepository.findByShop_Id(shopId).stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
    }
}
