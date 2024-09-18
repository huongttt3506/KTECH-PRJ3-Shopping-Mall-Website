package com.example.ShoppingMall.user.dto;

import com.example.ShoppingMall.Market.shop.dto.ShopDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.example.ShoppingMall.user.entity.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    private String username;
    private String password;
    private String nickname;
    private String firstName;
    private String lastName;
    private Integer ageGroup;
    private String email;
    private String phone;
    private String profileImagePath;
    private String role;

    private List<BusinessRegistrationDto> businessRegistrations;
    private List<ShopDto> shops;

    public static UserDto fromEntity(UserEntity entity) {
        return UserDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .nickname(entity.getNickname())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .ageGroup(entity.getAgeGroup())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .profileImagePath(entity.getProfileImagePath())
                .role(entity.getRole().name())
                .businessRegistrations(
                        entity.getBusinessRegistrations() != null
                                ? entity.getBusinessRegistrations()
                                    .stream()
                                    .map(BusinessRegistrationDto::fromEntity)
                                    .collect(Collectors.toList())
                                : null
                        )
                .shops(
                        entity.getShops() != null
                                ? entity.getShops().stream()
                                    .map(ShopDto::fromEntity)
                                    .collect(Collectors.toList())
                                :null
                )
                .build();

    }

}
