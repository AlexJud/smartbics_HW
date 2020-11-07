package com.kondratev.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.function.Consumer;

public class InputStreamReader implements StreamReader {
    @Override
    public void readLines(InputStream inputStream, Consumer<String> lineHandler) throws IOException {
        try (Scanner scanner = new Scanner(inputStream, "UTF-8");) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineHandler.accept(line);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
