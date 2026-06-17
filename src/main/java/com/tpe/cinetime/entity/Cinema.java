package com.tpe.cinetime.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="cinemas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder




public class Cinema {
   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Long id;

   @Column(nullable=false, length=100)
   private String name;

   @Column(nullable=false, length=50)
   private String city;

   @Column(nullable=false, length=50)
   private String district;

   @Column(nullable=false, length=255)
   private String address;

   @Column(nullable=false, unique=true)
   private String phone;

   @Column(nullable=false, length=20)
    private Double latitude;   

   @Column(nullable=false, length=20)
   private Double longitude;
    
}

