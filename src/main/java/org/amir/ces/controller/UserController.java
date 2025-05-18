package org.amir.ces.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.amir.ces.dto.ApiResponse;
import org.amir.ces.dto.RegisterUserDto;
import org.amir.ces.dto.UpdateUserDto;
import org.amir.ces.dto.UserResponseDto;
import org.amir.ces.model.User;
import org.amir.ces.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<ApiResponse<UserResponseDto>> registerUser(@RequestBody @Valid RegisterUserDto registerUserDto) {
        User user = userService.registerUser(registerUserDto);
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .agency(user.getAgency().getName())
                .build();
        return ResponseEntity.ok(ApiResponse.success(userResponseDto, "User registered successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .agency(user.getAgency().getName())
                .build();
        return ResponseEntity.ok(ApiResponse.success(userResponseDto, "User retrieved successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@PathVariable Long id, @RequestBody @Valid UpdateUserDto updateUserDto) {
        User user = userService.updateUser(id, updateUserDto);
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .agency(user.getAgency().getName())
                .build();
        return ResponseEntity.ok(ApiResponse.success(userResponseDto, "User updated successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }
}
