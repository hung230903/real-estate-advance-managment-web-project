package com.webapp.config;

import com.webapp.constant.SystemConstant;
import com.webapp.filters.JwtTokenFilter;
import com.webapp.security.CustomSuccessHandler;
import com.webapp.security.oauth2.DatabaseOAuth2UserService;
import com.webapp.security.oauth2.DatabaseOidcUserService;
import com.webapp.services.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenFilter jwtTokenFilter;
    private final DatabaseOAuth2UserService databaseOAuth2UserService;
    private final DatabaseOidcUserService databaseOidcUserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final PasswordEncoder passwordEncoder;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/login", "/login/**", "/register", String.format("%s/users/login", apiPrefix), String.format("%s/users/register", apiPrefix), "/access-denied", "/", "/assets/**", "/web/**").permitAll()
                        .requestMatchers(String.format("%s/users/userImage", apiPrefix)).hasAnyAuthority(SystemConstant.STAFF_ROLE, SystemConstant.MANAGER_ROLE)
                        .requestMatchers(String.format("%s/users/**", apiPrefix),
                                String.format("%s/buildings/assign", apiPrefix),
                                String.format("%s/buildings/{id}/staff", apiPrefix)).hasAuthority(SystemConstant.MANAGER_ROLE)
                        .requestMatchers("/admin/customers/**", "/admin/api/customers/**", "/admin/api/transactions/**", "/admin/**").hasAnyAuthority(SystemConstant.STAFF_ROLE, SystemConstant.MANAGER_ROLE)
                .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(databaseOAuth2UserService)
                        .oidcUserService(databaseOidcUserService))
                .successHandler(customSuccessHandler)
                .failureUrl("/login?oauth2Error"))
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(customSuccessHandler)
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .failureUrl("/login?incorrectAccount").permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .deleteCookies("JSESSIONID")
                        .permitAll()

                );
        return http.build();
    }
}

