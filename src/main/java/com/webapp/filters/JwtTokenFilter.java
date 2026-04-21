package com.webapp.filters;

import com.webapp.components.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;

    private final JwtTokenUtils jwtTokenUtil;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (isBypassToken(request)) {
                filterChain.doFilter(request, response); //enable bypass
                return;
            }
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            final String token = authHeader.substring(7);
            final String userName = jwtTokenUtil.extractUserName(token);
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                if (jwtTokenUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response); //enable bypass
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }

    }

    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        final List<String[]> bypassTokens = Arrays.asList(
                new String[]{String.format("%s/users/register", apiPrefix), "POST"},
                new String[]{String.format("%s/users/login", apiPrefix), "POST"},
                new String[]{"/login", "GET"},
                new String[]{"/login", "POST"},
                new String[]{"/register", "GET"},
                new String[]{"/register", "POST"},
                new String[]{"/", "GET"},
                new String[]{"/assets/", "GET"},
                new String[]{"/web/", "GET"}
        );
        String servletPath = request.getServletPath();
        String method = request.getMethod();

        if (servletPath.contains("/assets/") || servletPath.contains("/web/")) {
            return true;
        }

        for (String[] bypassToken : bypassTokens) {
            if (servletPath.startsWith(bypassToken[0]) &&
                    method.equals(bypassToken[1])) {
                return true;
            }
        }
        return false;
    }
}
