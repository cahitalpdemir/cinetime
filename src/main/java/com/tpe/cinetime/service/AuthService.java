package com.tpe.cinetime.service;

import com.tpe.cinetime.entity.Role;
import com.tpe.cinetime.entity.User;
import com.tpe.cinetime.enums.RoleName;
import com.tpe.cinetime.exception.BadRequestException;
import com.tpe.cinetime.payload.mapper.UserMapper;
import com.tpe.cinetime.payload.request.authentication.LoginRequestDTO;
import com.tpe.cinetime.payload.request.authentication.RegisterRequestDTO;
import com.tpe.cinetime.payload.response.authentication.LoginResponseDTO;
import com.tpe.cinetime.payload.response.ResponseMessage;
import com.tpe.cinetime.payload.response.user.UserResponseDTO;
import com.tpe.cinetime.repository.user.RoleRepository;
import com.tpe.cinetime.repository.user.UserRepository;
import com.tpe.cinetime.security.JwtUtils;
import com.tpe.cinetime.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtil;

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

    public ResponseMessage<LoginResponseDTO> login(LoginRequestDTO loginRequestDTO) {

        log.info("Inside login method");

        //LoginRequestDTO'dan gelen email ve password bilgilerini aliyoruz
        String email = loginRequestDTO.getEmail();
        String password = loginRequestDTO.getPassword();

        //AuthenticationManager verilen email ve password bilgisini kontrol ediyor
        //Eğer bilgiler doğruysa Authentication objesi oluşturuluyor
        //Arka planda UserDetailsService calisir ve DB'den user bilgileri alinir
        //PasswordEncoder ile sifre kontrol edilir
        //AuthenticationManager'dan gelen Authentication objesi UserDetailsImpl'e donusur
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));

        //Dogru bilgilerle gelen Authentication objesi SecurityContext'e kayit edilir
        //Böylece bu request boyunca user authenticated olarak kayit edilir
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Basarili authentication bilgisinden Token oluşturulur
        String generatedToken = jwtUtil.generateJwt(authentication);

        //Authentication principal bilgisini kendi UserDetailsImpl objesine donusturuyoruz
        //Bu obje User bilgilerini ve rollerini içermektedir
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        //UserDetailsImpl objesini LoginResponseDTO'ya donusturuyoruz
        LoginResponseDTO loginResponseDTO = userMapper.mapUserDetailsImplToLoginResponseDTO(userDetails, generatedToken);

        //Standart ResponseMessage yapisi ile login response donuyoruz
        return ResponseMessage.<LoginResponseDTO>builder()
                .object(loginResponseDTO)
                .message("Login successful")
                .httpStatus(HttpStatus.OK)
                .build();

    }
}
