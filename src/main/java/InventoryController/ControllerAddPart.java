package InventoryController;

import Database.AddPart;
import Database.Part;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerAddPart extends ControllerInventoryPage implements Initializable {


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

    public void submitItem(){
        addPart.addItem(setPartFields());

    }

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


}
