package com.tpe.cinetime.service.authentication;

import com.tpe.cinetime.constants.messages.ErrorMessages;
import com.tpe.cinetime.constants.messages.SuccessMessages;
import com.tpe.cinetime.entity.Role;
import com.tpe.cinetime.entity.User;
import com.tpe.cinetime.enums.RoleName;
import com.tpe.cinetime.exception.BadRequestException;
import com.tpe.cinetime.payload.mapper.UserMapper;
import com.tpe.cinetime.payload.request.authentication.*;
import com.tpe.cinetime.payload.response.authentication.LoginResponseDTO;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.payload.response.authentication.RefreshTokenResponseDTO;
import com.tpe.cinetime.payload.response.user.UserResponseDTO;
import com.tpe.cinetime.repository.user.RoleRepository;
import com.tpe.cinetime.repository.user.UserRepository;
import com.tpe.cinetime.security.JwtUtils;
import com.tpe.cinetime.security.UserDetailsImpl;
import com.tpe.cinetime.service.email.EmailService;
import com.tpe.cinetime.service.helpers.MethodHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


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
    private final EmailService emailService;
    private final MethodHelper methodHelper;


    @Value("${app.reset.token.expiration.time}")
    private int resetPasswordTokenExpirationTime;


    public ResponseMessage<UserResponseDTO> register(RegisterRequestDTO registerRequestDTO) {

        log.info("Inside register method");
        //check if email is already registered
        if(userRepository.existsByEmail(registerRequestDTO.getEmail())){
            throw new BadRequestException(ErrorMessages.EMAIL_ALREADY_EXISTS);
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
                .message(SuccessMessages.USER_REGISTERED_SUCCESSFULLY)
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
        //Access token oluşturulur
        String accessToken = jwtUtil.generateAccessToken(authentication);

        //Refresh token üret ve Redis'e kaydet
        String refreshToken = jwtUtil.generateRefreshToken(authentication);

        //Authentication principal bilgisini kendi UserDetailsImpl objesine donusturuyoruz
        //Bu obje User bilgilerini ve rollerini içermektedir
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        //UserDetailsImpl objesini LoginResponseDTO'ya donusturuyoruz
        LoginResponseDTO loginResponseDTO = userMapper.mapUserDetailsImplToLoginResponseDTO(userDetails,
                accessToken,
                refreshToken
        );

        //Standart ResponseMessage yapisi ile login response donuyoruz
        return ResponseMessage.<LoginResponseDTO>builder()
                .object(loginResponseDTO)
                .message(SuccessMessages.USER_LOGIN_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();

    }

    public ResponseMessage<RefreshTokenResponseDTO> refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {

        log.info("Inside refresh token method");

        String refreshToken = refreshTokenRequestDTO.getRefreshToken();

        //Get email from refresh token
        String email = jwtUtil.getEmailFromRefreshToken(refreshToken);

        //DB'den user bulma
        User user = methodHelper.findByEmailOrThrow(email);

        //Get user id
        Long userId = user.getId();

        //

        //Redis'teki refreshToken ile karsilastir
        boolean isRefreshTokenValid = jwtUtil.isRefreshTokenValid(userId, refreshToken);
        if (!isRefreshTokenValid) {
                throw new BadRequestException(ErrorMessages.INVALID_OR_EXPIRED_REFRESH_TOKEN);
        }

        //Build user details
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());

        //New access token generate
        String newAccessToken = jwtUtil.generateAccessToken(authentication);

        RefreshTokenResponseDTO refreshTokenResponseDTO = userMapper.mapToRefreshTokenResponseDTO(
                newAccessToken,
                refreshToken);

        return ResponseMessage.<RefreshTokenResponseDTO>builder()
                .object(refreshTokenResponseDTO)
                .message(SuccessMessages.USER_REFRESH_TOKEN_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    //Redis'ten refresh token sil
    public ResponseMessage<Void> logout(Authentication authentication) {

        log.info("Inside logout method");

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        jwtUtil.deleteRefreshToken(userDetails.getId());
        SecurityContextHolder.clearContext();

        return ResponseMessage.<Void>builder()
                .message(SuccessMessages.USER_LOGOUT_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ResponseMessage<String> forgotPassword( ForgotPasswordRequestDTO forgotPasswordRequestDTO) {

        log.info("Inside forgotPassword method");

        //Güvenlik: email var mi yok mu kontrol ediyoruz
        String genericMessage = "If the email exists, password reset instructions have been sent.";

        Optional<User> userOptional = userRepository.findByEmail(forgotPasswordRequestDTO.getEmail());

        if (userOptional.isEmpty()){
            //kullanici yok ama yine de 200 OK döndür (user enumeration engellendi)
            return ResponseMessage.<String>builder()
                    .object(null)
                    .message(genericMessage)
                    .httpStatus(HttpStatus.OK)
                    .build();
        }

        User user = userOptional.get();

        //UUID ile reset token üret
        String resetPasswordToken = UUID.randomUUID().toString();

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime resetPasswordTokenExpireDate = now.plusMinutes(resetPasswordTokenExpirationTime);

        //Set reset token and expire date
        user.setResetPasswordToken(resetPasswordToken);
        user.setResetPasswordTokenExpireDate(resetPasswordTokenExpireDate);
        userRepository.save(user);

        //mail gönder
        emailService.sendPasswordResetEmail(user.getEmail(), resetPasswordToken);

        return ResponseMessage.<String >builder()
                .object(null)
                .message(genericMessage)
                .httpStatus(HttpStatus.OK)
                .build();
    }


    @Transactional
    public ResponseMessage<String> resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) {

        log.info("Inside resetPassword method");

        String newPassword = resetPasswordRequestDTO.getNewPassword();
        String confirmPassword = resetPasswordRequestDTO.getConfirmPassword();
        String resetPasswordToken = resetPasswordRequestDTO.getResetPasswordToken();
        LocalDateTime now = LocalDateTime.now();


        //newPassword ve confirmPassword ayni mi
        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException(ErrorMessages.PASSWORD_NOT_MATCH);
        }

        //Token ile user'i bul
        User user = methodHelper.findByResetTokenOrThrow(resetPasswordToken);

        LocalDateTime resetPasswordTokenExpireDate = user.getResetPasswordTokenExpireDate();

        //Token süresi kontrol etme
        if (resetPasswordTokenExpireDate == null ||
                now.isAfter(resetPasswordTokenExpireDate)){

            throw new BadRequestException(ErrorMessages.PASSWORD_RESET_TOKEN_EXPIRED);
        }

        //New password with BCrypt hash and  save
        user.setPassword(passwordEncoder.encode(newPassword));

        //Token bilgilerini temizle(tek kullanimlik)
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpireDate(null);

        userRepository.save(user);

        //mail gönder
        emailService.sendPasswordChangedEmail(user.getEmail());

        return ResponseMessage.<String>builder()
                .object(null)
                .message(SuccessMessages.PASSWORD_RESET_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

}
