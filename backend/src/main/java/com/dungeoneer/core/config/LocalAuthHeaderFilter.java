package com.dungeoneer.core.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

/**
 * Convenience filter for local testing and Swagger UI.
 * If X-User-Id is missing but Authorization header is present,
 * extracts the 'sub' UUID claim from the JWT payload and injects X-User-Id.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class LocalAuthHeaderFilter extends OncePerRequestFilter {

    private static final String HEADER_X_USER_ID = "X-User-Id";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String existingXUserId = request.getHeader(HEADER_X_USER_ID);
        String authHeader = request.getHeader(HEADER_AUTHORIZATION);

        if ((existingXUserId == null || existingXUserId.isBlank()) && authHeader != null) {
            String token = authHeader.trim();
            // Handle multiple Bearer prefixes defensively in case user pastes "Bearer eyJ..." into Swagger
            while (token.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
                token = token.substring(BEARER_PREFIX.length()).trim();
            }

            String[] parts = token.split("\\.");
            if (parts.length >= 2) {
                try {
                    byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
                    JsonNode node = objectMapper.readTree(payloadBytes);
                    if (node.has("sub")) {
                        String sub = node.get("sub").asText();
                        UUID.fromString(sub); // Validate UUID format

                        HeaderEnrichingRequestWrapper wrapper = new HeaderEnrichingRequestWrapper(request, sub);
                        filterChain.doFilter(wrapper, response);
                        return;
                    }
                } catch (Exception ignored) {
                    // If parsing fails, continue down the filter chain
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    static class HeaderEnrichingRequestWrapper extends HttpServletRequestWrapper {
        private final String userId;

        HeaderEnrichingRequestWrapper(HttpServletRequest request, String userId) {
            super(request);
            this.userId = userId;
        }

        @Override
        public String getHeader(String name) {
            if (HEADER_X_USER_ID.equalsIgnoreCase(name)) {
                return userId;
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if (HEADER_X_USER_ID.equalsIgnoreCase(name)) {
                return Collections.enumeration(List.of(userId));
            }
            return super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Set<String> names = new LinkedHashSet<>(Collections.list(super.getHeaderNames()));
            names.add(HEADER_X_USER_ID);
            return Collections.enumeration(names);
        }
    }
}
