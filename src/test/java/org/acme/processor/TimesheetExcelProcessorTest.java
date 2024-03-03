package org.acme.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.time.YearMonth;

@Disabled
public class TimesheetExcelProcessorTest {

    private TimesheetExcelProcessor timesheetExcelProcessor;

    @BeforeEach
    void setUp() {
        timesheetExcelProcessor = new TimesheetExcelProcessor();
    }

    @Test
    void testGenerateTimeSheet() throws Exception {
        var result = timesheetExcelProcessor.generateTimeSheet(YearMonth.parse("2024-01"));
        try (FileOutputStream fos = new FileOutputStream("Timesheet.xlsx")) {
            fos.write(((ByteArrayOutputStream) result).toByteArray());
        }

    }


}
