package com.tpe.cinetime.payload.mapper;

import com.tpe.cinetime.entity.User;
import com.tpe.cinetime.payload.request.RegisterRequestDTO;
import com.tpe.cinetime.payload.response.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User mapRegisterRequestDTOToUser(RegisterRequestDTO registerRequestDTO){

        return User.builder()
                .name(registerRequestDTO.getName())
                .surname(registerRequestDTO.getSurname())
                .email(registerRequestDTO.getEmail())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .phoneNumber(registerRequestDTO.getPhoneNumber())
                .birthDate(registerRequestDTO.getBirthDate())
                .gender(registerRequestDTO.getGender())
                .build();
    }

    public UserResponseDTO mapUserToUserResponseDTO(User user){

        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .birthDate(user.getBirthDate())
                .gender(user.getGender().name())
                .role(user.getRole().getRoleName().name())
                .build();
    }
}
