package com.example.ShoppingMall.user.dto;

import lombok.Data;

@Data
public class EssentialInfoDto {
    private String nickname;
    private String firstName;
    private String lastName;
    private Integer ageGroup;
    private String email;
    private String phone;
}
