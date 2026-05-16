package com.tpe.cinetime.repository.user;

import com.tpe.cinetime.entity.Role;
import com.tpe.cinetime.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(String name);

    @Query("SELECT r FROM Role r WHERE r.roleName = 'CUSTOMER'")
    Optional<Role> findByRoleName(RoleName roleName);

    boolean existsByRoleName(RoleName roleName);
}
