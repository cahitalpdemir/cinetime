package com.tpe.cinetime.payload;

import com.tpe.cinetime.payload.request.MovieRequest;
import com.tpe.cinetime.payload.request.cinema.HallRequestDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.assertj.core.api.Assertions.assertThat;

class RequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void movieRejectsNonPositiveDurationAndOutOfRangeRating() {
        MovieRequest request = new MovieRequest();
        request.setDuration(0);
        request.setRating(11.0);

        assertThat(validator.validate(request))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("duration", "rating");
    }

    @Test
    void hallRejectsMoreThanTwentySixRows() {
        HallRequestDTO request = HallRequestDTO.builder()
                .name("Hall 1")
                .rows(27)
                .seatsPerRow(10)
                .cinemaId(1L)
                .build();

        assertThat(validator.validate(request))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("rows");
    }
}
