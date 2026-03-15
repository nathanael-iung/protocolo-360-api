package com.protocolo360.api.config;

import com.protocolo360.api.modules.auth.repository.UserRepository;
import com.protocolo360.api.modules.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    var token = jwtService.recoverToken(request);

    if (token != null) {

      var claims = jwtService.getClaims(token);

      if (claims != null) {

        String email = claims.getSubject();
        List<String> roles = claims.get("roles", List.class);

        var authorities = roles.stream()
            .map(SimpleGrantedAuthority::new)
            .toList();

        // We use a simple Principal (the email) instead of the full User entity
        var authentication = new UsernamePasswordAuthenticationToken(
            email,
            null,
            authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }
    filterChain.doFilter(request, response);
  }

}