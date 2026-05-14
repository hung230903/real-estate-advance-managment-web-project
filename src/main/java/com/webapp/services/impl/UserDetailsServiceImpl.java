package com.webapp.services.impl;

import com.webapp.entities.UserEntity;
import com.webapp.repositories.AccountRepository;
import com.webapp.repositories.UserRepository;
import com.webapp.security.MyUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AccountRepository accountRepository;

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUserName(username);

        if (userEntity == null) {
            throw new UsernameNotFoundException("UserEntity " //
                    + username + " was not found in the database");
        }

        // EMPLOYEE,MANAGER,..
        String role = userEntity.getUserRole();

        List<GrantedAuthority> grantList = new ArrayList<>();

        // Ensure role has ROLE_ prefix for Spring Security
        if (role != null && !role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        GrantedAuthority authority = new SimpleGrantedAuthority(role);

        grantList.add(authority);

        boolean enabled = userEntity.isActive();
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        MyUser userDetails = new MyUser(userEntity.getUserName(),
                userEntity.getEncrytedPassword(), enabled, accountNonExpired,
                credentialsNonExpired, accountNonLocked, grantList);

        userDetails.setId(userEntity.getId());
        userDetails.setFullName(userEntity.getFullName());
        userDetails.setRole(role);

        return userDetails;
    }
}
