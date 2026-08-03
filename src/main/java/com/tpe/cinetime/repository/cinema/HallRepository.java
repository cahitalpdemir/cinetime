package com.tpe.cinetime.repository.cinema;

import com.tpe.cinetime.entity.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HallRepository extends JpaRepository<Hall, Long> {

    List<Hall> findByCinema_IdOrderByNameAsc(Long cinemaId);

    boolean existsByCinema_Id(Long cinemaId);
}