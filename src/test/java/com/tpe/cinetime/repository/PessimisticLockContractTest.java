package com.tpe.cinetime.repository;

import com.tpe.cinetime.repository.booking.BookingRepository;
import com.tpe.cinetime.repository.showtime.ShowtimeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PessimisticLockContractTest {

    @Test
    void showtimeReservationUsesDatabaseWriteLock() throws Exception {
        Method method = ShowtimeRepository.class.getMethod("findByIdForUpdate", Long.class);
        assertEquals(LockModeType.PESSIMISTIC_WRITE, method.getAnnotation(Lock.class).value());
    }

    @Test
    void bookingPaymentAndCancellationUseDatabaseWriteLock() throws Exception {
        Method method = BookingRepository.class.getMethod(
                "findByIdAndUserIdForUpdate", Long.class, Long.class);
        assertEquals(LockModeType.PESSIMISTIC_WRITE, method.getAnnotation(Lock.class).value());
    }
}
