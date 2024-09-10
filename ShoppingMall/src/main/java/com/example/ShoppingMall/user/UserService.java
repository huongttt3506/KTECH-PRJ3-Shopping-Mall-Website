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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.example.ShoppingMall.user.entity.UserEntity;
import com.example.ShoppingMall.user.repo.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    // UPDATE
    public UserDto updateProfileImg(Long id, MultipartFile image)  {
        // 1. 유저가 존재하는지 확인한다.
        Optional<UserEntity> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        // 2. 파일의 업로드 위치를 결정한다.
        // 추천: media/{userId}/profile.png|jpeg
        //2.1 profile image folder 확인 및 생선
        String profileDirectory = "media/" + id + "/"; //media/{userId}
        try {
            Files.createDirectories(Path.of(profileDirectory));}
        catch (IOException e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //2.2. 업로드란 파일의 확장자를 추출한다
        String originalFilename = image.getOriginalFilename();
        String[] filenameSplit = originalFilename.split("\\.");
        // fish.pag -> filenameSplit = {"fish", "png"}
        // blue.whale.png -> filenameSplit = {"blue", "whale", "png"}
        String extension = filenameSplit[filenameSplit.length - 1];
        //2.3 실제 위치에 파일을 저장한다
        // media/1/profile.png
        // media/2/profile.png
        String uploadPath = profileDirectory + "profile." + extension;
        try {
            image.transferTo(Path.of(uploadPath));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 3. 업로드에 성공하면, 이미지 URL을 Entity에 저장한다.
        // http://localhost:8080/static/{userId}/profile.png|jpeg
        String reqPath = "/static/" + id + "/profile." + extension;
        UserEntity target = optionalUser.get();
        target.setProfileImagePath(reqPath);

        // 4. User Entity를 DTO로 변환해서 반환한다.
        return UserDto.fromEntity(userRepository.save(target));

    }
}
