package com.tpe.cinetime.repository.cinema;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tpe.cinetime.entity.Cinema;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {

    boolean existsByPhone(String phone);

}
