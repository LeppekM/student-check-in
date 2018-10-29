package InventoryController;

import Database.Part;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerInventoryPage extends ControllerMenu implements Initializable {

    @FXML
    private AnchorPane inventoryScene;

    @FXML
    private Button back, add, remove;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    public void addPart(){
        //Called when the "Add" button is clicked
    }

    @FXML
    public void editPart(Part part){
        //Called when a part is double clicked in a table.
        //@param part the part that was double clicked
    }

    @FXML
    public void removePart(){
        //Called when the "Remove" button is clicked
    }

    public ObservableList<Part> populateList(ObservableList<Part> data){
        Part part1 = new Part("Raspberry Pi", "3453I214", "Pi Inc.", 35.99, "MSOE", "OUT", "J26734", false, 0);
        Part part2 = new Part("HDMI Cable", "H2J4364", "Sony", 4.99, "MSOE", "IN", "A43453", false, 1);
        data.add(part1);
        data.add(part2);
        return data;
    }

    @FXML
    public void goBack(){
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("Menu.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            inventoryScene.getChildren().clear();
            inventoryScene.getScene().setRoot(loader.load(myFxmlURL));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
        }
    }
}
