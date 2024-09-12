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

                            auth.requestMatchers("/users/{userId}/updateEssentialInfo")
                                    .authenticated();

                            auth.requestMatchers("/users/{userId}/updateProfileImage")
                                    .authenticated();

                            auth.requestMatchers("/users/{userId}/business")
                                    .authenticated();

                            auth.requestMatchers("/users/admin/**")
                                    .authenticated();


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
