package InventoryController;

import Database.AddPart;
import Database.DatabaseLogin;
import Database.Part;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerAddPart extends ControllerInventoryPage implements Initializable {
    @FXML
    private VBox sceneAddPart;

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

    private AddPart addPart = new AddPart();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Adds the part to database
     */
    public void submitItem(){
        addPart.addItem(setPartFields(), DatabaseLogin.username, DatabaseLogin.password);
        partAddedSuccess();
        close();
    }

    /**
     * Helper method that sets all the part information that will be added to the database
     * @return The part to be added to database
     */
    private Part setPartFields(){
        String partname = nameField.getText();
        String serialNumber = serialField.getText();
        String manufacturer = manufacturerField.getText();
        double price = Double.parseDouble(priceField.getText());
        String vendor = vendorField.getText();
        String location = locationField.getText();
        String barcode = serialField.getText();
        int quantity = Integer.parseInt(quantityField.getText());
        return new Part(partname, serialNumber, manufacturer, price, vendor, location, barcode, quantity);
    }

    /**
     * Creates an alert informing user that part was added successfully
     */
    private void partAddedSuccess(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Part added successfully");
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
        sceneAddPart.getScene().getWindow().hide();
    }


}
