package com.tpe.cinetime.payload.request.admin;

import com.tpe.cinetime.enums.RoleName;
import com.tpe.cinetime.payload.request.user.UserUpdateWithoutPasswordRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AdminUserUpdateRequestDTO extends UserUpdateWithoutPasswordRequestDTO {

    private RoleName role;
}
