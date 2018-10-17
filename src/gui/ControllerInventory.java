package gui;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ControllerInventory implements Initializable {

    @FXML
    private VBox sceneInv;

    @FXML
    private Button print, back, add, remove;

    @FXML private TableView<Item> tableView;

    @FXML private TableColumn<Item,String> partName, serialNumber, manufacturer, quantity, price, vendor, location, barcode, fault, studentId;

    private final ObservableList<Item> data
            = FXCollections.observableArrayList(
            new Item("HDMI Cable", 234567, "Sony", 2, 5.99, "MSOE", "OUT", "H233J788", false, 533277),
            new Item("Raspberry Pi", 567890, "Pi Foundation", 3, 29.99, "MSOE", "IN", "P845J788", true, 000000)
    );


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTable();
        tableView.getItems().setAll(this.data);
    }

    private void populateTable() {
        partName.setCellValueFactory(new PropertyValueFactory("partName"));
        serialNumber.setCellValueFactory(new PropertyValueFactory("serialNumber"));
        manufacturer.setCellValueFactory(new PropertyValueFactory("manufacturer"));
        quantity.setCellValueFactory(new PropertyValueFactory("quantity"));
        price.setCellValueFactory(new PropertyValueFactory("price"));
        vendor.setCellValueFactory(new PropertyValueFactory("vendor"));
        location.setCellValueFactory(new PropertyValueFactory("location"));
        barcode.setCellValueFactory(new PropertyValueFactory("barcode"));
        fault.setCellValueFactory(new PropertyValueFactory("fault"));
        studentId.setCellValueFactory(new PropertyValueFactory("studentId"));
    }

    public void executeSqlScript(Connection conn, File inputFile) {

        // Delimiter
        String delimiter = ";";

        // Create scanner
        Scanner scanner;
        try {
            scanner = new Scanner(inputFile).useDelimiter(delimiter);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return;
        }

        // Loop through the SQL file statements
        Statement currentStatement = null;
        while(scanner.hasNext()) {

            // Get statement
            String rawStatement = scanner.next() + delimiter;
            try {
                // Execute statement
                currentStatement = conn.createStatement();
                currentStatement.execute(rawStatement);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Release resources
                if (currentStatement != null) {
                    try {
                        currentStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                currentStatement = null;
            }
        }
        scanner.close();
    }


    public void goBack(){
        try {
            Pane pane = FXMLLoader.load(getClass().getResource("Menu.fxml"));
            sceneInv.getScene().setRoot(pane);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
        }
    }
    public void printReport(){
        try {
            Pane pane = FXMLLoader.load(getClass().getResource("manageWorkers.fxml"));
            sceneInv.getScene().setRoot(pane);
        }
        catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();

        }
    }

    public void addItem(){
        try {
            Stage diffStage = new Stage();
            Pane pane = FXMLLoader.load(getClass().getResource("addItem.fxml"));
            Scene scene = new Scene(pane);
            diffStage.setScene(scene);
            diffStage.initModality(Modality.APPLICATION_MODAL);
            diffStage.setTitle("Add Part");
            diffStage.showAndWait();
        }
        catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();

        }
    }

    public void removeItem(){
        try {
            Stage diffStage = new Stage();
            Pane pane = FXMLLoader.load(getClass().getResource("removeConfirmation.fxml"));
            Scene scene = new Scene(pane);
            diffStage.setScene(scene);
            diffStage.initModality(Modality.APPLICATION_MODAL);
            diffStage.setTitle("Are you sure?");
            diffStage.showAndWait();
        }
        catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();

        }
    }

    public static class Item {

        private final SimpleStringProperty partName, manufacturer, vendor, location, barcode;
        private final SimpleDoubleProperty price;
        private final SimpleIntegerProperty quantity;
        private final SimpleLongProperty serialNumber, studentId;
        private final SimpleBooleanProperty fault;

        private Item(String partName, long serialNumber, String manufacturer, int quantity, double price, String vendor, String location, String barcode, boolean fault, long studentId) {
            this.partName = new SimpleStringProperty(partName);
            this.serialNumber = new SimpleLongProperty(serialNumber);
            this.manufacturer = new SimpleStringProperty(manufacturer);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.price = new SimpleDoubleProperty(price);
            this.vendor = new SimpleStringProperty(vendor);
            this.location = new SimpleStringProperty(location);
            this.barcode = new SimpleStringProperty(barcode);
            this.fault = new SimpleBooleanProperty(fault);
            this.studentId = new SimpleLongProperty(studentId);
        }

        public String getName() {
            return this.partName.get();
        }

        public void setName(String name) {
            this.partName.set(name);
        }

        public long getSerial() {
            return this.serialNumber.get();
        }

        public void setSerial(long serial) {
            this.serialNumber.set(serial);
        }

        public String getManufacturer() {
            return manufacturer.get();
        }

        public void setManufacturer(String manufacturer) {
            this.manufacturer.set(manufacturer);
        }

        public int getQuantity() {
            return quantity.get();
        }

        public void setQuantity(int quant) {
            quantity.set(quant);
        }

        public double getPrice() {
            return price.get();
        }

        public void setPrice(double price) {
            this.price.set(price);
        }

        public String getVendor() {
            return vendor.get();
        }

        public void setVendor(String vendor) {
            this.vendor.set(vendor);
        }

        public String getLocation() {
            return location.get();
        }

        public void setLocation(String location) {
            this.location.set(location);
        }

        public String getBarcode() {
            return barcode.get();
        }

        public void setBarcode(String barcode) {
            this.barcode.set(barcode);
        }

        public boolean getFault() {
            return fault.get();
        }

        public void setFault(boolean fault) {
            this.fault.set(fault);
        }

        public long getStudentId() {
            return studentId.get();
        }

        public void setVendor(long studentId) {
            this.studentId.set(studentId);
        }
    }
}
