package com.example.ShoppingMall.user.UserDetails;


import com.example.ShoppingMall.user.entity.UserEntity;
import com.example.ShoppingMall.user.entity.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;



@Getter
public class CustomUserDetails implements UserDetails {
    private final UserEntity userEntity;

    public CustomUserDetails(UserEntity userEntity) {
        this.userEntity = userEntity;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //Returns user permissions based on RoleUser enum
        return Collections.singletonList(() -> userEntity.getRole().name());
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();

    }

    @Override
    public String getUsername() {
        return userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userEntity.getRole() != UserRole.ROLE_INACTIVE;
    }
}
