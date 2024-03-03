package org.acme.processor;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.YearMonth;

@ApplicationScoped
public class TimesheetExcelProcessor {

    public static final String SHEET_NAME = "Timesheet";


    public OutputStream generateTimeSheet(YearMonth yearMonth) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(SHEET_NAME);
        // Set column widths
        this.configureColumnWidth(sheet);
        CellStyle tableCellStyle = tableCellStyle(workbook);
        CellStyle greyStyle = greyStyle(workbook);
        // Create first row
        Row row1 = sheet.createRow(0);
        createCellWithStyle(row1, TimesheetExcelStructure.MONTH.getIndex(), "Month", greyStyle);
        createCellWithStyle(row1, TimesheetExcelStructure.HOURS.getIndex(), yearMonth.toString(), greyStyle);
        createCellWithStyle(row1, TimesheetExcelStructure.TASK.getIndex(), "", greyStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 2));

        // Create second row
        Row row2 = sheet.createRow(1);
        createCellWithStyle(row2, TimesheetExcelStructure.MONTH.getIndex(), "Day", tableCellStyle);
        createCellWithStyle(row2, TimesheetExcelStructure.HOURS.getIndex(), "Hours", tableCellStyle);
        createCellWithStyle(row2, TimesheetExcelStructure.TASK.getIndex(), "Task", tableCellStyle);

        // Create the rest of the rows
        int daysInMonth = yearMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            CellStyle currentStyle = day % 2 == 0 ? greyStyle : tableCellStyle; // Use greyStyle for even rows
            Row row = sheet.createRow(day + 1);
            //Days cell
            createCellWithStyle(row, TimesheetExcelStructure.MONTH.getIndex(), String.valueOf(day), currentStyle);
            //Hours cell
            createCellWithStyle(row, TimesheetExcelStructure.HOURS.getIndex(), "", currentStyle);
            //Task cell
            createCellWithStyle(row, TimesheetExcelStructure.TASK.getIndex(), "", currentStyle);
        }
        Row totalRow = sheet.createRow(daysInMonth + 2);
        createCellWithStyle(totalRow, TimesheetExcelStructure.TOTAL_HOURS.getIndex(), "Total hours:", tableCellStyle);
        Cell totalHoursCell = totalRow.createCell(TimesheetExcelStructure.TOTAL_HOURS_FORMULA.getIndex());
        totalHoursCell.setCellFormula("SUM(B3:B" + (daysInMonth + 2) + ")");
        totalHoursCell.setCellStyle(tableCellStyle);
        try {
            ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
            workbook.write(fileOut);
            fileOut.close();
            return fileOut;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void configureColumnWidth(Sheet sheet) {
        sheet.setColumnWidth(0, 15 * 256);
        sheet.setColumnWidth(1, 11 * 256);
        sheet.setColumnWidth(2, 25 * 256);
    }

    private CellStyle tableCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    private CellStyle greyStyle(Workbook workbook) {
        CellStyle greyStyle = workbook.createCellStyle();
        greyStyle.cloneStyleFrom(tableCellStyle(workbook));
        greyStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        greyStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return greyStyle;
    }


    private Cell createCellWithStyle(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (isNumeric(value)) {
            cell.setCellValue(Integer.parseInt(value));
        } else {
            cell.setCellValue(value);
        }
        cell.setCellStyle(style);
        return cell;
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}
