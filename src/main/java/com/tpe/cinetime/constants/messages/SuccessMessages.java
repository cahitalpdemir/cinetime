package com.tpe.cinetime.constants.messages;

public final class SuccessMessages {

    private SuccessMessages() {
    }

    public static final String USER_REGISTERED_SUCCESSFULLY = "User registered successfully";
    public static final String USER_LOGIN_SUCCESSFULLY = "User logged in successfully";
    public static final String USER_REFRESH_TOKEN_SUCCESSFULLY = "User refresh token successfully generated";
    public static final String USER_LOGOUT_SUCCESSFULLY = "User logged out successfully";
    public static final String PASSWORD_RESET_SUCCESSFULLY = "Password reset successfully";
    public static final String ACCOUNT_DETAILS_RETRIEVED_SUCCESSFULLY = "Account details retrieved successfully";
    public static final String USER_UPDATED_SUCCESSFULLY = "User updated successfully";
    public static final String PASSWORD_UPDATED_SUCCESSFULLY = "Password updated successfully";
    public static final String ACCOUNT_DELETED_SUCCESSFULLY = "Account deleted successfully";

    //Admin and Manager
    public static final String USERS_FETCHED_SUCCESSFULLY = "Users fetched successfully";
    public static final String USER_FETCHED_SUCCESSFULLY = "User fetched successfully. User id: %d";
    public static final String ADMIN_UPDATED_USER_SUCCESSFULLY = "User updated successfully. User id: %d";
    public static final String ADMIN_DELETED_USER_SUCCESSFULLY = "User deleted successfully.";

    //Email

    //Cinema
    public static final String CINEMA_SAVED_SUCCESSFULLY = "Cinema saved successfully";
    public static final String CINEMA_FETCHED_SUCCESSFULLY = "Cinema fetched successfully";
    public static final String CINEMAS_FETCHED_SUCCESSFULLY = "Cinemas fetched successfully";

    //Hall
    public static final String HALL_SAVED_SUCCESSFULLY = "Hall saved successfully";
    public static final String HALLS_FETCHED_SUCCESSFULLY = "Halls fetched successfully";

    // Movie
    public static final String MOVIES_FETCHED_SUCCESSFULLY = "Movies fetched successfully";
    public static final String MOVIE_FETCHED_SUCCESSFULLY = "Movie fetched successfully";
    public static final String MOVIE_CREATED_SUCCESSFULLY = "Movie created successfully";
    public static final String MOVIE_UPDATED_SUCCESSFULLY = "Movie updated successfully";
    public static final String MOVIE_DELETED_SUCCESSFULLY = "Movie deleted successfully";
    public static final String MOVIE_SHOWTIMES_FETCHED_SUCCESSFULLY = "Movie showtimes fetched successfully";

    //Showtime
    public static final String SHOWTIME_CREATED_SUCCESSFULLY = "Showtime created successfully";
    public static final String SHOWTIME_CANCELLED_SUCCESSFULLY = "Showtime cancelled successfully";
    public static final String SHOWTIMES_FETCHED_SUCCESSFULLY = "Showtimes fetched successfully";
    public static final String SHOWTIME_SEATS_FETCHED_SUCCESSFULLY = "Showtime seats fetched successfully";

    //Seat Lock
    public static final String SEATS_LOCKED_SUCCESSFULLY = "Seats locked successfully";
    public static final String SEAT_LOCK_RELEASED_SUCCESSFULLY = "Seat lock released successfully";
}
