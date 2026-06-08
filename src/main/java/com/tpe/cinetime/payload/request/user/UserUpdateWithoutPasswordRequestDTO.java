package com.tpe.cinetime.payload.request.user;

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
@SuperBuilder
public class UserUpdateWithoutPasswordRequestDTO {

    @Size(min = 3, max = 20, message = ValidationMessages.NAME_SIZE)
    private String name;

    @Size(min = 2, max = 25, message = ValidationMessages.SURNAME_SIZE)
    private String surname;


    @Email(message = ValidationMessages.EMAIL_NOT_VALID)
    private String email;


    @Pattern(
            regexp = "^\\(\\d{3}\\) \\d{3}-\\d{4}$",
            message = ValidationMessages.PHONE_NUMBER_NOT_VALID
    )
    private String phoneNumber;

    @Past(message = ValidationMessages.BIRTHDAY_NOT_PAST)
    private Date birthDate;

    private Gender gender;
}
