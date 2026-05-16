package com.tpe.cinetime.repository;


import com.tpe.cinetime.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<com.tpe.cinetime.entity.Movie, Long> {

    Optional<Movie> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<Movie> findByStatus(Integer status, Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE " +
            "LOWER(m.title) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(m.summary) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Movie> search(@Param("q") String query, Pageable pageable);

//    @Query("SELECT m FROM Movie m WHERE m.specialHalls LIKE CONCAT('%', :hall, '%')")
//    Page<Movie> findBySpecialHall(@Param("hall") String hall, Pageable pageable);
}
