package com.kondratev.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public interface StreamReader {
    void readLines(InputStream inputStream, Consumer<String> lineHandler) throws IOException;
}
