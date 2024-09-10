package com.example.ShoppingMall.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_table")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String nickname;
    private String firstName;
    private String lastName;
    private Integer ageGroup;
    private String email;
    private String phone;
    private String profileImagePath;

    @Enumerated(EnumType.STRING)
    private UserRole role;

}
