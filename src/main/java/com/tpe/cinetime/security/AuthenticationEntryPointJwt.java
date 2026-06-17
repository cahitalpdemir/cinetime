package com.tpe.cinetime.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class AuthenticationEntryPointJwt implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {

        log.error("Unauthorized error: {}", authenticationException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ResponseMessage<Object> responseMessage = ResponseMessage.builder()
                .object(null)
                .message("Unauthorized access. Please login first.")
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .build();

        //ObjectMapper →  responseMessage'i  JSON'a cevirip direkt response'a yazarız
        objectMapper.writeValue(response.getOutputStream(), responseMessage);
    }
}
