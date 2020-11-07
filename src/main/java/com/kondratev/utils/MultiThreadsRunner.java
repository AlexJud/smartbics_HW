package com.kondratev.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Builder
public class MultiThreadsRunner<I, O> {
    private Function<I, O> task;
    private Stream<I> inputs;
    private Integer numberOfThreads = 10;

    public List<O> run() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        List<O> output;
        try {
            log.info("Running tasks in {} threads", numberOfThreads);

            List<Callable<O>> callables = inputs.map(input -> new CallableWrapper(task, input))
                                                      .collect(Collectors.toList());

            output = executor.invokeAll(callables)
                             .parallelStream()
                             .map(this::handleException)
                             .filter(Objects::nonNull)
                             .collect(Collectors.toList());

        } finally {
            log.info("Shutting down...");
            executor.shutdown();
            log.info("Awaiting termination...");
            executor.awaitTermination(1L, TimeUnit.HOURS);
        }
        return output;
    }

    private O handleException(Future<O> future){
        try {
            return future.get();
        } catch (Exception e) {
            log.warn("Got exception: {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @AllArgsConstructor
    private class CallableWrapper implements Callable<O> {
        private Function<I, O> function;
        private I input;

        @Override
        public O call() {
            return function.apply(input);
        }
    }
}

