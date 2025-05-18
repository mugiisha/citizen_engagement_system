package org.amir.ces.service;

import lombok.RequiredArgsConstructor;
import org.amir.ces.dto.AuthenticationRequest;
import org.amir.ces.dto.AuthenticationResponse;
import org.amir.ces.dto.UserDataDTO;
import org.amir.ces.exception.BadRequestException;
import org.amir.ces.exception.ForbiddenException;
import org.amir.ces.exception.UnauthorizedException;
import org.amir.ces.model.UserStatus;
import org.amir.ces.repository.UserRepository;
import org.amir.ces.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final EmailTemplateService emailTemplateService;

    @Value("${admin.email}")
    private String adminEmail;


    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        // Check if user has set their password (for invited users)
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new ForbiddenException("Your account requires password setup. Please contact an administrator.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid email or password");
        }

        // Check if user is approved
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new ForbiddenException("Your account is not deactivated. Please call the administration if you think this is wrong.");
        }

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // Create UserDataDTO
        UserDataDTO userData = UserDataDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .agency(user.getAgency().getName())
                .role(user.getRole())
                .status(user.getStatus())
                .build();

        return AuthenticationResponse.builder()
                .user(userData)
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new BadRequestException("Refresh token is required");
        }

        try {
            // Extract username from refresh token
            String email = jwtService.extractUsername(refreshToken);

            // Check if token is valid
            if (email == null) {
                throw new UnauthorizedException("Invalid refresh token");
            }

            // Get user from database
            var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UnauthorizedException("User not found"));

            // Check if token is valid for this user
            if (!jwtService.isTokenValid(refreshToken, user)) {
                throw new UnauthorizedException("Invalid refresh token");
            }

            // Check if user is approved
            if (user.getStatus() == UserStatus.INACTIVE) {
                throw new ForbiddenException("Your account is deactivated. Please call the administration if you think this is wrong.");
            }

            // Generate new tokens
            var newJwtToken = jwtService.generateToken(user);
            var newRefreshToken = jwtService.generateRefreshToken(user);

            // Create UserDataDTO
            UserDataDTO userData = UserDataDTO.builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .status(user.getStatus())
                    .build();

            return AuthenticationResponse.builder()
                    .user(userData)
                    .token(newJwtToken)
                    .refreshToken(newRefreshToken)
                    .build();
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid refresh token");
        }
    }
}
