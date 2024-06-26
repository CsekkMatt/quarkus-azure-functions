package org.acme.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import jakarta.inject.Inject;
import org.acme.service.InvoiceService;

import java.time.Instant;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class InvoiceFunction {

    @Inject
    InvoiceService invoiceService;

    @FunctionName("GetInvoiceSampleForMonth")
    public HttpResponseMessage getInvoiceSampleCurrentMonth(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.GET }, route = "invoice", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Generate invoice for month");
        String month = request.getQueryParameters().get("month");
        Instant now = Instant.now();
        if (!isValidMonth(month)) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Invalid month format. Please use yyyy-MM").build();
        }
        String timesheetUrl = invoiceService.generateTimeSheetForMonth(month);
        context.getLogger()
                .info(String.format("Request processed in %d ms", Instant.now().toEpochMilli() - now.toEpochMilli()));
        return request.createResponseBuilder(HttpStatus.OK).body(timesheetUrl).build();
    }

    @FunctionName("TestIfMonthIsValid")
    public HttpResponseMessage testIfMonthIsValid(@HttpTrigger(name = "req", methods = {
            HttpMethod.GET }, route = "test", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Test if month is valid");
        String month = request.getQueryParameters().get("month");
        if (!isValidMonth(month)) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Invalid month format. Please use yyyy-MM").build();
        }
        return request.createResponseBuilder(HttpStatus.OK).body("Month is valid").build();
    }

    @FunctionName("CacheInvoicesUntilDate")
    public HttpResponseMessage cacheInvoicesUntilDate(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.POST }, route = "cache", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Cache invoices until date");

        String month = request.getQueryParameters().get("month");
        Instant now = Instant.now();
        if (!isValidMonth(month)) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Invalid date format. Please use yyyy-MM")
                    .build();
        }
        invoiceService.cacheInvoicesUntilDate(month);
        context.getLogger()
                .info(String.format("Request processed in %d ms", Instant.now().toEpochMilli() - now.toEpochMilli()));
        return request.createResponseBuilder(HttpStatus.OK).body("Invoices cached until " + month).build();
    }

    private boolean isValidMonth(String month) {
        if (month == null || month.isEmpty()) {
            return false;
        }
        if (!canBeParsed(month)) {
            return false;
        }
        return true;
    }

    private boolean canBeParsed(String month) {
        try {
            YearMonth.parse(month);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }

    }

}
