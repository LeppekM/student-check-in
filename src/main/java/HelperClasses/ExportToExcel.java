package HelperClasses;

import Database.HistoryItems;
import Database.ObjectClasses.Part;
import Database.OverdueItem;
import InventoryController.CheckedOutItems;
import InventoryController.FaultyPartTabTableRow;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExportToExcel {
    StageWrapper helper = new StageWrapper();

    /**
     * Exports faulty parts to excel file
     * @param list List of faulty parts
     */
    public void exportFaulty(ObservableList<FaultyPartTabTableRow> list){
        FileChooser fileChooserSave = new FileChooser();
        fileChooserSave.setTitle("Save File");
        fileChooserSave.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel", "*.xlsx"));



        String[] columns = {"Part Name", "Location", "Barcode",  "Student Name", "Student Email","Price","Fault Description"}; //Number of columns in tableview
        int rowNum = 1;
        Workbook workbook = new XSSFWorkbook();

        CreationHelper createHelper = workbook.getCreationHelper();

        Sheet sheet = workbook.createSheet("Faulty Items");

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

        for (FaultyPartTabTableRow items :list) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(items.getPartName().getValue());
            row.createCell(1).setCellValue(items.getLocation().getValue());
            row.createCell(2).setCellValue(items.getBarcode().getValue());
            row.createCell(3).setCellValue(items.getStudentName().getValue());
            row.createCell(4).setCellValue(items.getStudentEmail().getValue());
            row.createCell(5).setCellValue(items.getPrice().getValue());
            row.createCell(6).setCellValue(items.getDescription().getValue());

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
        } catch (NullPointerException | IOException e) {
            return;
        }
        helper.slidingAlert("Success", "File created successfully!");

    }

    /**
     * Exports checked out items to a excel file
     * @param list List of checked out items and their information
     */
    public void exportCheckedOut(ObservableList<CheckedOutItems> list){
        FileChooser fileChooserSave = new FileChooser();
        fileChooserSave.setTitle("Save File");
        fileChooserSave.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel", "*.xlsx"));



        String[] columns = {"Student Name", "Part Name", "Barcode", "Check Out Date", "Due Date"}; //Number of columns in tableview
        int rowNum = 1;
        Workbook workbook = new XSSFWorkbook();

        CreationHelper createHelper = workbook.getCreationHelper();

        Sheet sheet = workbook.createSheet("Checked Out Items");

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

        for (CheckedOutItems items :list) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(items.getStudentName().getValue());
            row.createCell(1).setCellValue(items.getPartName().getValue());
            row.createCell(2).setCellValue(items.getBarcode().getValue());
            row.createCell(3).setCellValue(items.getCheckedOutDate().getValue());
            row.createCell(4).setCellValue(items.getDueDate().getValue());
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
        } catch (NullPointerException | IOException e) {
            return;
        }
        helper.slidingAlert("Success", "File created successfully!");
    }

    /**
     * Exports overdue items to excel sheet
     * @param list List of overdue items
     */
    public void exportOverdue(ObservableList<OverdueItem> list) {
        FileChooser fileChooserSave = new FileChooser();
        fileChooserSave.setTitle("Save File");
        fileChooserSave.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel", "*.xlsx"));



        String[] columns = {"Student Name", "Student ID", "Part Name", "Barcode", "Due Date"}; //Number of columns in tableview
        int rowNum = 1;
        Workbook workbook = new XSSFWorkbook();

        CreationHelper createHelper = workbook.getCreationHelper();

        Sheet sheet = workbook.createSheet("Overdue Items");

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
            row.createCell(0).setCellValue(items.getName().getValue());
            row.createCell(1).setCellValue(items.getID().getValue());
            row.createCell(2).setCellValue(items.getPart().getValue());
            row.createCell(3).setCellValue(items.getBarcode().getValue());
            row.createCell(4).setCellValue(items.getDate().getValue());
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
        } catch (NullPointerException | IOException e) {
            return;
        }
        helper.slidingAlert("Success", "File created successfully!");
    }

    /**
     * Exports the list of parts to excel
     * @param list List of parts to export
     */
    public void exportPartList(ObservableList<Part> list){
        FileChooser fileChooserSave = new FileChooser();
        fileChooserSave.setTitle("Save File");
        fileChooserSave.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel", "*.xlsx"));



        String[] columns = {"Part Name", "Serial Number", "Location", "Barcode"}; //Number of columns in tableview
        int rowNum = 1;
        Workbook workbook = new XSSFWorkbook();

        CreationHelper createHelper = workbook.getCreationHelper();

        Sheet sheet = workbook.createSheet("Parts List");

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

        for (Part items :list) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(items.getPartName());
            row.createCell(1).setCellValue(items.getSerialNumber());
            row.createCell(2).setCellValue(items.getLocation());
            row.createCell(3).setCellValue(items.getBarcode());
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
        } catch (NullPointerException | IOException e) {
            return;
        }
        helper.slidingAlert("Success", "File created successfully!");
    }

    /**
     * Exports transaction history to excel
     * @param list List of transactions
     */
    public void exportTransactionHistory(ObservableList<HistoryItems> list) {
        FileChooser fileChooserSave = new FileChooser();
        fileChooserSave.setTitle("Save File");
        fileChooserSave.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel", "*.xlsx"));



        String[] columns = {"Student Name", "Part Name", "Serial Number", "Action", "Date"}; //Number of columns in tableview
        int rowNum = 1;
        Workbook workbook = new XSSFWorkbook();

        CreationHelper createHelper = workbook.getCreationHelper();

        Sheet sheet = workbook.createSheet("Transaction History");

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

        for (HistoryItems items :list) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(items.getStudentName());
            row.createCell(1).setCellValue(items.getPartName());
            row.createCell(2).setCellValue(items.getSerialNumber());
            row.createCell(3).setCellValue(items.getAction());
            row.createCell(4).setCellValue(items.getDate());
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
        } catch (NullPointerException | IOException e) {
            return;
        }

        helper.slidingAlert("Success", "File created successfully!");
    }

}
