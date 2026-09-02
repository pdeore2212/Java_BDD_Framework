package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.List;
import java.util.Map;

public class ExcelWriter {

    private static final String FILE_PATH = "Transaction_Logs.xlsx";

    public static synchronized void saveLifecycleToExcel(String srNo, String scenarioName, List<Map<String, String>> apiSteps) {
        Workbook workbook;
        Sheet sheet;
        File file = new File(FILE_PATH);

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = WorkbookFactory.create(fis);
                sheet = workbook.getSheet("Automation_Logs");
                if (sheet == null) {
                    sheet = workbook.createSheet("Automation_Logs");
                }
            } catch (Exception e) {
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("Automation_Logs");
            }
        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Automation_Logs");
        }

        // --- GRID HEADER DEFINITIONS WITH STYLING ---
        if (sheet.getLastRowNum() < 0) {
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Sr No", "Scenario Name/Test Case", "API", "Generated OTT", "Transaction ID", "Transaction Status", "Timestamp"};
            
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setBorderBottom(BorderStyle.MEDIUM);
            headerStyle.setBorderTop(BorderStyle.MEDIUM);
            headerStyle.setBorderLeft(BorderStyle.MEDIUM);
            headerStyle.setBorderRight(BorderStyle.MEDIUM);
            headerStyle.setAlignment(HorizontalAlignment.LEFT);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
        }

        int startRow = sheet.getLastRowNum() + 1;
        int totalSteps = apiSteps.size();

        CellStyle middleStyle = workbook.createCellStyle();
        middleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        middleStyle.setAlignment(HorizontalAlignment.LEFT);
        middleStyle.setBorderBottom(BorderStyle.THIN);
        middleStyle.setBorderTop(BorderStyle.THIN);
        middleStyle.setBorderLeft(BorderStyle.THIN);
        middleStyle.setBorderRight(BorderStyle.THIN);

        CellStyle regularBorderStyle = workbook.createCellStyle();
        regularBorderStyle.setBorderBottom(BorderStyle.THIN);
        regularBorderStyle.setBorderTop(BorderStyle.THIN);
        regularBorderStyle.setBorderLeft(BorderStyle.THIN);
        regularBorderStyle.setBorderRight(BorderStyle.THIN);

        // --- MATRIX ENTRY POOL ---
        for (int i = 0; i < totalSteps; i++) {
            int currentRowNum = startRow + i;
            Row row = sheet.createRow(currentRowNum);
            Map<String, String> stepData = apiSteps.get(i);

            // Left Side Data populated strictly on cell start row context
            Cell cellSr = row.createCell(0);
            Cell cellName = row.createCell(1);
            cellSr.setCellStyle(middleStyle);
            cellName.setCellStyle(middleStyle);

            if (i == 0) {
                cellSr.setCellValue(Double.parseDouble(srNo));
                cellName.setCellValue(scenarioName);
            }

            // Step Metrics Population
            Cell c2 = row.createCell(2); c2.setCellValue(stepData.getOrDefault("API", "N/A")); c2.setCellStyle(regularBorderStyle);
            Cell c3 = row.createCell(3); c3.setCellValue(stepData.getOrDefault("OTT", "N/A")); c3.setCellStyle(regularBorderStyle);
            Cell c4 = row.createCell(4); c4.setCellValue(stepData.getOrDefault("TxId", "N/A")); c4.setCellStyle(regularBorderStyle);
            Cell c5 = row.createCell(5); c5.setCellValue(stepData.getOrDefault("Status", "N/A")); c5.setCellStyle(regularBorderStyle);
            Cell c6 = row.createCell(6); c6.setCellValue(new java.util.Date().toString()); c6.setCellStyle(regularBorderStyle);
        }

        // --- APPLY VERTICAL COLUMN MERGING ---
        if (totalSteps > 1) {
            sheet.addMergedRegion(new CellRangeAddress(startRow, startRow + totalSteps - 1, 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(startRow, startRow + totalSteps - 1, 1, 1));
        }

        for (int i = 0; i < 7; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
            workbook.write(fos);
            workbook.close();
            System.out.println("[EXCEL WRITER] Successfully output structured grouped row lifecycle data.");
        } catch (IOException e) {
            System.err.println("[EXCEL ERROR] Failed to save grid block tracking updates.");
            e.printStackTrace();
        }
    }
}