package com.tpe.cinetime.payload.request.authentication;

import com.tpe.cinetime.constants.messages.ValidationMessages;
import com.tpe.cinetime.payload.request.BaseUserRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder //Kalıtımda @Builder çalışmaz, parent field'larını da builder'a
//dahil etmek için @SuperBuilder kullanılmalı.
public class RegisterRequestDTO extends BaseUserRequestDTO {

    @NotBlank(message = ValidationMessages.PASSWORD_NOT_BLANK)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&_#]{8,}$",
            message = ValidationMessages.PASSWORD_PATTERN
    )
    private String password;

}

