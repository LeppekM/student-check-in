package InventoryController;

import Database.DatabaseLogin;
import Database.EditPart;
import Database.Part;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class ControllerEditPart extends ControllerInventoryPage implements Initializable {
    @FXML
    private VBox sceneEditPart;

    @FXML
    private TextField nameField;

    @FXML
    private TextField serialField;

    @FXML
    private TextField manufacturerField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField vendorField;

    @FXML
    private TextField locationField;

    private Part part;

    private EditPart editPart = new EditPart();    // change this to get part

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        part = null;
    }

    public void initPart(Part part) {
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        if (this.part == null && part != null) {
            this.part = part;
            nameField.setText(part.getPartName());
            serialField.setText(part.getSerialNumber());
            manufacturerField.setText(part.getManufacturer());
            quantityField.setText("" + part.getQuantity());
            priceField.setText("$" + df.format(part.getPrice()));
            vendorField.setText(part.getVendor());
            locationField.setText(part.getLocation());
        }
    }

    /**
     * Edits the part in database
     */
    public void updateItem(){
        editPart.editItem(getPartFromInput(), DatabaseLogin.username, DatabaseLogin.password);
        partEditedSuccess();
        close();
    }

    /**
     * Helper method that sets creates a part from the user input
     */
    private Part getPartFromInput() {
        String partName = nameField.getText();
        String serialNumber = serialField.getText();
        String manufacturer = manufacturerField.getText();
        double price = Double.parseDouble(priceField.getText().substring(1));
        String vendor = vendorField.getText();
        String location = locationField.getText();
        String barcode = serialField.getText();
        int quantity = Integer.parseInt(quantityField.getText());
        part.update(partName, serialNumber, manufacturer, price, vendor, location, barcode, quantity);
        return part;
    }

    /**
     * Creates an alert informing user that part was edited successfully
     */
    private void partEditedSuccess(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Part Edited successfully");
        alert.showAndWait();
    }

    /**
     * Returns to main inventory page
     */
    public void goBack(){
        close();
    }

    /**
     * Helper method to close platform
     */
    private void close(){
        sceneEditPart.getScene().getWindow().hide();
    }


}
