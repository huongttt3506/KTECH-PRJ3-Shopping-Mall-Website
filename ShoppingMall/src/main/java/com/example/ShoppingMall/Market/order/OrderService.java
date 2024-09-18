package com.example.ShoppingMall.Market.order;
import com.example.ShoppingMall.AuthenticationFacade;
import com.example.ShoppingMall.Market.item.entity.ItemEntity;
import com.example.ShoppingMall.Market.item.repo.ItemRepository;
import com.example.ShoppingMall.Market.order.dto.*;
import com.example.ShoppingMall.Market.order.entity.OrderEntity;
import com.example.ShoppingMall.Market.order.entity.OrderItemEntity;
import com.example.ShoppingMall.Market.order.entity.OrderStatus;
import com.example.ShoppingMall.Market.order.repo.OrderItemRepository;
import com.example.ShoppingMall.Market.order.repo.OrderRepository;
import com.example.ShoppingMall.Market.shop.entity.ShopEntity;
import com.example.ShoppingMall.Market.shop.repo.ShopRepository;
import com.example.ShoppingMall.user.entity.UserEntity;
import com.example.ShoppingMall.user.entity.UserRole;
import com.example.ShoppingMall.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;


import com.example.ShoppingMall.AuthenticationFacade;
import com.example.ShoppingMall.Market.item.entity.ItemEntity;
import com.example.ShoppingMall.Market.item.repo.ItemRepository;
import com.example.ShoppingMall.Market.order.dto.OrderDto;
import com.example.ShoppingMall.Market.order.dto.OrderItemDto;
import com.example.ShoppingMall.Market.order.entity.OrderEntity;
import com.example.ShoppingMall.Market.order.entity.OrderItemEntity;
import com.example.ShoppingMall.Market.order.entity.OrderStatus;
import com.example.ShoppingMall.Market.order.repo.OrderItemRepository;
import com.example.ShoppingMall.Market.order.repo.OrderRepository;
import com.example.ShoppingMall.Market.shop.entity.ShopEntity;
import com.example.ShoppingMall.Market.shop.repo.ShopRepository;
import com.example.ShoppingMall.user.entity.UserEntity;
import com.example.ShoppingMall.user.entity.UserRole;
import com.example.ShoppingMall.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
// This class contains create, cancel, read, and update order methods
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final ShopRepository shopRepository;
    private final AuthenticationFacade facade;

    // User Create an Order
    public Long createOrder(RequestOrderDto requestOrderDto) {
        //Check permission
        UserEntity currentUser = facade.getCurrentUserEntity();
        if (currentUser.getRole().equals(UserRole.ROLE_INACTIVE)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You must update basic information to activate your account.");
        }

        // Create OrderItem list from requestOrderDto
        List<OrderItemEntity> orderItemList = new ArrayList<>();

        for (OrderItemRequestDto orderItemRequestDto : requestOrderDto.getOrderItems()) {
            // find item by itemId
            ItemEntity item = itemRepository.findById(orderItemRequestDto.getItemId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

            // Create OrderItem by item và quantity
            OrderItemEntity orderItem = OrderItemEntity.createOrderItem(item, orderItemRequestDto.getQuantity());
            orderItem.setShop(item.getShop());
            orderItemList.add(orderItem);
        }

        //
        OrderEntity order = OrderEntity.createOrder(currentUser, orderItemList);
        order.setShop(orderItemList.get(0).getItem().getShop());

        // set total amount
        int totalAmount = order.getTotalAmount();
        order.setTotalAmount(totalAmount);

        // save to order entity
        orderRepository.save(order);

        // save each orderItem to OrderItemEntity
        for (OrderItemEntity orderItem : orderItemList) {
            orderItem.setOrder(order);
            orderItemRepository.save(orderItem);
        }
        return order.getId();
    }
//    public Long createOrder(OrderDto orderDto) {
//        UserEntity currentUser = facade.getCurrentUserEntity();
//        if (currentUser.getRole().equals(UserRole.ROLE_INACTIVE)) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
//                    "You must update basic information to activate your account.");
//        }
//
//        // Get shopId, userId
//        Long shopId = orderDto.getShopId();
//        Long userId = orderDto.getUserId();
//
//        // Find user
//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//
//        // Find shop
//        ShopEntity shop = shopRepository.findById(shopId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop not found"));
//
//        // Create order item list
//        List<OrderItemEntity> orderItemList = new ArrayList<>();
//        for (OrderItemDto orderItemDto : orderDto.getOrderItems()) {
//            ItemEntity item = itemRepository.findById(orderItemDto.getItemId())
//                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
//
//            OrderItemEntity orderItem = OrderItemEntity.createOrderItem(item, orderItemDto.getQuantity());
//            orderItem.setShop(shop);
//            orderItemList.add(orderItem);
//        }
//
//        // Create order
//        OrderEntity order = OrderEntity.createOrder(user, orderItemList);
//        order.setShop(shop);
//
//        // Save order
//        orderRepository.save(order);
//
//        return order.getId();
//    }

    // Get all orders by user
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByUser() {
        UserEntity currentUser = facade.getCurrentUserEntity();
        List<OrderEntity> orders = orderRepository.findByUser(currentUser);
        return orders.stream().map(OrderDto::fromEntity).toList();
    }

    // Get all orders by shop
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByShop(Long shopId) {
        ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop not found"));

        List<OrderEntity> orders = orderRepository.findByShop(shop);
        return orders.stream().map(OrderDto::fromEntity).toList();
    }

    // Get a single order by ID
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long orderId) {
        // Current user
        UserEntity currentUser = facade.getCurrentUserEntity();

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // Check if the order belongs to the current user or shop
        if (!order.getUser().equals(currentUser) && !order.getShop().getOwner().equals(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Unauthorized to access this order");
        }
        return OrderDto.fromEntity(order);
    }

    // Confirm an order (for shop owner)
    @Transactional
    public void confirmOrder(Long orderId) {
        UserEntity currentUser = facade.getCurrentUserEntity();
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // Check if the order belongs to the shop of the current user
        if (!order.getShop().getOwner().equals(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Unauthorized to confirm this order");
        }
        // Update order status to "CONFIRMED"
        order.setOrderStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
    }

    // Cancel an order
    @Transactional
    public void cancelOrder(Long orderId) {
        UserEntity currentUser = facade.getCurrentUserEntity();
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // Only order owner and admin can cancel the order
        if (!order.getUser().equals(currentUser) && !currentUser.getRole().equals(UserRole.ROLE_ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Unauthorized to cancel this order");
        }

        //only order with order status is "PENDING" can cancel
        if (order.getOrderStatus() !=  OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is already canceled or confirmed and on delivery");
        }
        // Update OrderStatus to CANCELED
        order.cancelOrder();
        orderRepository.save(order);
    }
}
