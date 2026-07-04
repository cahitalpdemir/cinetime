package com.tpe.cinetime.enums;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpe.cinetime.payload.response.MovieResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MovieStatusSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void statusIsSerializedAsReadableString() throws Exception {
        MovieResponse response = MovieResponse.builder()
                .status(MovieStatus.COMING_SOON)
                .build();

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("\"status\":\"COMING_SOON\"");
    }
}
