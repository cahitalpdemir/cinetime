package com.tpe.cinetime.payload.request.cinema;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.tpe.cinetime.enums.HallType;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class HallRequestDTO {

   @NotBlank(message = "Hall name cannot be blank")
   @Size(max = 100, message = "Hall name must be at most 100 characters")
   private String name;

   @NotNull(message = "Hall type cannot be null")
   private HallType hallType;

   @NotNull(message = "Rows cannot be null")
   @Min(value = 1, message = "Rows must be at least 1")
   @Max(value = 26, message = "Rows must be at most 26")
   private Integer rows;

   @NotNull(message = "Seats per row cannot be null")
   @Min(value = 1, message = "Seats per row must be at least 1")
   @Max(value = 100, message = "Seats per row must be at most 100")
   private Integer seatsPerRow;

   @NotNull(message = "Cinema id cannot be null")
   private Long cinemaId;

}
