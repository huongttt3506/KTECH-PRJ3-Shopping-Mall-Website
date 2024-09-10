package com.example.ShoppingMall.user;
import com.example.ShoppingMall.AuthenticationFacade;
import com.example.ShoppingMall.jwt.JwtTokenUtils;
import com.example.ShoppingMall.jwt.dto.JwtResponseDto;
import com.example.ShoppingMall.user.dto.EssentialInfoDto;
import com.example.ShoppingMall.user.dto.LoginDto;
import com.example.ShoppingMall.user.dto.RegisterUserDto;
import com.example.ShoppingMall.user.dto.UserDto;
import com.example.ShoppingMall.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.ShoppingMall.user.entity.UserEntity;
import com.example.ShoppingMall.user.repo.UserRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationFacade facade;

    // Register user
    public UserDto registerUser(RegisterUserDto registerUserDto) {
        //1. Check username is not exist
        if (userRepository.existsByUsername(registerUserDto.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        //2. Check Password and confirmPassword are the same.
        if (!registerUserDto.getPassword().equals(registerUserDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Password do not match");
        }

        //3. Create userEntity newUser Object

        UserEntity newUser = UserEntity.builder()
                .username(registerUserDto.getUsername())
                .password(passwordEncoder.encode(registerUserDto.getPassword()))
                .role(UserRole.ROLE_INACTIVE)
                .build();
        //4. save to userEntity
        userRepository.save(newUser);

        //5. Conversion from UserEntity to UserDto
        return UserDto.fromEntity(newUser);
    }

    // Generate jwt token base on Username, password (user input)
    public JwtResponseDto userLogin(LoginDto loginDto) {
        // 1. Check if username exists
        Optional<UserEntity> optionalUser = userRepository.findByUsername(loginDto.getUsername());
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        UserEntity userEntity = optionalUser.get();
        // 2. Check if password matches
        if(!passwordEncoder.matches(loginDto.getPassword(), userEntity.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid username or password");
        }
        // 3. Generate JWT token
        String token = jwtTokenUtils.generateToken(userEntity);

        // 4. Return JWT token in response
        JwtResponseDto responseDto = new JwtResponseDto(token);
        responseDto.setToken(token);
        return responseDto;
    }

    // Fill essential info to change from ROLE_INACTIVE to  ROLE_USER
    public UserDto updateEssentialInfo(EssentialInfoDto dto) {
        // 1. Get the current user from AuthenticationFacade
        UserEntity userEntity = facade.getCurrentUserEntity();
        //2. update essential info by get info from dto
        userEntity.setNickname(dto.getNickname());
        userEntity.setFirstName(dto.getFirstName());
        userEntity.setLastName(dto.getLastName());
        userEntity.setAgeGroup(dto.getAgeGroup());
        userEntity.setEmail(dto.getEmail());
        userEntity.setPhone(dto.getPhone());
        //3. Change status of user role from ROLE_INACTIVE to ROLE_USER
        if (userEntity.getRole().equals(UserRole.ROLE_INACTIVE)) {
            userEntity.setRole(UserRole.ROLE_USER);
        }
        //4. Save and return updated user information
        return UserDto.fromEntity(userRepository.save(userEntity));


    }



}
