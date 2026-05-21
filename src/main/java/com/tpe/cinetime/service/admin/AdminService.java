package com.tpe.cinetime.service.admin;

import com.tpe.cinetime.constants.messages.ErrorMessages;
import com.tpe.cinetime.constants.messages.SuccessMessages;
import com.tpe.cinetime.entity.User;
import com.tpe.cinetime.enums.RoleName;
import com.tpe.cinetime.exception.BadRequestException;
import com.tpe.cinetime.payload.mapper.UserMapper;
import com.tpe.cinetime.payload.request.admin.AdminUserUpdateRequestDTO;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.payload.response.user.UserResponseDTO;
import com.tpe.cinetime.repository.user.UserRepository;
import com.tpe.cinetime.service.helpers.MethodHelper;
import com.tpe.cinetime.service.helpers.PageableHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PageableHelper pageableHelper;
    private final UserMapper userMapper;
    private final MethodHelper methodHelper;

    public ResponseMessage<Page<UserResponseDTO>> getAllUsersByPage(
            int page,
            int size,
            String sortBy,
            Sort.Direction order) {

        Pageable pageable = pageableHelper.getPageableWithProps(page, size, sortBy, order);

        Page<User> users = userRepository.findAll(pageable);

        Page<UserResponseDTO> userResponseDTOPage = users.map(userMapper::mapUserToUserResponseDTO);

        return ResponseMessage.<Page<UserResponseDTO>>builder()
                .object(userResponseDTOPage)
                .message(SuccessMessages.USERS_FETCHED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public ResponseMessage<UserResponseDTO> getUserById(Long userId) {

        User user = methodHelper.getUserById(userId);

        UserResponseDTO userResponseDTO = userMapper.mapUserToUserResponseDTO(user);

        return ResponseMessage.<UserResponseDTO>builder()
                .object(userResponseDTO)
                .message(String.format(SuccessMessages.USER_FETCHED_SUCCESSFULLY, userId))
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public ResponseMessage<UserResponseDTO> updateUserById(
            Long userId,AdminUserUpdateRequestDTO adminUserUpdateRequestDTO) {

        User user = methodHelper.getUserById(userId);
        User currentUser = methodHelper.currentUser();

        if (Boolean.TRUE.equals(user.getBuiltIn())){
            throw new BadRequestException(ErrorMessages.CANNOT_UPDATE_BUILT_IN_USER);
        }

        if (currentUser.getRole().getRoleName().equals(RoleName.MANAGER)
                && !user.getRole().getRoleName().equals(RoleName.CUSTOMER)){
            throw new BadRequestException(ErrorMessages.MANAGER_CANNOT_UPDATE);
        }

        methodHelper.AdminUpdateUserFields(user, adminUserUpdateRequestDTO, currentUser);

        User updatedUser = userRepository.save(user);

        return ResponseMessage.<UserResponseDTO>builder()
                .object(userMapper.mapUserToUserResponseDTO(updatedUser))
                .message(String.format(SuccessMessages.ADMIN_UPDATED_USER_SUCCESSFULLY, userId))
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public ResponseMessage<?> deleteUserById(Long userId) {

        User user = methodHelper.getUserById(userId);
        User currentUser = methodHelper.currentUser();

        if (Boolean.TRUE.equals(user.getBuiltIn())){
            throw new BadRequestException(ErrorMessages.CANNOT_DELETE_BUILT_IN_USER);
        }

        if (currentUser.getRole().getRoleName().equals(RoleName.MANAGER)
                && !user.getRole().getRoleName().equals(RoleName.CUSTOMER)){
            throw new BadRequestException(ErrorMessages.MANAGER_CANNOT_DELETE);
        }

        userRepository.delete(user);

        return ResponseMessage.<UserResponseDTO>builder()
                .object(null)
                .message(String.format(SuccessMessages.ADMIN_DELETED_USER_SUCCESSFULLY, userId))
                .httpStatus(HttpStatus.OK)
                .build();
    }
}
