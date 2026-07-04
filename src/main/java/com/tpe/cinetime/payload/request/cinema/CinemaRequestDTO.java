package com.tpe.cinetime.payload.request.cinema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder




public class CinemaRequestDTO {

   @NotBlank(message = "Cinema name cannot be blank")
   @Size(max = 100, message = "Cinema name must be at most 100 characters")
   private String name;

   @NotBlank(message = "City cannot be blank")
   @Size(max = 80, message = "City must be at most 80 characters")
   private String city;

   @NotBlank(message = "District cannot be blank")
   @Size(max = 80, message = "District must be at most 80 characters")
   private String district;

   @NotBlank(message = "Address cannot be blank")
   @Size(max = 250, message = "Address must be at most 250 characters")
   private String address;

   @NotBlank(message = "Phone cannot be blank")
   @Pattern(regexp = "^\\+?[0-9() \\-]{10,20}$", message = "Phone format is invalid")
   private String phone;

   @DecimalMin(value = "-90.0", message = "Latitude must be at least -90")
   @DecimalMax(value = "90.0", message = "Latitude must be at most 90")
   private Double latitude;

   @DecimalMin(value = "-180.0", message = "Longitude must be at least -180")
   @DecimalMax(value = "180.0", message = "Longitude must be at most 180")
   private Double longitude;
}
