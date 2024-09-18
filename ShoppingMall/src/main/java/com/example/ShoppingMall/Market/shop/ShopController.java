package com.example.ShoppingMall.ShoppingMall.shop;

import com.example.ShoppingMall.ShoppingMall.shop.dto.*;
import com.example.ShoppingMall.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shops")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;

    //READ ALL
    // Get all shops - accessible by admin or business user
    @GetMapping
    public ResponseEntity<List<ShopDto>> getAllShops() {
        List<ShopDto> shops = shopService.getAllShops();
        return ResponseEntity.ok(shops);
    }
    //READ ONE
    // Get a shop by ID - accessible by admin or shop owner
    @GetMapping("/{shopId}")
    public ResponseEntity<ShopDto> getShopById(@PathVariable Long shopId) {
        ShopDto shop = shopService.getShopById(shopId);
        return ResponseEntity.ok(shop);
    }

    // Update shop info - accessible by shop owner
    @PutMapping("/{shopId}/update-info")
    public ResponseEntity<UserDto> updateShopInfo(@PathVariable Long shopId, @RequestBody ShopEssentialDto dto) {
        UserDto updatedUser = shopService.updateShopInfo(shopId, dto);
        return ResponseEntity.ok(updatedUser);
    }

    // Register a shop for opening - accessible by shop owner
    @PostMapping("/{shopId}/register")
    public ResponseEntity<ShopRegResponseDto> shopRegister(@PathVariable Long shopId) {
        ShopRegResponseDto registration = shopService.shopRegister(shopId);
        return ResponseEntity.status(HttpStatus.CREATED).body(registration);
    }

    //READ ALL SHOP REGISTRATIONS
    // Get all shop registrations - accessible by admin or business user
    @GetMapping("/registrations")
    public ResponseEntity<List<ShopRegResponseDto>> readAllShopRegistrations() {
        List<ShopRegResponseDto> registrations = shopService.readAllShopRegistrations();
        return ResponseEntity.ok(registrations);
    }

    //READ ONE SHOP REGISTRATION
    // Get a specific shop registration by ID - accessible by admin or shop owner
    @GetMapping("/registrations/{shopRegId}")
    public ResponseEntity<ShopRegResponseDto> readOneShopRegistration(@PathVariable Long shopRegId) {
        ShopRegResponseDto registration = shopService.readOneShopRegistration(shopRegId);
        return ResponseEntity.ok(registration);
    }

    //APPROVAL OR DECLINE SHOP REGISTRATION (ACCESSIBLE BY ADMIN)
    // Admin accepts a shop registration
    @PostMapping("/registrations/{shopRegId}/accept")
    public ShopDto acceptShopReg(
            @PathVariable("shopRegId")
            Long regId
    ) {
        return shopService.acceptShopReg(regId);
    }

    // Admin declines a shop registration
    @PostMapping("/registrations/{shopRegId}/decline")
    public ShopRegResponseDto declineShopReg(
            @PathVariable("shopRegId") Long shopRegId,
            @RequestBody ShopRegDeclineDto dto
    ) {
        return shopService.declineShopReg(shopRegId, dto);
    }

    // CLOSE SHOP
    // Request to close a shop - accessible by shop owner
    @PostMapping("/close/{shopId}")
    public ResponseEntity<CloseResponseDto> shopCloseRequest(
            @PathVariable("shopId") Long shopId,
            @RequestBody CloseRequestDto dto) {
        CloseResponseDto closeResponse = shopService.shopCloseRequest(shopId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(closeResponse);
    }
    //READ close request
    // Get all close requests - accessible by admin or business user
    @GetMapping("/close-requests")
    public ResponseEntity<List<CloseResponseDto>> readAllCloseRequests() {
        List<CloseResponseDto> closeRequests = shopService.readAllCloseRequest();
        return ResponseEntity.ok(closeRequests);
    }

    // Get one close request by ID
    @GetMapping("/close-requests/{closeReqId}")
    public ResponseEntity<CloseResponseDto> getCloseRequest(@PathVariable Long closeReqId) {
        CloseResponseDto responseDto = shopService.readOneCloseRequest(closeReqId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // Approve close request and close shop (Admin only)
    @PatchMapping("/close-requests/{id}/approve")
    public ResponseEntity<ShopDto> approveCloseRequest(@PathVariable Long id) {
        ShopDto responseDto = shopService.closeShop(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // Search shops by name and category
    @GetMapping("/search")
    public ResponseEntity<List<ShopDto>> searchShops(
            @RequestParam(required = false) String nameKeyword,
            @RequestParam(required = false) String category) {
        List<ShopDto> responseDtos = shopService.searchShops(nameKeyword, category);
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }
}

