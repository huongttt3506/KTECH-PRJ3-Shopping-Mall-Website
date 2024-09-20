package com.example.ShoppingMall.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.example.ShoppingMall.user.entity.UserEntity;
import com.example.ShoppingMall.user.repo.UserRepository;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
// a utility class to get current user information from SecurityContext in Spring Security
public class AuthenticationFacade {

    private final UserRepository userRepository;

    public UserEntity getCurrentUserEntity() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        String username = ((UserEntity) authentication.getPrincipal()).getUsername();

        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return optionalUser.get();
    }

}
