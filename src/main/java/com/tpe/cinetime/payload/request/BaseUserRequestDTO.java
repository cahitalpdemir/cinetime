package com.tpe.cinetime.payload.request;

import com.tpe.cinetime.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder //Kalıtımda @Builder çalışmaz, parent field'larını da builder'a
//dahil etmek için @SuperBuilder kullanılmalı.
public class BaseUserRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters")
    private String name;

    @NotBlank(message = "Surname is required")
    @Size(min = 3, max = 25, message = "Surname must be between 3 and 25 characters")
    private String surname;


    @NotBlank(message = "Email is required")
    @Email(message = "Email must be in a valid format")
    private String email;

    @NotNull(message = "Phone number is required")
    @Pattern(
            regexp = "^\\(\\d{3}\\) \\d{3}-\\d{4}$",
            message = "Phone number must be in format: (XXX) XXX-XXXX"
    )
    private String phoneNumber;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private Date birthDate;

    @NotNull(message = "Gender is required")
    private Gender gender;

}
