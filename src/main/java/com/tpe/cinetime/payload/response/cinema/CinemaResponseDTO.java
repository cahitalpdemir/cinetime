package com.tpe.cinetime.payload.response.cinema;

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
public class CinemaResponseDTO {
   private Long id;
   private String name;
   private String city;
   private String district;
   private String address;
   private String phone;
   private Double latitude;   
   private Double longitude;
    

    
}