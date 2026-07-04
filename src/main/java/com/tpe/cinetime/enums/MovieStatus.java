package com.tpe.cinetime.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum MovieStatus {
    NOW_SHOWING,
    COMING_SOON,
    ARCHIVED
}
