package com.tpe.cinetime.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) //Include only non-null fields in JSON
public class ResponseMessage<T> {

    private T object;
    private String message;
    private HttpStatus httpStatus;

}
