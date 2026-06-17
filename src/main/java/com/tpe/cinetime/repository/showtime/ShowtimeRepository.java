package com.tpe.cinetime.repository.showtime;

import com.tpe.cinetime.entity.Showtime;
import com.tpe.cinetime.enums.ShowtimeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    // filter by optional params/null values are ignored
    @Query("SELECT s FROM Showtime s WHERE " +
            "(:movieId IS NULL OR s.movie.id = :movieId) AND " +
            "(:hallId IS NULL OR s.hall.id = :hallId) AND " +
            "(:date IS NULL OR s.date = :date) AND " +
            "s.status = 'ACTIVE'")
    List<Showtime> findByFilters(
            @Param("movieId") Long movieId,
            @Param("hallId") Long hallId,
            @Param("date") LocalDate date
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

    List<Showtime> findByMovieIdAndStatus(Long movieId, ShowtimeStatus status);
}
