package com.example.ShoppingMall.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.example.ShoppingMall.user.entity.UserEntity;

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

    public static UserDto fromEntity(UserEntity entity) {
        return new UserDto(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getNickname(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getAgeGroup(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getProfileImagePath(),
                entity.getRole().name());

    }

}
