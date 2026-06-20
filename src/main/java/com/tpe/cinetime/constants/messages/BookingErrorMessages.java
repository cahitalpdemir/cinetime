package com.tpe.cinetime.constants.messages;

public final class BookingErrorMessages {

    private BookingErrorMessages() {
    }

    public static final String BOOKING_NOT_FOUND = "Booking not found. Id: %d";
    public static final String BOOKING_ACCESS_DENIED = "You do not have access to this booking";
    public static final String BOOKING_ALREADY_CANCELLED = "This booking is already cancelled";
    public static final String BOOKING_ALREADY_CONFIRMED = "This booking is already confirmed";
    public static final String BOOKING_CANNOT_CANCEL_CONFIRMED = "Confirmed bookings cannot be cancelled";
    public static final String BOOKING_SEATS_EMPTY = "At least one seat must be selected";
    public static final String BOOKING_SEAT_NOT_FOUND = "Seat not found. Seat id: %d";
    public static final String BOOKING_SEAT_NOT_IN_HALL = "Seat does not belong to the showtime hall";
    public static final String BOOKING_SEAT_ALREADY_TAKEN = "One or more selected seats are already booked";
    public static final String BOOKING_SHOWTIME_IN_PAST = "Cannot book a showtime that has already started";
    public static final String PAYMENT_NOT_FOUND = "Payment not found for this booking";
    public static final String PAYMENT_ALREADY_COMPLETED = "Payment has already been completed for this booking";
    public static final String PAYMENT_FAILED = "Payment processing failed. Please try again";
    public static final String TICKET_NOT_FOUND = "Ticket not found. Ticket number: %s";
    public static final String TICKET_ACCESS_DENIED = "You do not have access to this ticket";
}
