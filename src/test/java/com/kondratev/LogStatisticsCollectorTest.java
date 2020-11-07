package com.kondratev;

import com.kondratev.classifier.LogClassifiers;
import com.kondratev.logparser.SplittingLogParser;
import com.kondratev.model.LogEntry;
import com.kondratev.model.LogLevel;
import com.kondratev.reader.InputStreamReader;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class LogStatisticsCollectorTest {

    private List<LogEntry> LOGS = Arrays.asList(LogEntry.builder().date(LocalDateTime.now()).level(LogLevel.INFO).message("Info").build(),
                                                LogEntry.builder().date(LocalDateTime.now()).level(LogLevel.ERROR).message("Error").build(),
                                                LogEntry.builder().date(LocalDateTime.now()).level(LogLevel.WARN).message("Warning").build());

    @DataProvider(parallel = true)
    public Object[][] logLevels() {
        return new Object[][]{
                { createInputStreamFromLogList(LOGS), Stream.of(LogLevel.INFO).collect(Collectors.toCollection(HashSet::new)), 1 },
                { createInputStreamFromLogList(LOGS), Stream.of(LogLevel.ERROR).collect(Collectors.toCollection(HashSet::new)), 1 },
                { createInputStreamFromLogList(LOGS), Stream.of(LogLevel.WARN).collect(Collectors.toCollection(HashSet::new)), 1 },
                { createInputStreamFromLogList(LOGS), Stream.of(LogLevel.INFO, LogLevel.ERROR, LogLevel.WARN).collect(Collectors.toCollection(HashSet::new)), 3 }
        };
    }

    @Test(dataProvider = "logLevels")
    public void testCollectByLogLevel(InputStream inputStream, Set<LogLevel> levels, int expectedSize) throws Exception {
        LogStatisticsCollector collector = new LogStatisticsCollector(new SplittingLogParser(), new InputStreamReader());

        Map<LogLevel, Long> collectedLogs = collector.collect(inputStream, LogClassifiers.BY_LEVEL, logEntry -> levels.contains(logEntry.getLevel()));
        assertThat(collectedLogs.size(), equalTo(expectedSize));
    }

    private InputStream createInputStreamFromLogList(List<LogEntry> logs) {
        return new ByteArrayInputStream(logs.stream()
                                            .map(LogEntry::toString)
                                            .collect(Collectors.joining("\n"))
                                            .getBytes());
    }
}