package com.kondratev.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Utils {
    public static String getOrEmptyString(Object o) {
        return o == null ? "" : o.toString();
    }

    public static void saveToFile(String path, String str) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
