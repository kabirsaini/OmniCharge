package com.omnicharge.recharge.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.security.Key;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    // Secret key for signing and verifying JWTs (should be at least 256 bits for HS256)
    @Value("${jwt.secret}") private String secret;

    @Override
    // This method is called for every incoming HTTP request. It checks for the presence of a JWT in the "Authorization" header,
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {

        String h = req.getHeader("Authorization"); // The filter retrieves the "Authorization" header from the incoming request.
        // If the header is present and starts with "Bearer ", it proceeds to validate the JWT.

        // The filter checks if the "Authorization" header is present and starts with "Bearer ".
        // If it does, it extracts the JWT from the header (by removing the "Bearer " prefix) and attempts to validate it.

        if (h != null && h.startsWith("Bearer ")) {
            try {
                Key key = Keys.hmacShaKeyFor(secret.getBytes()); // The filter uses the secret key to create a signing key for validating the JWT.

                // The filter parses the JWT, validates its signature, and extracts the claims (payload) from the token.
                // If the token is valid, it retrieves the user's role from the claims and sets the authentication
                // in the security context, allowing the user to access protected resources based on their role.
                Claims c = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(h.substring(7))
                        .getBody();

                // The filter retrieves the user's role from the claims and sets the authentication in the security context, allowing the user to access protected resources based on their role.
                String role = c.get("role", String.class);

                // The filter sets the authentication in the security context using the user's subject (username) and their role.
                // This allows Spring Security to authorize the user based on their role when they access protected resources.
                SecurityContextHolder.getContext()
                        .setAuthentication(new UsernamePasswordAuthenticationToken(c.getSubject(), null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        chain.doFilter(req, res); // Finally, the filter continues the filter chain, allowing the request to proceed to the next filter or the target resource.
    }
}
