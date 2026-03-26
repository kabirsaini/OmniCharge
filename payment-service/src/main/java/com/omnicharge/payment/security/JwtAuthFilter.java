package com.omnicharge.payment.security;

import io.jsonwebtoken.*;
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
    @Value("${jwt.secret}") private String secret;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String h = req.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) {
            try {
                Key key = Keys.hmacShaKeyFor(secret.getBytes());
                Claims c = Jwts.parserBuilder().setSigningKey(key).build()
                        .parseClaimsJws(h.substring(7)).getBody();
                String role = c.get("role", String.class);
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(c.getSubject(), null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))));
            } catch (Exception ignored) {}
        }
        chain.doFilter(req, res);
    }
}
