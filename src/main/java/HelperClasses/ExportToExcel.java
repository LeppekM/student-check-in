package HelperClasses;

import Database.OverdueItem;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExportToExcel {
    StageWrapper helper = new StageWrapper();

    public void exportOverdue(ObservableList<OverdueItem> list) {
        FileChooser fileChooserSave = new FileChooser();
        fileChooserSave.setTitle("Save File");
        fileChooserSave.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel", "*.xlsx"));



        String[] columns = {"Part Name", "Student Name", "Date", "Serial Number", "Price"}; //Number of columns in tableview
        int rowNum = 1;
        Workbook workbook = new XSSFWorkbook();

        CreationHelper createHelper = workbook.getCreationHelper();

        Sheet sheet = workbook.createSheet("Main Information");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        for (OverdueItem items :list) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(items.getPart().getValue());
            row.createCell(1).setCellValue(items.getName().getValue());
            row.createCell(2).setCellValue(items.getDate().getValue());
            row.createCell(3).setCellValue(items.getSerial().getValue());
            row.createCell(4).setCellValue(items.getPrice().getValue());
        }
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
        try {
            FileOutputStream fileOut = new FileOutputStream(fileChooserSave.showSaveDialog(null));
            workbook.write(fileOut);
            fileOut.close();
            // Closing the workbook
            workbook.close();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        helper.slidingAlert("Success", "File created successfully!");
    }

}
