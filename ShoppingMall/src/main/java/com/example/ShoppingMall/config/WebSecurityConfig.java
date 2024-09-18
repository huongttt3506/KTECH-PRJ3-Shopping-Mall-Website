package com.example.ShoppingMall.config;


import com.example.ShoppingMall.jwt.JwtTokenFilter;
import com.example.ShoppingMall.jwt.JwtTokenUtils;
import com.example.ShoppingMall.user.UserDetails.CustomUserDetailsService;
import com.example.ShoppingMall.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

// configures how Spring Security handles HTTP requests
// ensures that JWT-based authentication is enforced.
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {
    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    // Bean to configure authentication manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
        http
                //JWT tokens are stateless and don’t require server-side sessions
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> {
                            auth.requestMatchers(
                                            "/users/login",
                                            "/users/register"
                                    )
                                    .anonymous();
                            // Endpoints accessible only by authenticated users
                            auth.requestMatchers("/users/{userId}/updateEssentialInfo")
                                    .authenticated();

                            auth.requestMatchers("/users/{userId}/updateProfileImage")
                                    .authenticated();

                            auth.requestMatchers("/users/{userId}/business")
                                    .authenticated();

                            // Endpoints accessible only by admins
                            auth.requestMatchers("/users/admin/**")
                                    .authenticated();

                            // Endpoints for ShopController
                            auth.requestMatchers(
                                    // read
                                    "/shops",
                                    "/shops/{shopId}",
                                    //update info
                                    "/shops/{shopId}/update-info",
                                    // shop register request
                                    "/shops/{shopId}/register",
                                    // read register request
                                    "/shops/registrations",
                                    "/shops/registrations/{shopRegId}",
                                    // admin approval
                                    "/shops/registrations/{shopRegId}/accept",
                                    //admin decline
                                    "/shops//registrations/{shopRegId}/decline",
                                    //shop close request
                                    "/shops/close/{shopId}",
                                    //view
                                    "/shops/close-requests",
                                    "/shops/close-requests/{closeReqId}",
                                    //admin approve
                                    "shops/close-requests/{closeReqId}/approve",
                                    //search shop
                                    "/shops/search"
                            ).authenticated(); // Allow all authenticated users
                            //endpoint for items
                            auth.requestMatchers("/items/add," +
                                    "items/{itemId}/view",
                                    "items/{itemId}/update",
                                    "items/{itemId}/delete",
                                    "items/admin",
                                    "items/{shopId}").authenticated();
                            //endpoint for order item
                            auth.requestMatchers("/order",
                                    "/order/user",
                                    "/order/shop/{shopId}",
                                    "/order/{orderId}",
                                    "/order/{orderId}/confirm",
                                    "/order/{orderId}/cancel"
                                    ).authenticated();
                            //endpoint for cart


                            // All different endpoints:
                            auth.anyRequest().authenticated();
                        }
                )


                // JWT를 사용하기 때문에 보안 관련 세션 해제
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(
                        new JwtTokenFilter(jwtTokenUtils),
                        AuthorizationFilter.class
                );

        return http.build();
    }





}
