package com.tpe.cinetime.payload.request.cinema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder




public class CinemaRequestDTO {
    
  
   private String name;
   private String city;
   private String district;
   private String address;
   private String phone;
    private Double latitude;   
   private Double longitude;
}
