package com.protocolo360.api.modules.auth.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.protocolo360.api.modules.auth.dto.LoginRequest;
import com.protocolo360.api.modules.auth.dto.LoginResponse;
import com.protocolo360.api.modules.auth.dto.RegisterRequest;
import com.protocolo360.api.modules.auth.dto.TokenMetadata;
import com.protocolo360.api.modules.auth.model.User;
import com.protocolo360.api.modules.auth.repository.UserRepository;
import com.protocolo360.api.modules.goal.model.Goal;
import com.protocolo360.api.modules.goal.repository.GoalRepository;
import com.protocolo360.api.modules.roles.model.Role;
import com.protocolo360.api.modules.roles.repository.RoleRepository;
import com.protocolo360.api.shared.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final GoalRepository goalRepository;
    private final RoleRepository roleRepository;

    public User registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessException("This email is already registered.", HttpStatus.CONFLICT);
        }

        Set<Goal> userGoals = request.goals().stream()
                .map(goalId -> goalRepository.findById(goalId)
                        .orElseThrow(() -> new BusinessException("Goal not found: " + goalId, HttpStatus.BAD_REQUEST)))
                .collect(Collectors.toSet());

        Role standardRole = roleRepository.findByName("STANDARD")
                .orElseThrow(() -> new BusinessException("Default role not found", HttpStatus.INTERNAL_SERVER_ERROR));

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .birthDate(request.birthDate())
                .phone(request.phone())
                .gender(request.gender())
                .goals(userGoals)
                .roles(new HashSet<>(Set.of(standardRole)))
                .build();

        return userRepository.save(user);
    }

    public LoginResponse authenticate(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("Invalid credentials", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        TokenMetadata tokenData = jwtService.generateToken(user.getEmail());

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new LoginResponse(
                user.getEmail(),
                roles,
                tokenData.issuedAt(),
                tokenData.expiresAt());
    }
}
