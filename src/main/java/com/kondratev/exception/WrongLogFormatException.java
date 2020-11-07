package com.kondratev.exception;

public class WrongLogFormatException extends ParsingException {
    public WrongLogFormatException(String message) {
        super(message);
    }
}
