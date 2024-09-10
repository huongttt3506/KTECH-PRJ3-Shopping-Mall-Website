package com.example.ShoppingMall.user;

import com.example.ShoppingMall.jwt.dto.JwtResponseDto;
import com.example.ShoppingMall.user.dto.EssentialInfoDto;
import lombok.RequiredArgsConstructor;
import com.example.ShoppingMall.user.dto.LoginDto;
import com.example.ShoppingMall.user.dto.RegisterUserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.ShoppingMall.user.dto.UserDto;

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
    @PatchMapping("/update-essential-info")
    public ResponseEntity<UserDto> updateEssentialInfo(
            @RequestBody EssentialInfoDto dto
    ) {
        UserDto updatedUser = userService.updateEssentialInfo(dto);
        return ResponseEntity.ok(updatedUser);
    }









}
