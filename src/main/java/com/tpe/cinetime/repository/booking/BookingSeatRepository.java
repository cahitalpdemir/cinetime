package com.tpe.cinetime.repository.booking;

import com.tpe.cinetime.entity.BookingSeat;
import com.tpe.cinetime.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

    @Query("SELECT bs.seat.id FROM BookingSeat bs " +
            "WHERE bs.showtime.id = :showtimeId " +
            "AND bs.booking.status IN :statuses")
    Set<Long> findBookedSeatIdsByShowtimeId(
            @Param("showtimeId") Long showtimeId,
            @Param("statuses") List<BookingStatus> statuses);

    long countByShowtimeIdAndBooking_StatusIn(Long showtimeId, List<BookingStatus> statuses);
}
