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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

        UserEntity user = userRepository.findByUserName(normalizedUsername);
        if (user == null) {
            user = createOidcUser(normalizedUsername, claims);
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getUserRole()));

        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), "email");
    }

    private Optional<String> extractUsername(Map<String, Object> claims) {
        Object email = claims.get("email");
        if (email instanceof String s && !s.isBlank()) return Optional.of(s);

        Object preferredUsername = claims.get("preferred_username");
        if (preferredUsername instanceof String s && !s.isBlank()) return Optional.of(s);

        Object sub = claims.get("sub");
        if (sub instanceof String s && !s.isBlank()) return Optional.of(s);

        return Optional.empty();
    }

    private UserEntity createOidcUser(String normalizedUsername, Map<String, Object> claims) {
        UserEntity user = new UserEntity();
        user.setUserName(normalizedUsername);
        user.setActive(true);
        user.setUserRole(SystemConstant.USER_ROLE);
        user.setEncrytedPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

        Object fullName = claims.get("name");
        if (fullName instanceof String s && !s.isBlank()) {
            user.setFullName(s);
        } else {
            user.setFullName(user.getUserName());
        }

        user.setPhone("0000000000");
        return userRepository.save(user);
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
