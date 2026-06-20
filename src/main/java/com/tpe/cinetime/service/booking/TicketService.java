package com.tpe.cinetime.service.booking;

import com.tpe.cinetime.constants.messages.BookingErrorMessages;
import com.tpe.cinetime.constants.messages.BookingSuccessMessages;
import com.tpe.cinetime.entity.Booking;
import com.tpe.cinetime.entity.BookingSeat;
import com.tpe.cinetime.entity.Ticket;
import com.tpe.cinetime.entity.User;
import com.tpe.cinetime.exception.NotFoundException;
import com.tpe.cinetime.payload.mapper.BookingMapper;
import com.tpe.cinetime.payload.response.booking.TicketResponse;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.repository.booking.TicketRepository;
import com.tpe.cinetime.service.helpers.MethodHelper;
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

    @Transactional
    public void generateTicketsForBooking(Booking booking) {
        for (BookingSeat bookingSeat : booking.getBookingSeats()) {
            String ticketNumber = generateTicketNumber();

            Ticket ticket = Ticket.builder()
                    .booking(booking)
                    .seat(bookingSeat.getSeat())
                    .ticketNumber(ticketNumber)
                    .qrCode(ticketNumber)
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

    private String generateTicketNumber() {
        return "TKT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
