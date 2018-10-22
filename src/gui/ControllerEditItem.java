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

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerEditItem implements Initializable {

    @FXML
    private TextField nameField, serialField, manufacturerField, quantityField, priceField, vendorField, barcodeField;

    @FXML
    private Button submit;

    @FXML
    private Hyperlink cancel;

    private Part selectedPart;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initData(Part part){
        selectedPart = part;
        populateFields(selectedPart);
    }

    @FXML
    private void populateFields(Part part){
        nameField.setText(part.getName());
        serialField.setText(String.valueOf(part.getSerial()));
        manufacturerField.setText(part.getManufacturer());
        quantityField.setText(String.valueOf(part.getQuantity()));
        priceField.setText(String.valueOf(part.getPrice()));
        vendorField.setText(part.getVendor());
        barcodeField.setText(part.getBarcode());
    }

    @FXML
    public void goBack(){
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void submitItem(){
        Part editedPart = null;
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

            editedPart =new Part(name, serial, manufacturer, quantity, price, vendor, "IN", barcode, false, 0);
        }
        catch(NullPointerException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, "One or more fields are not empty. Please make sure all fields are filled.");
            alert.showAndWait();
        }
        catch(NumberFormatException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, "One or more fields are not correctly entered.");
            alert.showAndWait();
        }
        try {
            if (editedPart != null) {
                Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/parts", "root", "xam54678");
                byte fault = 0;
                if (editedPart.getFault()){
                    fault = 1;
                }
                String addToDB = "Insert into parts (serialNumber, partName, price, vendor, manufacturer, location, barcode," +
                        "fault, studentID) VALUES ('" + editedPart.getSerial() + "', '" + editedPart.getName() + "', " + editedPart.getPrice() + ", '" +
                        editedPart.getVendor() + "', '" + editedPart.getManufacturer() + "', '" + editedPart.getLocation() + "', " + editedPart.getBarcode() +
                        ", " + fault + ", " + editedPart.getStudentId() + ");";
                editSQLData(con, addToDB);
                System.out.println("Part edited!");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void editSQLData(Connection conn, String rawStatement) {
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
