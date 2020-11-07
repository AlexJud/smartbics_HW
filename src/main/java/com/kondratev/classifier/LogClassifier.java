package com.kondratev.classifier;

import com.kondratev.model.LogEntry;

import java.util.function.Function;

@FunctionalInterface
public interface LogClassifier<T> {
    Function<LogEntry, T> classify();
}
