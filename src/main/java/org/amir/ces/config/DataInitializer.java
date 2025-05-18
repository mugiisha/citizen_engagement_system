package org.amir.ces.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.amir.ces.model.Agency;
import org.amir.ces.model.Role;
import org.amir.ces.model.User;
import org.amir.ces.model.UserStatus;
import org.amir.ces.repository.UserRepository;
import org.amir.ces.service.AgencyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    private final AgencyService agencyService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Ensure Administration department exists
        agencyService.ensureAdministrationDepartmentExists();

        // Create default admin user if not exists
        createDefaultAdminUser();

    }

    private void createDefaultAdminUser() {
        if (!userRepository.existsByEmail(adminEmail)) {
            Agency adminDepartment = agencyService.getAgencyByName("Administration");

            User adminUser = User.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .agency(adminDepartment)
                    .build();

            userRepository.save(adminUser);
            log.info("Default admin user created");
        }
    }

}
