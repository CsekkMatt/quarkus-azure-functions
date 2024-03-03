package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import org.acme.processor.TimesheetExcelProcessor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.time.YearMonth;

@ApplicationScoped
@Log4j2
public class InvoiceService {

    public static final String DEFAULT_REPORT_NAME = "Mate-Petok-Timesheet-%s.xlsx";

    @Inject
    TimesheetExcelProcessor timesheetExcelProcessor;

    @Inject
    BlobService blobService;

    public String generateTimeSheetForMonth(String month) {
        try {
            Instant now = Instant.now();
            YearMonth yearMonth = YearMonth.parse(month);
            OutputStream timesheetReport = timesheetExcelProcessor.generateTimeSheet(yearMonth);
            log.info("Generated excel in {} ms", Instant.now().toEpochMilli() - now.toEpochMilli());
            return uploadToBlob(timesheetReport, month);
        } catch (Exception e) {
            log.error("Error generating invoice", e);
        }
        return "Error generating invoice";
    }

    private String uploadToBlob(OutputStream timesheetReport, String month) {
        byte[] bytes = ((ByteArrayOutputStream) timesheetReport).toByteArray();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String fileName = String.format(DEFAULT_REPORT_NAME, month);
        blobService.uploadToBlob(fileName, inputStream);
        return blobService.getDownloadLinkFromBlob(fileName);

    }

}
