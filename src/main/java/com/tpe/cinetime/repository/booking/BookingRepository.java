package com.tpe.cinetime.repository.booking;

import com.tpe.cinetime.entity.Booking;
import com.tpe.cinetime.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Booking> findByIdAndUserId(Long id, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "javax.persistence.lock.timeout", value = "5000"))
    @Query("SELECT b FROM Booking b WHERE b.id = :id AND b.user.id = :userId")
    Optional<Booking> findByIdAndUserIdForUpdate(
            @Param("id") Long id,
            @Param("userId") Long userId);

    List<Booking> findByShowtimeIdAndStatusIn(Long showtimeId, List<BookingStatus> statuses);

    // YENİ EKLENEN — belirli bir süreden eski, hâlâ PENDING olan booking'leri bulur
    @Query("SELECT b FROM Booking b WHERE b.status = :status AND b.createdAt < :cutoffTime")
    List<Booking> findByStatusAndCreatedAtBefore(
            @Param("status") BookingStatus status,
            @Param("cutoffTime") LocalDateTime cutoffTime);
}
