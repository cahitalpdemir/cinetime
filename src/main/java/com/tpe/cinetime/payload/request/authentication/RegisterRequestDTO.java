package com.tpe.cinetime.payload.request.authentication;

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

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&_#]{8,}$",
            message = "Password must be at least 8 characters and contain at least one uppercase letter, " +
                    "one lowercase letter, and one digit"
    )
    private String password;

}

