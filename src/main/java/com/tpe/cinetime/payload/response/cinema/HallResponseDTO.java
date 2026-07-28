package com.tpe.cinetime.payload.response.cinema;

import com.tpe.cinetime.enums.HallType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HallResponseDTO {
   private Long id;
   private String name;
   private HallType hallType;
   private Long cinemaId;
   private Integer capacity;
   private Integer createdSeatCount;
}
