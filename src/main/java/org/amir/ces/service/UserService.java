package org.amir.ces.service;

import lombok.RequiredArgsConstructor;
import org.amir.ces.dto.RegisterUserDto;
import org.amir.ces.dto.UpdateUserDto;
import org.amir.ces.dto.UserResponseDto;
import org.amir.ces.exception.BadRequestException;
import org.amir.ces.helper.PasswordGenerator;
import org.amir.ces.model.Agency;
import org.amir.ces.model.User;
import org.amir.ces.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final AgencyService agencyService;
    private final PasswordEncoder passwordEncoder;


    public User registerUser(RegisterUserDto registerUserDto){
        String password = PasswordGenerator.generatePassword(registerUserDto.getFirstName(), 8);

        // Check if the email already exists
        if (userRepository.existsByEmail(registerUserDto.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        Agency agency = agencyService.getAgencyByName(registerUserDto.getAgency());

        User user = User.builder()
                .firstName(registerUserDto.getFirstName())
                .lastName(registerUserDto.getLastName())
                .email(registerUserDto.getEmail())
                .role(registerUserDto.getRole())
                .agency(agency)
                .password(passwordEncoder.encode(password))
                .build();

        emailService.sendInvitationEmail(user, password);

        userRepository.save(user);

        return user;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }


    public User updateUser(Long id, UpdateUserDto updateUserDto) {
        User user = getUserById(id);
        user.setEmail(updateUserDto.getEmail());
        user.setRole(updateUserDto.getRole());
        if(updateUserDto.getAgencyId() != user.getAgency().getId()){
            Agency agency = agencyService.getAgencyById(updateUserDto.getAgencyId());
            user.setAgency(agency);
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> UserResponseDto.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .role(user.getRole().toString())
                        .agency(user.getAgency().getName())
                        .build())
                .toList();
    }
}
