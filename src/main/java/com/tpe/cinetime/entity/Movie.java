package com.tpe.cinetime.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 3, max = 100)
    @Column(nullable = false, length = 100)
    private String title;

    @NotNull
    @Size(min = 5, max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String slug;

    @NotNull
    @Size(min = 3, max = 300)
    @Column(nullable = false, length = 300)
    private String summary;

    @NotNull
    @Column(nullable = false)
    private LocalDate releaseDate;

    @NotNull
    @Column(nullable = false)
    private Integer duration;

    private Double rating;

    @NotNull
    @Column(nullable = false)
    private String director;

    @ElementCollection
    @CollectionTable(name = "movie_cast", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "actor")
    @Builder.Default
    private List<String> cast = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "movie_formats", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "format")
    @Builder.Default
    private List<String> formats = new ArrayList<>();

    @NotNull
    @Column(nullable = false)
    private String genre;

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "poster_id")
//    private Image poster;

    @Column(nullable = false)
    @Builder.Default
    private Integer status = 0;

    private String specialHalls;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
