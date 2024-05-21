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
import java.util.List;

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
            if (isPresentInBlob(month, blobService.getAllBlobFileNames(BlobService.TIMESHEET_CONTAINER))) {
                log.info("Excel already present in blob storage" + String.format(DEFAULT_REPORT_NAME, month));
                return blobService.getDownloadLinkFromBlob(String.format(DEFAULT_REPORT_NAME, month));
            }
            YearMonth yearMonth = YearMonth.parse(month);
            OutputStream timesheetReport = timesheetExcelProcessor.generateTimeSheet(yearMonth);
            log.info("Generated excel in {} ms", Instant.now().toEpochMilli() - now.toEpochMilli());
            return uploadToBlob(timesheetReport, month);
        } catch (Exception e) {
            log.error("Error generating invoice", e);
        }
        return "Error generating invoice";
    }

    public void cacheInvoicesUntilDate(String month) {
        try {
            Instant now = Instant.now();
            YearMonth currentMonth = YearMonth.now();
            YearMonth inputMonth = YearMonth.parse(month);
            YearMonth tempMonth = currentMonth;
            List<String> blobFileNames = blobService.getAllBlobFileNames(BlobService.TIMESHEET_CONTAINER);
            log.info("Got blob names in " + (Instant.now().toEpochMilli() - now.toEpochMilli()) + " ms");
            while (tempMonth.isBefore(inputMonth) || tempMonth.equals(inputMonth)) {
                if (isPresentInBlob(tempMonth.toString(), blobFileNames)) {
                    log.debug("Excel already present in blob storage" + String.format(DEFAULT_REPORT_NAME, tempMonth));
                    tempMonth = tempMonth.plusMonths(1);
                    continue;
                }
                OutputStream timesheetReport = timesheetExcelProcessor.generateTimeSheet(tempMonth);
                uploadToBlob(timesheetReport, tempMonth.toString());
                tempMonth = tempMonth.plusMonths(1);
                log.info("Generated excel in {} ms", Instant.now().toEpochMilli() - now.toEpochMilli());
            }
        } catch (Exception e) {
            log.error("Error generating invoice", e);
        }
    }

    private boolean isPresentInBlob(String month, List<String> blobFileNames) {
        return blobFileNames.stream()
                .anyMatch(fileName -> fileName.equals(String.format(DEFAULT_REPORT_NAME, month)));
    }

    private String uploadToBlob(OutputStream timesheetReport, String month) {
        byte[] bytes = ((ByteArrayOutputStream) timesheetReport).toByteArray();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String fileName = String.format(DEFAULT_REPORT_NAME, month);
        blobService.uploadToBlob(fileName, inputStream);
        return blobService.getDownloadLinkFromBlob(fileName);

    }

}
