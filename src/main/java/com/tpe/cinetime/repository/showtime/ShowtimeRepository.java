package com.tpe.cinetime.repository.showtime;

import com.tpe.cinetime.entity.Showtime;
import com.tpe.cinetime.enums.ShowtimeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "javax.persistence.lock.timeout", value = "5000"))
    @Query("SELECT s FROM Showtime s WHERE s.id = :id")
    Optional<Showtime> findByIdForUpdate(@Param("id") Long id);

    // Derived queries avoid PostgreSQL null-parameter type errors from (:param IS NULL OR ...) JPQL patterns.
    List<Showtime> findByStatus(ShowtimeStatus status);

    List<Showtime> findByMovieIdAndStatus(Long movieId, ShowtimeStatus status);

    List<Showtime> findByHallIdAndStatus(Long hallId, ShowtimeStatus status);

    boolean existsByHallId(Long hallId);

    List<Showtime> findByDateAndStatus(LocalDate date, ShowtimeStatus status);

    List<Showtime> findByMovieIdAndHallIdAndStatus(Long movieId, Long hallId, ShowtimeStatus status);

    List<Showtime> findByMovieIdAndDateAndStatus(Long movieId, LocalDate date, ShowtimeStatus status);

    List<Showtime> findByHallIdAndDateAndStatus(Long hallId, LocalDate date, ShowtimeStatus status);

    List<Showtime> findByMovieIdAndHallIdAndDateAndStatus(
            Long movieId,
            Long hallId,
            LocalDate date,
            ShowtimeStatus status
    );

    // checks if hall is already booked at given date and time
    @Query("SELECT COUNT(s) > 0 FROM Showtime s WHERE " +
            "s.hall.id = :hallId AND " +
            "s.date = :date AND " +
            "s.status = 'ACTIVE' AND " +
            "s.startTime < :endTime AND " +
            "s.endTime > :startTime")
    boolean existsConflictingShowtime(
            @Param("hallId") Long hallId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}
