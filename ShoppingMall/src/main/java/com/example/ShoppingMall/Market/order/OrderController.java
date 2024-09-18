package com.example.ShoppingMall.Market.order;

import com.example.ShoppingMall.Market.order.dto.OrderDto;
import com.example.ShoppingMall.Market.order.dto.RequestOrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    // Create an order
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody RequestOrderDto requestOrderDto) {
        Long orderId = orderService.createOrder(requestOrderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }

    // Get all orders by current user
    @GetMapping("/user")
    public ResponseEntity<List<OrderDto>> getOrdersByUser() {
        List<OrderDto> orders = orderService.getOrdersByUser();
        return ResponseEntity.ok(orders);
    }
    // Shop owner get all orders by shopId
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<OrderDto>> getOrdersByShop(@PathVariable Long shopId) {
        List<OrderDto> orders = orderService.getOrdersByShop(shopId);
        return ResponseEntity.ok(orders);
    }

    // Get order details by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long orderId) {
        OrderDto order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    // Confirm an order (for shop owner)
    @PatchMapping("/{orderId}/confirm")
    public ResponseEntity<String> confirmOrder(@PathVariable Long orderId) {
        orderService.confirmOrder(orderId);
        return ResponseEntity.ok("order confirmed");
    }

    // Cancel an order
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok("order is canceled successful!");
    }
}
