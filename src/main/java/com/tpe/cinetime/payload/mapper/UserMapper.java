package com.tpe.cinetime.payload.mapper;

import com.tpe.cinetime.entity.User;
import com.tpe.cinetime.payload.request.authentication.RegisterRequestDTO;
import com.tpe.cinetime.payload.response.authentication.LoginResponseDTO;
import com.tpe.cinetime.payload.response.user.UserResponseDTO;
import com.tpe.cinetime.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
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

    public LoginResponseDTO mapUserDetailsImplToLoginResponseDTO(UserDetailsImpl userDetailsImpl, String token){

        String role = userDetailsImpl.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        return LoginResponseDTO.builder()
                .id(userDetailsImpl.getId())
                .email(userDetailsImpl.getUsername())
                .role(role)
                .token(token)
                .tokenType("Bearer")
                .build();

    }
}
