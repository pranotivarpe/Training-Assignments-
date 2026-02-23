package com.votingsystem.config;

import com.votingsystem.model.Role;
import com.votingsystem.model.User;
import com.votingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Initializes default data on application startup.
 * Creates a default admin user if none exists.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Create default admin if not exists
            if (userRepository.findByEmail("admin@votingsystem.com").isEmpty()) {
                User admin = User.builder()
                        .fullName("System Administrator")
                        .email("admin@votingsystem.com")
                        .password(passwordEncoder.encode("admin123"))
                        .voterId("ADMIN-001")
                        .role(Role.ROLE_ADMIN)
                        .enabled(true)
                        .build();
                userRepository.save(admin);
                log.info("Default admin user created: admin@votingsystem.com / admin123");
            }
        };
    }
}
