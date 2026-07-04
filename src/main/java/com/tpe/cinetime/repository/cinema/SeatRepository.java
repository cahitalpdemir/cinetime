package com.tpe.cinetime.repository.cinema;


import com.tpe.cinetime.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    //returns all seat belonging to a hall
    List<Seat> findByHallId(Long hallId);
}

