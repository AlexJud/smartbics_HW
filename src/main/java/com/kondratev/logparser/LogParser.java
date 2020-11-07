package com.kondratev.logparser;

import com.kondratev.model.LogEntry;
import com.kondratev.exception.ParsingException;

public interface LogParser {
    LogEntry parse(String logLine) throws ParsingException;
}
