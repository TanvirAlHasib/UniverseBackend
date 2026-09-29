package com.amr.uniVerse.auth.service;

import com.amr.uniVerse.auth.dto.AuthResponse;
import com.amr.uniVerse.auth.dto.LoginRequest;
import com.amr.uniVerse.auth.dto.RegisterRequest;
import com.amr.uniVerse.auth.entity.Department;
import com.amr.uniVerse.auth.entity.Role;
import com.amr.uniVerse.auth.entity.User;
import com.amr.uniVerse.auth.repository.DepartmentRepository;
import com.amr.uniVerse.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already in use");
        }

        // 👇 NEW: Prevent anyone from registering as ADMIN
        if (request.role() == Role.ADMIN) {
            throw new RuntimeException("Cannot register as ADMIN");
        }

        Department department = departmentRepository.findByCode(request.departmentCode())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        var user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .department(department)
                .role(request.role())
                .enabled(true)
                .build();

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }
}