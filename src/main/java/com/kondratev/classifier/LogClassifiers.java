package com.kondratev.classifier;

import com.kondratev.model.LogEntry;
import com.kondratev.model.LogLevel;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class LogClassifiers {
    public static LogClassifier<LocalDateTime> BY_HOUR = createLocalDateTimeLogClassifierBy(ChronoUnit.HOURS);
    public static LogClassifier<LocalDateTime> BY_MINUTES = createLocalDateTimeLogClassifierBy(ChronoUnit.MINUTES);

    public static LogClassifier<LogLevel> BY_LEVEL = () -> LogEntry::getLevel;

    private static LogClassifier<LocalDateTime> createLocalDateTimeLogClassifierBy(ChronoUnit timeUnit) {
        return () -> logEntry -> logEntry.getDate().truncatedTo(timeUnit);
    }
}
