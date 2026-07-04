package com.tpe.cinetime.service.user;

import com.tpe.cinetime.constants.messages.ErrorMessages;
import com.tpe.cinetime.constants.messages.SuccessMessages;
import com.tpe.cinetime.entity.User;
import com.tpe.cinetime.exception.BadRequestException;
import com.tpe.cinetime.payload.mapper.UserMapper;
import com.tpe.cinetime.payload.request.user.UserUpdatePasswordRequestDTO;
import com.tpe.cinetime.payload.request.user.UserUpdateWithoutPasswordRequestDTO;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.payload.response.user.UserResponseDTO;
import com.tpe.cinetime.repository.user.RoleRepository;
import com.tpe.cinetime.repository.user.UserRepository;
import com.tpe.cinetime.service.email.EmailService;
import com.tpe.cinetime.service.helpers.MethodHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final MethodHelper methodHelper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public ResponseMessage<UserResponseDTO> getAccountDetails() {

        log.info("Inside getAccountDetails()");

        User user = methodHelper.currentUser();

        UserResponseDTO userResponseDTO = userMapper.mapUserToUserResponseDTO(user);

        return ResponseMessage.<UserResponseDTO>builder()
                .object(userResponseDTO)
                .message(SuccessMessages.ACCOUNT_DETAILS_RETRIEVED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }


    @Transactional
    public ResponseMessage<UserResponseDTO> updateAccountDetails(
            UserUpdateWithoutPasswordRequestDTO userUpdateWithPasswordRequestDTO) {

        log.info("Inside updateAccountDetails()");

        User user = methodHelper.currentUser();

        methodHelper.UpdateUserFields(user, userUpdateWithPasswordRequestDTO);

        userRepository.save(user);

        UserResponseDTO userResponseDTO = userMapper.mapUserToUserResponseDTO(user);

        return ResponseMessage.<UserResponseDTO>builder()
                .object(userResponseDTO)
                .message(SuccessMessages.USER_UPDATED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ResponseMessage<?> updatePassword(UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO) {

        User user = methodHelper.currentUser();

        String currentPassword = userUpdatePasswordRequestDTO.getCurrentPassword();
        String newPassword = userUpdatePasswordRequestDTO.getNewPassword();
        String confirmPassword = userUpdatePasswordRequestDTO.getConfirmPassword();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())){
            throw new BadRequestException(ErrorMessages.CURRENT_PASSWORD_IS_INCORRECT);
        }

        if (!newPassword.equals(confirmPassword)){
            throw new BadRequestException(ErrorMessages.NEW_PASSWORD_AND_CONFIRM_PASSWORD_DO_NOT_MATCH);
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())){
            throw new BadRequestException(ErrorMessages.NEW_PASSWORD_CANNOT_BE_SAME_AS_CURRENT_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        emailService.sendPasswordChangedEmail(user.getEmail());

        return ResponseMessage.builder()
                .message(SuccessMessages.PASSWORD_UPDATED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ResponseMessage<?> deleteAccount() {

        User user = methodHelper.currentUser();

        userRepository.delete(user);

        return ResponseMessage.builder()
                .message(SuccessMessages.ACCOUNT_DELETED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }
}
