package com.webapp.security.oauth2;

import com.webapp.constant.SystemConstant;
import com.webapp.entities.UserEntity;
import com.webapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DatabaseOidcUserService extends OidcUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        Map<String, Object> claims = oidcUser.getClaims();

        String rawUsername = extractUsername(claims)
                .orElseThrow(() -> new OAuth2AuthenticationException(new OAuth2Error(
                        "invalid_user_info",
                        "Cannot determine username/email from OIDC provider response",
                        null
                )));
        String normalizedUsername = normalizeUsername(rawUsername);
        UserEntity userEntity = userRepository.findByUserName(normalizedUsername);
        if (userEntity == null) {
            userEntity = createOidcUser(normalizedUsername, claims);
        }

        // Ensure role has ROLE_ prefix for Spring Security
        String role = userEntity.getUserRole();
        if (role != null && !role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        } else if (role == null) {
            role = SystemConstant.USER_ROLE;
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        // Use the name attribute key from registration or fallback to email
        String nameAttributeKey = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        if (nameAttributeKey == null || nameAttributeKey.isBlank() ||
                (oidcUser.getIdToken().getClaims().get(nameAttributeKey) == null &&
                 (oidcUser.getUserInfo() == null || oidcUser.getUserInfo().getClaims().get(nameAttributeKey) == null))) {
            nameAttributeKey = Stream.of("email", "sub", "preferred_username")
                    .filter(k -> oidcUser.getIdToken().getClaims().containsKey(k) ||
                                (oidcUser.getUserInfo() != null && oidcUser.getUserInfo().getClaims().containsKey(k)))
                    .findFirst()
                    .orElse("sub");
        }

        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), nameAttributeKey);
    }

    private Optional<String> extractUsername(Map<String, Object> claims) {
        return Stream.of("email", "preferred_username", "sub")
                .map(claims::get)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .filter(s -> !s.isBlank())
                .findFirst();
    }

    private UserEntity createOidcUser(String normalizedUsername, Map<String, Object> claims) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(normalizedUsername);
        userEntity.setActive(true);
        userEntity.setUserRole(SystemConstant.USER_ROLE);
        userEntity.setEncrytedPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

        Object fullName = claims.get("name");
        if (fullName instanceof String s && !s.isBlank()) {
            userEntity.setFullName(s);
        } else {
            userEntity.setFullName(userEntity.getUserName());
        }

        userEntity.setPhone("0000000000");
        return userRepository.save(userEntity);
    }

    private String normalizeUsername(String rawUsername) {
        if (rawUsername.length() <= 30) { // 30 is the maximum length of the username
            return rawUsername;
        }
        return buildHashedUsername(rawUsername);
    }

    private String buildHashedUsername(String value) {
        String prefix = value.substring(0, Math.min(10, value.length()));
        String hash = sha256Hex(value).substring(0, 8);
        return (prefix + "_" + hash).substring(0, Math.min(20, prefix.length() + 1 + hash.length()));
    }

    private String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available", e);
        }
    }
}