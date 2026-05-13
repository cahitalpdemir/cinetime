package com.tpe.cinetime.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuperBuilder//Kalıtımda @Builder çalışmaz, parent field'larını da builder'a
//dahil etmek için @SuperBuilder kullanılmalı.
public class UserResponseDTO extends BaseUserResponseDTO{


    private String phoneNumber;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date birthDate;
    private String gender;
}
