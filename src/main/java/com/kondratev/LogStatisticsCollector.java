package com.kondratev;

import com.kondratev.classifier.LogClassifier;
import com.kondratev.exception.ParsingException;
import com.kondratev.logparser.LogParser;
import com.kondratev.model.LogEntry;
import com.kondratev.reader.InputStreamReader;
import com.kondratev.reader.StreamReader;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@Slf4j
public class LogStatisticsCollector {

    private LogParser logParser;
    private StreamReader reader;

    public LogStatisticsCollector(LogParser logParser, InputStreamReader reader) {
        this.logParser = logParser;
        this.reader = reader;
    }

    public <T> Map<T, Long> collect(InputStream inputStream, LogClassifier<T> logClassifier, Predicate<LogEntry> filter)
            throws Exception {
        Map<T, Long> results = new HashMap<>();
        reader.readLines(inputStream, s -> {
            try {
                LogEntry logEntry = logParser.parse(s);
                if (filter.test(logEntry)) {
                    T classifiedLog = logClassifier.classify().apply(logEntry);
                    if (results.putIfAbsent(classifiedLog, 1L) != null) {
                        Long oldValue = results.get(classifiedLog);
                        results.replace(classifiedLog, oldValue, oldValue + 1L);
                    }
                }
            } catch (ParsingException e) {
                // Ignore log which can't be parsed
                log.error(e.getMessage());
            }

        });
        return results;
    }
}
