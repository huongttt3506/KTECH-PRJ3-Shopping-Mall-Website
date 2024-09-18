package com.example.ShoppingMall.user.dto;

import com.example.ShoppingMall.user.entity.BusinessRegistration;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessRegistrationDto {
    private Long id;
    private String businessNum;
    private Long userId; // To reference to associated user


    public static BusinessRegistrationDto fromEntity(BusinessRegistration entity) {
        return BusinessRegistrationDto.builder()
                .id(entity.getId())
                .businessNum(entity.getBusinessNum())
                .userId(entity.getUser().getId()) //reference to the user entity
                .build();

    }
}
