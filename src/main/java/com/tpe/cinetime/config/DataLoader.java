package com.tpe.cinetime.config;

import com.tpe.cinetime.entity.Role;
import com.tpe.cinetime.enums.RoleName;
import com.tpe.cinetime.repository.user.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        for (RoleName roleName : RoleName.values()){
            if (!roleRepository.existsByRoleName(roleName)){
                roleRepository.save(Role.builder()
                        .roleName(roleName)
                        .build());
            }
        }
    }
}
