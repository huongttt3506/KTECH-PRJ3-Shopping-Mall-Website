package com.example.ShoppingMall.user;

import com.example.ShoppingMall.jwt.dto.JwtResponseDto;
import com.example.ShoppingMall.user.dto.EssentialInfoDto;
import lombok.RequiredArgsConstructor;
import com.example.ShoppingMall.user.dto.LoginDto;
import com.example.ShoppingMall.user.dto.RegisterUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.ShoppingMall.user.dto.UserDto;
import org.springframework.web.multipart.MultipartFile;
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









}
