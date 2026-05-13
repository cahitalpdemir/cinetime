package com.tpe.cinetime.service;

import com.tpe.cinetime.entity.Role;
import com.tpe.cinetime.entity.User;
import com.tpe.cinetime.enums.RoleName;
import com.tpe.cinetime.exception.BadRequestException;
import com.tpe.cinetime.exception.NotFoundException;
import com.tpe.cinetime.payload.mapper.UserMapper;
import com.tpe.cinetime.payload.request.RegisterRequestDTO;
import com.tpe.cinetime.payload.response.ResponseMessage;
import com.tpe.cinetime.payload.response.UserResponseDTO;
import com.tpe.cinetime.repository.RoleRepository;
import com.tpe.cinetime.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    public ResponseMessage<UserResponseDTO> register(RegisterRequestDTO registerRequestDTO) {

        log.info("Inside register method");
        //check if email is already registered
        if(userRepository.existsByEmail(registerRequestDTO.getEmail())){
            throw new BadRequestException("Email already exist");
        }

        Role customerRole = roleRepository.findByRoleName(RoleName.CUSTOMER)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .roleName(RoleName.CUSTOMER)
                                .build()
                ));

        User user = userMapper.mapRegisterRequestDTOToUser(registerRequestDTO);
        user.setRole(customerRole);
        user.setBuiltIn(false);

        User savedUser = userRepository.save(user);
        UserResponseDTO userResponseDTO = userMapper.mapUserToUserResponseDTO(savedUser);

        return ResponseMessage.<UserResponseDTO>builder()
                .object(userResponseDTO)
                .message("User registered successfully")
                .httpStatus(HttpStatus.CREATED)
                .build();
    }
}
