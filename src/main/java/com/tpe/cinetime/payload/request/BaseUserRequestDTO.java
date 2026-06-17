package com.tpe.cinetime.payload.request;

import com.tpe.cinetime.constants.messages.ValidationMessages;
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

    @NotBlank(message = ValidationMessages.NAME_NOT_BLANK)
    @Size(min = 3, max = 20, message = ValidationMessages.NAME_SIZE)
    private String name;

    @NotBlank(message =ValidationMessages.SURNAME_NOT_BLANK)
    @Size(min = 2, max = 25, message = ValidationMessages.SURNAME_SIZE)
    private String surname;


    @NotBlank(message = ValidationMessages.EMAIL_NOT_BLANK)
    @Email(message = ValidationMessages.EMAIL_NOT_VALID)
    private String email;

    @NotNull(message = ValidationMessages.PHONE_NUMBER_NOT_BLANK)
    @Pattern(
            regexp = "^\\(\\d{3}\\) \\d{3}-\\d{4}$",
            message = ValidationMessages.PHONE_NUMBER_NOT_VALID
    )
    private String phoneNumber;

    @NotNull(message = ValidationMessages.BIRTHDAY_NOT_BLANK)
    @Past(message = ValidationMessages.BIRTHDAY_NOT_PAST)
    private Date birthDate;

    @NotNull(message = ValidationMessages.GENDER_NOT_BLANK)
    private Gender gender;

}
