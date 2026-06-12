package com.tpe.cinetime.constants.messages;

public final class ErrorMessages {

    private ErrorMessages() {
    }

    //User
    public static final String USER_NOT_FOUND = "User not found";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String USER_NOT_FOUND_BY_ID = "User not found by id. User id: %d";

    //Token
    public static final String INVALID_OR_EXPIRED_REFRESH_TOKEN = "Refresh token is invalid or expired";
    public static final String PASSWORD_RESET_TOKEN_INVALID_OR_EXPIRED = "Invalid or expired reset password token";
    public static final String PASSWORD_RESET_TOKEN_EXPIRED = "Password reset token has expired. Please request a new one.";

    //Password
    public static final String CURRENT_PASSWORD_IS_INCORRECT = "Current password is incorrect";
    public static final String PASSWORD_NOT_MATCH = "Passwords do not match";
    public static final String NEW_PASSWORD_AND_CONFIRM_PASSWORD_DO_NOT_MATCH = "New password and confirm password do not match";
    public static final String NEW_PASSWORD_CANNOT_BE_SAME_AS_CURRENT_PASSWORD = "New password cannot be same as current password";

    //Admin
    public static final String ONLY_ADMIN_CAN_UPDATE_ROLE = "Only admins can update user role";
    public static final String MANAGER_CANNOT_UPDATE = "Managers can update only customer users";
    public static final String CANNOT_UPDATE_BUILT_IN_USER = "Built-in users can not be updated";
    public static final String ROLE_NOT_FOUND = "Role not found";
    public static final String CANNOT_DELETE_BUILT_IN_USER = "Built-in users can not be deleted";
    public static final String MANAGER_CANNOT_DELETE = "Managers can delete only customer users";

    //Cinema
    public static final String CINEMA_NOT_FOUND = "Cinema not found. Cinema id: %d";
    public static final String CINEMA_PHONE_ALREADY_EXISTS = "A cinema with this phone number already exists";

    //Hall
    public static final String CINEMA_NOT_FOUND_FOR_HALL = "Cinema not found for hall. Cinema id: %d";

    //Email
    public static final String EMAIL_SEND_FAILED = "Password reset email could not be sent to: ";



}
