package com.kondratev.model;

import com.kondratev.utils.Utils;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogEntry {
    private LocalDateTime date;
    private LogLevel level;
    private String message;

    @Override
    public String toString() {
        return String.format("%s;%s;%s", Utils.getOrEmptyString(date), level, Utils.getOrEmptyString(message));
    }


}
