package com.tpe.cinetime.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpe.cinetime.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder //Kalıtımda @Builder çalışmaz, parent field'larını da builder'a
//dahil etmek için @SuperBuilder kullanılmalı.
public class BaseUserResponseDTO {

    private Long id;
    private String name;
    private String surname;
    private String email;
    private String role;
}
