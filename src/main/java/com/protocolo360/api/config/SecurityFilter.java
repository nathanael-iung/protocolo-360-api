package com.protocolo360.api.config;

import com.protocolo360.api.modules.auth.repository.UserRepository;
import com.protocolo360.api.modules.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    var token = this.recoverToken(request);

    if (token != null) {
      var login = jwtService.validateToken(token);

      if (login != null) {
        userRepository.findByEmail(login).ifPresent(user -> {
          var authentication = new UsernamePasswordAuthenticationToken(
              user,
              null,
              user.getAuthorities());

          SecurityContextHolder.getContext().setAuthentication(authentication);
        });
      }
    }
    filterChain.doFilter(request, response);
  }

  private String recoverToken(HttpServletRequest request) {
    if (request.getCookies() == null)
      return null;

    return Arrays.stream(request.getCookies())
        .filter(cookie -> "protocolo360_auth".equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst()
        .orElse(null);
  }
}