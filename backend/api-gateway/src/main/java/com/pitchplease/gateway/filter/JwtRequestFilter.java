package com.pitchplease.gateway.filter;

import com.pitchplease.gateway.client.UserServiceClient;
import com.pitchplease.gateway.model.Token;
import feign.FeignException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserServiceClient userServiceClient;

    // Same public endpoints as used in your JwtAuthenticationFilter
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh_token",
            "/api/auth/logout"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Skip filtering for public endpoints
        if (PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (Token.isBearerToken(authHeader)) {
            String jwt = Token.getJwt(authHeader);

            try {
                userServiceClient.validateToken(jwt);
                log.debug("Token validated for path: {}", path);

                // You can optionally extract user info from the token and set auth context
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken("user", null, List.of());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            } catch (FeignException.Unauthorized | FeignException.Forbidden e) {
                log.warn("Unauthorized access attempt to: {}", path);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
                return;
            } catch (Exception e) {
                log.error("Error validating JWT for path: {}", path, e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Error");
                return;
            }
        } else {
            log.warn("Missing or invalid Authorization header for path: {}", path);
        }

        filterChain.doFilter(request, response);
    }
}
