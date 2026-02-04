package com.protocolo360.api.modules.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.protocolo360.api.modules.auth.dto.LoginRequest;
import com.protocolo360.api.modules.auth.dto.LoginResponse;
import com.protocolo360.api.modules.auth.dto.RegisterRequest;
import com.protocolo360.api.modules.auth.model.User;
import com.protocolo360.api.modules.auth.repository.UserRepository;
import com.protocolo360.api.shared.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public User registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessException("This email is already registered.", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .birthDate(request.birthDate())
                .phone(request.phone())
                .gender(request.gender())
                .goals(request.goals())
                .build();

        return userRepository.save(user);
    }

    public LoginResponse authenticate(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("Invalid credentials", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtService.generateToken(user.getEmail());
        return new LoginResponse(token);
    }
}
