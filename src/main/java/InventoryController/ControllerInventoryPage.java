package InventoryController;

import Database.Part;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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
        Stage stage = new Stage();
        try{
            URL myFxmlURL = ClassLoader.getSystemResource("AddPart.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            Parent root = loader.load(myFxmlURL);
            Scene scene = new Scene(root, 400, 400);
            stage.setTitle("Add a Part");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e){
            e.printStackTrace();
        }
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

    @FXML
    public void goBack(){
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("Menu.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            inventoryScene.getScene().setRoot(loader.load(myFxmlURL));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
        }
    }
}
