package com.kondratev.logparser;

import com.kondratev.exception.ParsingException;
import com.kondratev.model.LogEntry;
import com.kondratev.model.LogLevel;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SplittingLogParserTest {

    @DataProvider(parallel = true)
    public Object[][] wrongFormatLogs() {
        return new Object[][]{
                { "Wrong format" },
                { "2020-03-20T15:24:24.357;DEFAULT;111111" },
        };
    }


    @Test(dataProvider = "wrongFormatLogs", expectedExceptions = ParsingException.class)
    public void brokenLogStringTest(String logString) throws ParsingException {
        SplittingLogParser parser = new SplittingLogParser();
        parser.parse(logString);
    }

    @Test
    public void parseLogLevel() throws ParsingException {
        SplittingLogParser parser = new SplittingLogParser();
        LogEntry logEntry = parser.parse("2020-03-20T15:24:24.357;INFO;111111");

        assertThat(logEntry.getLevel(), equalTo(LogLevel.INFO));
    }
}