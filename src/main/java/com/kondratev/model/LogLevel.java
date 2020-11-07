package com.kondratev.model;

import com.kondratev.exception.ParsingException;

import java.util.Arrays;

public enum LogLevel {
    ERROR("ERROR"),
    WARN("WARN"),
    INFO("INFO");

    private String value;

    LogLevel(String value) {
        this.value = value;
    }

    public static LogLevel create(String value) throws ParsingException {
        return Arrays.stream(LogLevel.values())
                     .filter(v -> v.value.equalsIgnoreCase(value))
                     .findFirst()
                     .orElseThrow(() -> new ParsingException(String.format("Can't create LogLevel from value %s", value)));
    }
}
