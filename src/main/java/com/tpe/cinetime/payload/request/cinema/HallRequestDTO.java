package com.tpe.cinetime.payload.request.cinema;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.tpe.cinetime.enums.HallType;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class HallRequestDTO {

   private String name;
   private HallType hallType;
   private Integer rows;
   private Integer seatsPerRow;
   private Long cinemaId;

}
