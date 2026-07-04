package com.tpe.cinetime.service.booking;

import com.tpe.cinetime.constants.messages.BookingErrorMessages;
import com.tpe.cinetime.constants.messages.BookingSuccessMessages;
import com.tpe.cinetime.entity.Booking;
import com.tpe.cinetime.entity.BookingSeat;
import com.tpe.cinetime.entity.Ticket;
import com.tpe.cinetime.entity.User;
import com.tpe.cinetime.enums.BookingStatus;
import com.tpe.cinetime.enums.TicketStatus;
import com.tpe.cinetime.exception.BadRequestException;
import com.tpe.cinetime.exception.NotFoundException;
import com.tpe.cinetime.payload.mapper.BookingMapper;
import com.tpe.cinetime.payload.response.booking.TicketResponse;
import com.tpe.cinetime.payload.response.booking.TicketVerificationResponse;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.repository.booking.TicketRepository;
import com.tpe.cinetime.service.helpers.MethodHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;
    private final MethodHelper methodHelper;
    private final TicketQrService ticketQrService;

    @Transactional
    public void generateTicketsForBooking(Booking booking) {
        for (BookingSeat bookingSeat : booking.getBookingSeats()) {
            String ticketNumber = generateTicketNumber();

            Ticket ticket = Ticket.builder()
                    .booking(booking)
                    .seat(bookingSeat.getSeat())
                    .ticketNumber(ticketNumber)
                    .qrCode(ticketQrService.generateQrCode(
                            ticketNumber, booking, bookingSeat.getSeat()))
                    .status(TicketStatus.ACTIVE)
                    .build();

            booking.getTickets().add(ticket);
        }

        ticketRepository.saveAll(booking.getTickets());
    }

    @Transactional
    public ResponseMessage<List<TicketResponse>> getTicketsByBookingId(Long bookingId) {
        Booking booking = bookingService.getBookingForCurrentUser(bookingId);

        List<TicketResponse> tickets = ticketRepository.findByBookingId(booking.getId())
                .stream()
                .map(bookingMapper::toTicketResponse)
                .collect(Collectors.toList());

        return ResponseMessage.<List<TicketResponse>>builder()
                .object(tickets)
                .message(BookingSuccessMessages.TICKETS_FETCHED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ResponseMessage<TicketResponse> getTicketByNumber(String ticketNumber) {
        User user = methodHelper.currentUser();

        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new NotFoundException(
                        String.format(BookingErrorMessages.TICKET_NOT_FOUND, ticketNumber)));

        if (!ticket.getBooking().getUser().getId().equals(user.getId())) {
            throw new NotFoundException(
                    String.format(BookingErrorMessages.TICKET_NOT_FOUND, ticketNumber));
        }

        return ResponseMessage.<TicketResponse>builder()
                .object(bookingMapper.toTicketResponse(ticket))
                .message(BookingSuccessMessages.TICKET_FETCHED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ResponseMessage<TicketVerificationResponse> verifyTicket(String qrCode) {
        Ticket ticket = findAndValidateTicket(qrCode);

        return verificationResponse(ticket, BookingSuccessMessages.TICKET_VERIFIED_SUCCESSFULLY);
    }

    @Transactional
    public ResponseMessage<TicketVerificationResponse> checkInTicket(String qrCode) {
        Ticket ticket = findAndValidateTicket(qrCode);
        ticket.setStatus(TicketStatus.USED);
        ticketRepository.save(ticket);

        return verificationResponse(ticket, BookingSuccessMessages.TICKET_CHECKED_IN_SUCCESSFULLY);
    }

    private Ticket findAndValidateTicket(String qrCode) {
        try {
            Claims claims = ticketQrService.parseAndVerify(qrCode);
            Ticket ticket = ticketRepository.findByTicketNumber(claims.getSubject())
                    .orElseThrow(() -> new BadRequestException(
                            BookingErrorMessages.TICKET_QR_INVALID));

            validateQrClaims(ticket, claims, qrCode);
            return ticket;
        } catch (JwtException | IllegalArgumentException exception) {
            throw new BadRequestException(BookingErrorMessages.TICKET_QR_INVALID);
        }
    }

    private ResponseMessage<TicketVerificationResponse> verificationResponse(
            Ticket ticket,
            String message) {
        return ResponseMessage.<TicketVerificationResponse>builder()
                .object(toVerificationResponse(ticket))
                .message(message)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    private void validateQrClaims(Ticket ticket, Claims claims, String qrCode) {
        Booking booking = ticket.getBooking();
        if (!ticket.getQrCode().equals(qrCode)
                || ticket.getStatus() != TicketStatus.ACTIVE
                || booking.getStatus() != BookingStatus.CONFIRMED
                || !claimMatches(claims, "bookingId", booking.getId())
                || !claimMatches(claims, "showtimeId", booking.getShowtime().getId())
                || !claimMatches(claims, "seatId", ticket.getSeat().getId())) {
            throw new BadRequestException(BookingErrorMessages.TICKET_QR_INVALID);
        }
    }

    private boolean claimMatches(Claims claims, String claim, Long expected) {
        Object value = claims.get(claim);
        return value instanceof Number && ((Number) value).longValue() == expected;
    }

    private TicketVerificationResponse toVerificationResponse(Ticket ticket) {
        Booking booking = ticket.getBooking();
        return TicketVerificationResponse.builder()
                .valid(true)
                .ticketNumber(ticket.getTicketNumber())
                .status(ticket.getStatus())
                .movieTitle(booking.getShowtime().getMovie().getTitle())
                .cinemaName(booking.getShowtime().getHall().getCinema().getName())
                .hallName(booking.getShowtime().getHall().getName())
                .showtimeDate(booking.getShowtime().getDate())
                .startTime(booking.getShowtime().getStartTime())
                .rowLetter(ticket.getSeat().getRowLetter())
                .seatNumber(ticket.getSeat().getSeatNumber())
                .build();
    }

    private String generateTicketNumber() {
        return "TKT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
