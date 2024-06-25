package HelperClasses;

import Database.ObjectClasses.Checkout;
import Database.ObjectClasses.Part;
import Database.OverdueItem;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExportToExcel {

    private void formatExcelFile(String[] columns, Workbook workbook, Sheet sheet) {
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
    }

    /**
     * Exports checked out items to a excel file
     *
     * @param list List of checked out items and their information
     */
    public void exportCheckedOut(ObservableList<Checkout> list) {
        FileChooser fileChooserSave = new FileChooser();
        fileChooserSave.setTitle("Save File");
        fileChooserSave.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel", "*.xlsx"));


        String[] columns = {"Student Name", "Part Name", "Barcode", "Check Out Date", "Due Date"}; //Number of columns in tableview
        int rowNum = 1;
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Checked Out Items");

        formatExcelFile(columns, workbook, sheet);

        for (Checkout items : list) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(items.getStudentName().getValue());
            row.createCell(1).setCellValue(items.getPartName().getValue());
            row.createCell(2).setCellValue(items.getBarcode().getValue());
            row.createCell(3).setCellValue(items.getCheckedOutDate().getValue().toString());
            row.createCell(4).setCellValue(items.getDueDate().getValue().toString());
        }
        buildExcelFile(fileChooserSave, columns, workbook, sheet);
    }

    /**
     * Exports overdue items to excel sheet
     *
     * @param list List of overdue items
     */
    public void exportOverdue(ObservableList<OverdueItem> list) {
        FileChooser fileChooserSave = new FileChooser();
        fileChooserSave.setTitle("Save File");
        fileChooserSave.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel", "*.xlsx"));


        String[] columns = {"Student Name", "Student ID", "Part Name", "Serial Number", "Barcode", "Due Date"}; //Number of columns in tableview
        int rowNum = 1;
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Overdue Items");

        formatExcelFile(columns, workbook, sheet);

        for (OverdueItem items : list) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(items.getName().getValue());
            row.createCell(1).setCellValue(items.getID().getValue());
            row.createCell(2).setCellValue(items.getPart().getValue());
            row.createCell(3).setCellValue(items.getSerialNumber().getValue());
            row.createCell(4).setCellValue(items.getBarcode().getValue());
            row.createCell(5).setCellValue(items.getDate().getValue().toString());
        }
        buildExcelFile(fileChooserSave, columns, workbook, sheet);
    }

    private void buildExcelFile(FileChooser fileChooserSave, String[] columns, Workbook workbook, Sheet sheet) {
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
        StageUtils.getInstance().slidingAlert("Success", "File created successfully!");
    }

    /**
     * Exports the list of parts to excel
     *
     * @param list List of parts to export
     */
    public void exportPartList(ObservableList<Part> list) {
        FileChooser fileChooserSave = new FileChooser();
        fileChooserSave.setTitle("Save File");
        fileChooserSave.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel", "*.xlsx"));


        String[] columns = {"Part Name", "Serial Number", "Location", "Barcode"}; //Number of columns in tableview
        int rowNum = 1;
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Parts List");

        formatExcelFile(columns, workbook, sheet);

        for (Part items : list) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(items.getPartName());
            row.createCell(1).setCellValue(items.getSerialNumber());
            row.createCell(2).setCellValue(items.getLocation());
            row.createCell(3).setCellValue(items.getBarcode());
        }
        buildExcelFile(fileChooserSave, columns, workbook, sheet);
    }

    /**
     * Exports transaction history to excel
     *
     * @param list List of transactions
     */
    public void exportTransactionHistory(ObservableList<Checkout> list) {
        FileChooser fileChooserSave = new FileChooser();
        fileChooserSave.setTitle("Save File");
        fileChooserSave.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel", "*.xlsx"));


        String[] columns = {"Student Name", "Part Name", "Barcode", "Action", "Date"}; //Number of columns in tableview
        int rowNum = 1;
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Transaction History");

        formatExcelFile(columns, workbook, sheet);

        for (Checkout items : list) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(items.getStudentName().get());
            row.createCell(1).setCellValue(items.getPartName().get());
            row.createCell(2).setCellValue(items.getBarcode().get());
            row.createCell(3).setCellValue(items.getAction().get());
            row.createCell(4).setCellValue(items.getDate().get().toString());
        }
        buildExcelFile(fileChooserSave, columns, workbook, sheet);
    }

}
