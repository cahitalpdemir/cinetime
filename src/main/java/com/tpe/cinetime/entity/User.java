package com.tpe.cinetime.entity;

import com.tpe.cinetime.enums.Gender;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

import java.util.Date;


@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 25)
    private String surname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    //LocalDate kullanilirsa @Temporal(TemporalType.DATE) gerek kalmaz.
    private Date birthDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="role_id", nullable = false)
    private Role role;

    /*
    @OneToMany(mappedBy = "user")
    private List<Booking> bookings;
    */


    @Builder.Default
    private Boolean builtIn = false; // @Builder.Default bu degeri kullanir.

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    //Entity ilk kez veritabanına kaydedilmeden önce çalışır.
    //createdAt ve updatedAt alanlarını doldurur
    //ve null olabilecek varsayılan alanları güvenli hale getirir.
    @PrePersist
    private void onCreate(){
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if(this.builtIn == null){
            this.builtIn = false;
        }

    }

    @Column(nullable = false)
    private LocalDateTime updatedAt ;

    //Güncelleme öncesi updatedAt alanını yeniler
    @PreUpdate
    private void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    @Column(nullable = true, name = "reset_password_token")
    private String resetPasswordToken;

    @Column(nullable = true, name = "reset_password_token_expire_date")
    private LocalDateTime resetPasswordTokenExpireDate;



}
