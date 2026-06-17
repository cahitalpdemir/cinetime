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
import javax.persistence.UniqueConstraint;


import com.tpe.cinetime.enums.SeatType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
   name="seats",
   uniqueConstraints = {
      @UniqueConstraint(
         name = "uk_seat_hall_row_number",
         columnNames = {"hall_id", "rowLetter", "seatNumber"}
      )
   }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder


public class Seat {
     @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Long id;

    @Column(nullable=false, length=2)
   private String rowLetter; 

   @Column(nullable=false)
   private Integer seatNumber; 

   @Column(nullable=false)
   @Enumerated(EnumType.STRING)
   private SeatType seatType;

   @ManyToOne
   @JoinColumn(name="hall_id",nullable=false)
   private Hall hall; 


}
