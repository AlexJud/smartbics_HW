package com.kondratev.logparser;

import com.kondratev.model.LogEntry;
import com.kondratev.model.LogLevel;
import com.kondratev.exception.ParsingException;
import com.kondratev.exception.WrongLogFormatException;

import java.time.LocalDateTime;

public class SplittingLogParser implements LogParser {
    @Override
    public LogEntry parse(String logLine) throws ParsingException {
        String[] splittedLog = logLine.split(";");
        if (splittedLog.length != 3) {
            throw new WrongLogFormatException("Log line can't be parsed by splitting");
        }
        return LogEntry.builder()
                       .date(LocalDateTime.parse(splittedLog[0]))
                       .level(LogLevel.create(splittedLog[1]))
                       .message(splittedLog[2])
                       .build();
    }
}
