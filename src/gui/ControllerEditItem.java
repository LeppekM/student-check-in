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
import java.util.List;
import java.util.ResourceBundle;

public class ControllerEditItem implements Initializable {

    @FXML
    private TextField nameField, serialField, manufacturerField, quantityField, priceField, vendorField, barcodeField;

    @FXML
    private Button submit;

    @FXML
    private Hyperlink cancel;


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
        Part editedPart = null;
        try {
            if (nameField.getText().isEmpty() || serialField.getText().isEmpty() || manufacturerField.getText().isEmpty()
                    || quantityField.getText().isEmpty() || priceField.getText().isEmpty() || vendorField.getText().isEmpty()
                    || barcodeField.getText().isEmpty()){
                throw new NullPointerException("One or more fields are empty.");
            }
            String name = nameField.getText();
            long serial = Long.parseLong(serialField.getText());
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
        if(editedPart != null){
            System.out.println("Part edited!");
        }
    }
}
