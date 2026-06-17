package com.tpe.cinetime.service.helpers;

import com.tpe.cinetime.constants.messages.ErrorMessages;
import com.tpe.cinetime.entity.Role;
import com.tpe.cinetime.entity.User;
import com.tpe.cinetime.enums.Gender;
import com.tpe.cinetime.enums.RoleName;
import com.tpe.cinetime.exception.BadRequestException;
import com.tpe.cinetime.exception.NotFoundException;
import com.tpe.cinetime.payload.request.admin.AdminUserUpdateRequestDTO;
import com.tpe.cinetime.payload.request.user.UserUpdateWithoutPasswordRequestDTO;
import com.tpe.cinetime.repository.user.RoleRepository;
import com.tpe.cinetime.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class MethodHelper {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public User currentUser(){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND));
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.USER_NOT_FOUND_BY_ID, userId)));
    }

    public User findByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorMessages.USER_NOT_FOUND));
    }

    public User findByResetTokenOrThrow(String token) {
        return userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new BadRequestException(ErrorMessages.PASSWORD_RESET_TOKEN_INVALID_OR_EXPIRED));
    }

    public void UpdateUserFields(
            User user,
            UserUpdateWithoutPasswordRequestDTO userUpdateWithoutPasswordRequestDTO){

        String newName = userUpdateWithoutPasswordRequestDTO.getName();
        String newSurname = userUpdateWithoutPasswordRequestDTO.getSurname();
        String newPhoneNumber = userUpdateWithoutPasswordRequestDTO.getPhoneNumber();
        Date newBirthDate = userUpdateWithoutPasswordRequestDTO.getBirthDate();
        Gender newGender = userUpdateWithoutPasswordRequestDTO.getGender();
        String newEmail = userUpdateWithoutPasswordRequestDTO.getEmail();

        if (newName !=null){
            user.setName(newName);
        }

        if (newSurname !=null){
            user.setSurname(newSurname);
        }

        if (newPhoneNumber !=null){
            user.setPhoneNumber(newPhoneNumber);
        }

        if (newBirthDate !=null){
            user.setBirthDate(newBirthDate);
        }

        if (newGender !=null){
            user.setGender(newGender);
        }

        if (newEmail !=null && !newEmail.equals(user.getEmail())){
            if (userRepository.existsByEmail(newEmail)) {
                throw new BadRequestException(ErrorMessages.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(newEmail);
        }
    }


    public void AdminUpdateUserFields(
            User user,
            AdminUserUpdateRequestDTO adminUserUpdateRequestDTO,
            User currentUser) {

        UpdateUserFields(user, adminUserUpdateRequestDTO);

        RoleName newRoleName = adminUserUpdateRequestDTO.getRole();

        System.out.println("newRoleName: " + newRoleName);
        System.out.println("currentUser role: " + currentUser.getRole().getRoleName());


        if (newRoleName == null){
            return;
        }

        System.out.println("newRoleName: " + newRoleName);
        System.out.println("currentUser role: " + currentUser.getRole().getRoleName());


        if (!currentUser.getRole().getRoleName().equals(RoleName.ADMIN)) {
            throw new BadRequestException(ErrorMessages.ONLY_ADMIN_CAN_UPDATE_ROLE);
        }

        Role newRole = roleRepository.findByRoleName(newRoleName)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ROLE_NOT_FOUND));

        user.setRole(newRole);

    }




}
