package com.tpe.cinetime.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.tpe.cinetime.enums.HallType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="halls")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder


public class Hall {
   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Long id;

   @Column(nullable=false, length=25)
   private String name;

   @Column(nullable=false, length=20)
   @Enumerated(EnumType.STRING)
   private HallType hallType;


   @Column(nullable=false)
   private Integer rows;

   @Column(nullable=false)
   private Integer seatsPerRow;

   @ManyToOne
   @JoinColumn(name="cinema_id", nullable=false)
   private Cinema cinema;


   
}








