package com.tpe.cinetime.controller.admin;

import com.tpe.cinetime.payload.request.admin.AdminUserUpdateRequestDTO;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.payload.response.user.UserResponseDTO;
import com.tpe.cinetime.service.admin.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<ResponseMessage<Page<UserResponseDTO>>> getAllUsersByPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") Sort.Direction order) {

        return ResponseEntity.ok(adminService.getAllUsersByPage(page, size, sortBy, order));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<ResponseMessage<UserResponseDTO>> getUserById(@PathVariable("id") Long userId) {

        return ResponseEntity.ok(adminService.getUserById(userId));
    }

    @PatchMapping("/user/{id}")
    public ResponseEntity<ResponseMessage<UserResponseDTO>> updateUserById(
            @PathVariable("id") Long userId,
            @Valid
            @RequestBody
            AdminUserUpdateRequestDTO adminUserUpdateRequestDTO){

        return ResponseEntity.ok(adminService.updateUserById(userId, adminUserUpdateRequestDTO));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<ResponseMessage<?>> deleteUserById(@PathVariable("id") Long userId) {

        return ResponseEntity.ok(adminService.deleteUserById(userId));
    }

    //TODO: /admin/users/{id}/activitiy -> get user activity
}
