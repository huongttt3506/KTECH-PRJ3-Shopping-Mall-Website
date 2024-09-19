package com.example.ShoppingMall.Market.cart;

import com.example.ShoppingMall.Market.cart.dto.CartDetailsDto;
import com.example.ShoppingMall.Market.cart.dto.CartItemDto;
import com.example.ShoppingMall.Market.cart.dto.CartOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartItemDto> addItemToCart(@RequestBody CartItemDto cartItemDto) {
        Long cartItemId = cartService.addItemToCart(cartItemDto);
        log.info("add item {} into cart", cartItemId);
        return ResponseEntity.ok(cartItemDto);
    }


    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<String> updateCartItemCount(@PathVariable Long cartItemId, @RequestParam int quantity) {
        cartService.updateCartItemCount(cartItemId, quantity);
        return ResponseEntity.ok("update cart item quantity successfully");
    }


    @DeleteMapping("/delete/{cartItemId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable Long cartItemId) {
        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.ok("delete item in cart successfully");
    }

    @GetMapping("/details")
    public ResponseEntity<CartDetailsDto> viewCartDetails() {
        CartDetailsDto cartDetails = cartService.viewCartDetails();
        return ResponseEntity.ok(cartDetails);
    }


    @PostMapping("/order")
    public ResponseEntity<List<Long>> orderCartItem(@RequestBody CartOrderDto cartOrderDto) {
        List<Long> orderIds = cartService.orderCartItem(cartOrderDto);
        return ResponseEntity.ok(orderIds);
    }

}
