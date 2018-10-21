package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerAddItem implements Initializable {

    @FXML
    private TextField nameField, serialField, manufacturerField, quantityField, priceField, vendorField, barcodeField;

    @FXML
    private Button submit;

    @FXML
    private Hyperlink cancel;

    static String dbdriver = "com.mysql.jdbc.Driver";
    static String dburl = "jdbc:mysql://localhost";
    static String dbname = "parts";


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void goBack(){
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void submitItem(){
        Part addedPart = null;
        try {
            if (nameField.getText().isEmpty() || serialField.getText().isEmpty() || manufacturerField.getText().isEmpty()
                    || quantityField.getText().isEmpty() || priceField.getText().isEmpty() || vendorField.getText().isEmpty()
                    || barcodeField.getText().isEmpty()){
                throw new NullPointerException("One or more fields are empty.");
            }
            String name = nameField.getText();
            String serial = serialField.getText();
            String manufacturer = manufacturerField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());
            String vendor = vendorField.getText();
            String barcode = barcodeField.getText();

            addedPart =new Part(name, serial, manufacturer, quantity, price, vendor, "IN", barcode, false, 0);
        }
        catch(NullPointerException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, "One or more fields are not empty. Please make sure all fields are filled.");
            alert.showAndWait();
        }
        catch(NumberFormatException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, "One or more fields are not correctly entered.");
            alert.showAndWait();
        }
        ControllerInventory ci = new ControllerInventory();
        ci.tableView.getItems().add(addedPart);
        updateDataBase(addedPart);
        if(addedPart != null){
            System.out.println("Part submitted!");
        }
    }

    private void updateDataBase(Part part){
        String user = JOptionPane.showInputDialog("Enter username to update Parts database");
        String userPass = JOptionPane.showInputDialog("Enter password");
        try{
            Class.forName(dbdriver);
        }catch (ClassNotFoundException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Class not found");
            alert.showAndWait();
        }
        Connection connection = null;
        try{
            connection = DriverManager.getConnection((dburl + "/" + dbname), user, userPass);
            connection.setClientInfo("autoReconnect", "true");
        }catch (SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Connection failure");
            alert.showAndWait();
        }
        byte fault = 0;
        if (part.getFault()){
            fault = 1;
        }
        String addToDB = "Insert into parts_list (serialNumber, partName, price, vendor, manufacturer, location, barcode," +
                "fault, studentID) VALUES ('" + part.getSerial() + "', '" + part.getName() + "', " + part.getPrice() + ", '" +
                part.getVendor() + "', '" + part.getManufacturer() + "', '" + part.getLocation() + "', " + part.getBarcode() +
                ", " + fault + ", " + part.getStudentId() + ");";
        grabSQLData(connection, addToDB);
    }

    private void grabSQLData(Connection conn, String rawStatement) {
        Statement currentStatement = null;
        try {
            currentStatement = conn.createStatement();
            currentStatement.execute(rawStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
}
