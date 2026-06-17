package com.tpe.cinetime.payload.response.authentication;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.tpe.cinetime.payload.response.user.BaseUserResponseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO extends BaseUserResponseDTO {

    private String accessToken;

    private String refreshToken;

    @Builder.Default
    private String tokenType = "Bearer";

}
