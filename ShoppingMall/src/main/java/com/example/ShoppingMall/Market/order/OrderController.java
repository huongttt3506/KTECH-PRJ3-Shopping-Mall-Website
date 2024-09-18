package com.example.ShoppingMall.ShoppingMall.order;

import com.example.ShoppingMall.ShoppingMall.order.dto.OrderDto;
import com.example.ShoppingMall.ShoppingMall.order.dto.OrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody Set<OrderItemDto> orderItemDtos) {
        OrderDto createdOrder = orderService.createOrder(orderItemDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<Void> markOrderAsPaid(@PathVariable Long id) {
        orderService.markOrderAsPaid(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/deliver")
    public ResponseEntity<Void> markOrderAsDelivered(@PathVariable Long id) {
        orderService.markOrderAsDelivered(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto>> viewOrdersByUser(@PathVariable Long userId) {
        List<OrderDto> orders = orderService.viewOrdersByUser(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<OrderDto>> viewOrdersByShop(@PathVariable Long shopId) {
        List<OrderDto> orders = orderService.viewOrdersByShop(shopId);
        return ResponseEntity.ok(orders);
    }

}
