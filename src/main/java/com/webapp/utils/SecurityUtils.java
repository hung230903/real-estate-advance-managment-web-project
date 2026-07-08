package com.webapp.utils;

import com.webapp.security.MyUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public class SecurityUtils {

  public static MyUser getPrincipal() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof MyUser) {
      return (MyUser) authentication.getPrincipal();
    }
    return null;
  }

  public static List<String> getAuthorities() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      return authentication.getAuthorities().stream()
          .map(GrantedAuthority::getAuthority)
          .toList();
    }
    return List.of();
  }
}
