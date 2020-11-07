package com.kondratev;

import com.kondratev.classifier.LogClassifiers;
import com.kondratev.logparser.SplittingLogParser;
import com.kondratev.model.LogLevel;
import com.kondratev.reader.InputStreamReader;
import com.kondratev.utils.MultiThreadsRunner;
import com.kondratev.utils.Utils;
import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ApplicationMain {

    @Argument(alias = "f", description = "Log files", delimiter = ",")
    private static String[] inputLogFiles;
    @Argument(alias = "tn", description = "Thread numbers")
    private static Integer numberOfThreads = 2;

    private static void init(String[] args) {
        try {
            Args.parse(ApplicationMain.class, args);
        } catch (IllegalArgumentException e) {
            Args.usage(ApplicationMain.class);
            System.exit(1);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        init(args);
        Stream<FileInputStream> inputLogFilesStreams = Stream.of(inputLogFiles)
                                                             .map(file -> {
                                                                 try {
                                                                     return new FileInputStream(file);
                                                                 } catch (FileNotFoundException e) {
                                                                     log.error("Can't read file {}", file);
                                                                     return null;
                                                                 }
                                                             })
                                                             .filter(obj -> !Objects.isNull(obj));

        // Collect log statistics from files
        List<Map<LocalDateTime, Long>> results = MultiThreadsRunner.<FileInputStream, Map<LocalDateTime, Long>>builder()
                .inputs(inputLogFilesStreams)
                .numberOfThreads(numberOfThreads)
                .task(fileInputStream -> {
                    LogStatisticsCollector collector = new LogStatisticsCollector(new SplittingLogParser(), new InputStreamReader());
                    try {
                        return collector.collect(fileInputStream, LogClassifiers.BY_HOUR, logEntry -> logEntry.getLevel()
                                                                                                              .equals(LogLevel.ERROR));
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        return Collections.emptyMap();
                    }
                })
                .build()
                .run();

        // Merge all results to one map
        Map<LocalDateTime, Long> resultMap = new HashMap<>();
        results.forEach(result -> {
            result.forEach((key, val) -> {
                if (resultMap.putIfAbsent(key, val) != null) {
                    Long oldValue = resultMap.get(key);
                    resultMap.replace(key, oldValue, oldValue + val);
                }
            });
        });

        String result = resultMap.entrySet()
                                  .stream()
                                  .map(statisticEntry -> String.format("%s, Количество ошибок: %d", timeIntervalFormatter(statisticEntry.getKey()), statisticEntry.getValue()))
                                  .collect(Collectors.joining("\n"));

        Utils.saveToFile("./log_statistics.log", result);
    }

    private static String timeIntervalFormatter(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:00-");
        String interval = time.format(formatter);
        int hour = time.getHour();
        return interval + ((hour + 1) + ".00");
    }
}
