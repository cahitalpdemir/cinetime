package com.tpe.cinetime.constants.messages;

public final class BookingValidationMessages {

    private BookingValidationMessages() {
    }

    public static final String SHOWTIME_ID_NOT_NULL = "Showtime id cannot be null";
    public static final String SEAT_IDS_NOT_EMPTY = "At least one seat id is required";
    public static final String PAYMENT_METHOD_NOT_NULL = "Payment method cannot be null";
    public static final String CARD_NUMBER_NOT_BLANK = "Card number cannot be blank";
    public static final String CARD_HOLDER_NOT_BLANK = "Card holder name cannot be blank";
    public static final String CARD_NUMBER_INVALID = "Card number must be 16 digits";
}
