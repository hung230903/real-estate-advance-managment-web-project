package com.webapp.security.oauth2;

import com.webapp.constant.SystemConstant;
import com.webapp.entities.UserEntity;
import com.webapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DatabaseOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = delegate.loadUser(userRequest);
        Map<String, Object> attributes = oauth2User.getAttributes();

        String rawUsername = extractUsername(attributes)
                .orElseThrow(() -> new OAuth2AuthenticationException(new OAuth2Error(
                        "invalid_user_info",
                        "Cannot determine username/email from OAuth2 provider response",
                        null
                )));
        String normalizedUsername = normalizeUsername(rawUsername);

        UserEntity userEntity = userRepository.findByUserName(normalizedUsername);
        if (userEntity == null) {
            userEntity = createOAuth2User(normalizedUsername, attributes);
        }

        // Ensure role has ROLE_ prefix for Spring Security
        String role = userEntity.getUserRole();
        if (role != null && !role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        } else if (role == null) {
            role = SystemConstant.USER_ROLE;
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        String nameAttributeKey = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        if (nameAttributeKey == null || nameAttributeKey.isBlank()) {
            nameAttributeKey = "email";
        }

        return new DefaultOAuth2User(authorities, attributes, nameAttributeKey);
    }

    private Optional<String> extractUsername(Map<String, Object> attributes) {
        Object email = attributes.get("email");
        if (email instanceof String s && !s.isBlank()) return Optional.of(s);

        Object preferredUsername = attributes.get("preferred_username");
        if (preferredUsername instanceof String s && !s.isBlank()) return Optional.of(s);

        Object login = attributes.get("login"); // GitHub
        if (login instanceof String s && !s.isBlank()) return Optional.of(s);

        Object sub = attributes.get("sub"); // OIDC subject
        if (sub instanceof String s && !s.isBlank()) return Optional.of(s);

        return Optional.empty();
    }

    private UserEntity createOAuth2User(String normalizedUsername, Map<String, Object> attributes) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(normalizedUsername);
        userEntity.setActive(true);
        userEntity.setUserRole(SystemConstant.USER_ROLE);
        userEntity.setEncrytedPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

        Object fullName = attributes.get("name");
        if (fullName instanceof String s && !s.isBlank()) {
            userEntity.setFullName(s);
        } else {
            userEntity.setFullName(userEntity.getUserName());
        }

        // Required by current schema, OAuth2 providers do not always return phone.
        userEntity.setPhone("0000000000");

        return userRepository.save(userEntity);
    }

    private String normalizeUsername(String rawUsername) {
        if (rawUsername.length() <= 20) {
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