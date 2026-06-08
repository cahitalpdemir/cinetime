package com.tpe.cinetime.constants.messages;

public final class ValidationMessages {

    private  ValidationMessages() {
    }

    //Name / Surname
    public static final String NAME_NOT_BLANK = "Name cannot be blank";
    public static final String SURNAME_NOT_BLANK = "Surname cannot be blank";
    public static final String NAME_SIZE = "Name must be between 3 and 20 characters";
    public static final String SURNAME_SIZE = "Surname must be between 2 and 25 characters";

    //Email
    public static final String EMAIL_NOT_BLANK = "Email cannot be blank";
    public static final String EMAIL_NOT_VALID = "Please provide a valid email address";

    //Password
    public static final String PASSWORD_NOT_BLANK = "Password cannot be blank";
    public static final String PASSWORD_PATTERN =
            "Password must be at least 8 characters and contain at least one uppercase letter, " +
                    "one lowercase letter, and one digit";

    public static final String CURRENT_PASSWORD_NOT_BLANK = "Current password cannot be blank";
    public static final String NEW_PASSWORD_NOT_BLANK = "New password cannot be blank";
    public static final String CONFIRM_PASSWORD_NOT_BLANK = "Confirm password cannot be blank";

    //Phone
    public static final String PHONE_NUMBER_NOT_BLANK = "Phone number cannot be blank";
    public static final String PHONE_NUMBER_NOT_VALID = "Phone number must be in format: (XXX) XXX-XXXX";

    //Birthday
    public static final String BIRTHDAY_NOT_BLANK = "Birthday cannot be blank";
    public static final String BIRTHDAY_NOT_PAST = "Birth date must be in the past";

    //Gender
    public static final String GENDER_NOT_BLANK = "Gender cannot be blank";


    //Reset Password Token
    public static final String RESET_PASSWORD_TOKEN_NOT_BLANK = "Reset password token cannot be blank";

    //Refresh Token
    public static final String REFRESH_TOKEN_NOT_BLANK = "Refresh token cannot be blank";



}
