package com.example.ShoppingMall.user;

import com.example.ShoppingMall.jwt.dto.JwtResponseDto;
import lombok.RequiredArgsConstructor;
import com.example.ShoppingMall.user.dto.LoginDto;
import com.example.ShoppingMall.user.dto.RegisterUserDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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








}
