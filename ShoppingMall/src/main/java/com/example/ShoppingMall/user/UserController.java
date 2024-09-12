package com.example.ShoppingMall.user;

import com.example.ShoppingMall.ShoppingMall.shop.dto.ShopDto;
import com.example.ShoppingMall.jwt.dto.JwtResponseDto;
import com.example.ShoppingMall.user.dto.*;
import com.example.ShoppingMall.user.entity.BusinessRegistration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public UserDto userRegister(
            @RequestBody
            RegisterUserDto dto
    ) {
        return userService.registerUser(dto);
    }

    @PostMapping("/login")
    public JwtResponseDto userLogin(
            @RequestBody
            LoginDto dto
    ) {
       log.info("username login: {}", dto.getUsername());
       return userService.userLogin(dto);
    }

    // Endpoint updates essential information
    @PatchMapping("/{userId}/updateEssentialInfo")
    public ResponseEntity<UserDto> updateEssentialInfo(
            @PathVariable Long userId,
            @RequestBody EssentialInfoDto dto
    ) {
        UserDto updatedUser = userService.updateEssentialInfo(dto);
        return ResponseEntity.ok(updatedUser);
    }

    // Update profile image
    @PutMapping("/{userId}/updateProfileImage")
    public ResponseEntity<UserDto> updateProfileImage(
            @PathVariable Long userId,
            @RequestParam("image") MultipartFile image
    ) {
        log.info("Request to upload profile img for user: {}", userId);
        log.info("File name: {}", image.getOriginalFilename());

        UserDto updatedUser = userService.updateProfileImg(userId, image);
        return ResponseEntity.ok(updatedUser);
    }


    // ROLE_USER request to upgrade to ROLE_BUSINESS
    @PostMapping("/{userId}/business")
    public BusinessRegistrationDto businessRegister(
            @PathVariable("userId") Long userId,
            @RequestParam("business_number")
            String businessNumber
    ) {
        return userService.businessRegister(businessNumber);
    }

    // ADMIN READ REGISTRATION REQUEST LIST
    @GetMapping("/admin/business")
    public List<BusinessRegistrationDto> readBusinessRegistrations() {
        return userService.readBusinessRegistrations();
    }

    // READ ONE
    @GetMapping("/admin/business/{id}")
    public BusinessRegistrationDto readOneBusinessRegistration(
            @PathVariable("id") Long id
    ) {
        return userService.readOneBusinessRegistration(id);

    }

    // Admin accept business registration request
    // users/admin/business/1/accept
    @PostMapping("/admin/business/{registrationId}/accept")
    public String acceptBusinessRegistration(
            @PathVariable("registrationId") Long id
    ) {
        userService.acceptBusinessRegistration(id);
        return "done";
    }

    // Admin decline business registration request
    @DeleteMapping("/admin/business/{BusinessRegId}/decline")
    public String declineBusinessRegistration(
            @PathVariable("BusinessRegId") Long id
    ) {
        userService.declineBusinessRegistration(id);
        return "Business registration declined.";
    }

}
