package com.example.ShoppingMall.Market.cart;

import com.example.ShoppingMall.AuthenticationFacade;
import com.example.ShoppingMall.Market.cart.dto.CartDetailsDto;
import com.example.ShoppingMall.Market.cart.dto.CartItemDto;
import com.example.ShoppingMall.Market.cart.dto.CartOrderDto;
import com.example.ShoppingMall.Market.cart.entity.CartEntity;
import com.example.ShoppingMall.Market.cart.entity.CartItemEntity;
import com.example.ShoppingMall.Market.cart.repo.CartItemRepository;
import com.example.ShoppingMall.Market.cart.repo.CartRepository;
import com.example.ShoppingMall.Market.item.entity.ItemEntity;
import com.example.ShoppingMall.Market.item.repo.ItemRepository;
import com.example.ShoppingMall.Market.order.OrderService;
import com.example.ShoppingMall.Market.order.dto.OrderItemRequestDto;
import com.example.ShoppingMall.Market.order.dto.RequestOrderDto;
import com.example.ShoppingMall.Market.order.repo.OrderRepository;
import com.example.ShoppingMall.exception.EntityNotFoundException;
import com.example.ShoppingMall.user.entity.UserEntity;
import com.example.ShoppingMall.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {
    private final ItemRepository itemRepository;
    private final AuthenticationFacade facade;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    // Add item to cart
    @Transactional
    public Long addItemToCart(CartItemDto cartItemDto) {
        UserEntity currentUser = facade.getCurrentUserEntity();
        if (currentUser.getRole().equals(UserRole.ROLE_INACTIVE))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You must update essential information to active your account");

        //Find item from cartItemDto
        ItemEntity item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("item not found"));

        Optional<CartEntity> cartOpt = cartRepository.findByUser(currentUser);
        CartEntity cart;
        if (cartOpt.isEmpty())
        {
            cart = CartEntity.createCart(currentUser);
            cartRepository.save(cart);
        }
        else cart = cartOpt.get();

        // Get item in cart
        CartItemEntity savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        // if item exists before,update item quantity ++
        if (savedCartItem != null) {
            savedCartItem.addQuantity(cartItemDto.getQuantity());
            return savedCartItem.getId();
        } else {
            //if item is not exists before, add to cart
            CartItemEntity cartItem = CartItemEntity.createCartItem(cart, item, cartItemDto.getQuantity());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }
    // Update quantity of item in cart
    public void updateCartItemCount(Long cartItemId, int quantity) {

        CartItemEntity cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("item not found"));

        cartItem.updateQuantity(quantity); // quantity++
    }

    // Delete item in cart
    public void deleteCartItem(Long cartItemId) {

        CartItemEntity cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("item not found"));

        cartItemRepository.delete(cartItem); // delete item in cart
    }

    //User view their cart through CartDetailsDto
    @Transactional(readOnly = true)
    public CartDetailsDto viewCartDetails() {
        UserEntity currentUser = facade.getCurrentUserEntity();
        // Get cart for the current user
        CartEntity cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cart not found"));

        // Get all items in the cart
        List<CartItemEntity> cartItems = cartItemRepository.findByCart(cart);

        // Calculate total amount
        int totalAmount = 0;
        List<CartItemDto> cartItemDtos = new ArrayList<>();
        for (CartItemEntity cartItem : cartItems) {
            ItemEntity item = cartItem.getItem();
            int itemTotal = item.getPrice() * cartItem.getQuantity();
            totalAmount += itemTotal;

            CartItemDto cartItemDto = CartItemDto.fromEntity(cartItem);
            cartItemDtos.add(cartItemDto);}
        // Create and return OrderDetailsDto

        return  CartDetailsDto.builder()
                .cartId(cart.getId())
                .cartItems(cartItemDtos)
                .totalAmount(totalAmount)
                .build();
    }


    // Method to process the cart and convert it to a list of OrderItemEntity
    @Transactional
    public List<Long> orderCart(Long cartId) {
        UserEntity currentUser = facade.getCurrentUserEntity();
        if (currentUser.getRole().equals(UserRole.ROLE_INACTIVE)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You must update essential information to activate your account.");
        }

        // find cart info by cart id
        CartEntity cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart not found for the given ID"));

        // Check cart owner
        if (!cart.getUser().equals(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not authorized to access this cart");
        }

        // create OrderItemRequestDto from cart
        Set<OrderItemRequestDto> orderItemRequestDtoSet =
                cartItemRepository.findByCart(cart).stream()
                .map(cartItem -> {
                    OrderItemRequestDto dto = new OrderItemRequestDto();
                    dto.setItemId(cartItem.getItem().getId());
                    dto.setQuantity(cartItem.getQuantity());
                    return dto;
                })
                .collect(Collectors.toSet());

        // create RequestOrderDto from set
        RequestOrderDto requestOrderDto = new RequestOrderDto();
        requestOrderDto.setOrderItems(orderItemRequestDtoSet);

        //create order for each shop
        List<Long> orderIds = orderService.createOrdersForEachShop(requestOrderDto);

        // delete cart after order
        cartItemRepository.deleteByCart(cart);

        return orderIds;
    }

//    @Transactional
//    public List<Long> orderCartItem(CartOrderDto cartOrderDto) {
//        // Check permission
//        UserEntity currentUser = facade.getCurrentUserEntity();
//        if (currentUser.getRole().equals(UserRole.ROLE_INACTIVE)) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
//                    "You must update essential information to activate your account.");
//        }
//
//        // Retrieve the user's cart
//        CartEntity cart = cartRepository.findByUser(currentUser)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart not found for the user"));
//
//        // Create OrderItemRequestDto set from CartOrderDto list
//        Set<OrderItemRequestDto> orderItemRequestDtoSet = new HashSet<>();
//        for (CartItemDto cartItemDto : cartOrderDto.getCartItems()) {
//            // Retrieve item entity from cartItemDto
//            ItemEntity item = itemRepository.findById(cartItemDto.getItemId())
//                    .orElseThrow(() -> new EntityNotFoundException("Item not found"));
//
//            // Create OrderItemRequestDto from CartItemDto
//            OrderItemRequestDto orderItemRequestDto = new OrderItemRequestDto();
//            orderItemRequestDto.setItemId(cartItemDto.getItemId());
//            orderItemRequestDto.setQuantity(cartItemDto.getQuantity());
//
//            // Add to orderItemRequestDtoSet
//            orderItemRequestDtoSet.add(orderItemRequestDto);
//        }
//
//        // Create RequestOrderDto from the set
//        RequestOrderDto requestOrderDto = new RequestOrderDto();
//        requestOrderDto.setOrderItems(orderItemRequestDtoSet);  // Set used here
//
//        // Create orders for each shop
//        List<Long> orderIds = orderService.createOrdersForEachShop(requestOrderDto);
//
//        // Clear the cart after order is placed
//        cartItemRepository.deleteByCart(cart);
//
//        return orderIds;  // Return the list of order IDs after processing
//    }

}
