package InventoryController;

import Database.Part;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerTotalInventory extends ControllerMenu implements Initializable {

    @FXML
    private VBox sceneInv;

    @FXML
    private Button print, back;

    @FXML
    private TextField searchField;

    @FXML public TableView<Part> tableView;

    @FXML private TableColumn<Part,String> partName, serialNumber, manufacturer, price, vendor, location,
            barcode, fault, partID;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    public void search(){

    }

    @FXML
    public void goBack(){
        try {
            Pane pane = FXMLLoader.load(getClass().getResource("Menu.fxml"));
            sceneInv.getScene().setRoot(pane);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
        }
    }
}
