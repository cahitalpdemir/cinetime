package com.tpe.cinetime.config;

import com.tpe.cinetime.entity.Role;
import com.tpe.cinetime.entity.User;
import com.tpe.cinetime.enums.Gender;
import com.tpe.cinetime.enums.RoleName;
import com.tpe.cinetime.repository.user.RoleRepository;
import com.tpe.cinetime.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    String adminEmail;

    @Value("${app.admin.password}")
    String adminPassword;

    @Override
    public void run(String... args) throws Exception {

        Role adminRole = roleRepository.findByRoleName(RoleName.ADMIN)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .roleName(RoleName.ADMIN)
                                .build()
                ));

        if (!userRepository.existsByEmail(adminEmail)){
            User admin = User.builder()
                    .name("Admin")
                    .surname("Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .phoneNumber("5551112233")
                    .builtIn(true)
                    .birthDate(new Date())
                    .role(adminRole)
                    .gender(Gender.MALE)
                    .build();

            userRepository.save(admin);
            log.info("Admin user created: {}", adminEmail);
        }

    }
}
