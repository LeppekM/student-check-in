package InventoryController;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerMenu implements Initializable {

    @FXML
    private VBox mainMenuScene;

    @FXML
    private Button inventory;

    public List <String> studentIDArray = new ArrayList<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inventory.setOnAction(event -> openInventory());
    }

    public void openInventory(){
        newStage("InventoryPage.fxml");
    }

    public void openCheckItemsPage(){ newStage("CheckOutItems.fxml"); }

    public void newStage(String fxml){
        try {
            URL myFxmlURL = ClassLoader.getSystemResource(fxml);
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            mainMenuScene.getScene().setRoot(loader.load(myFxmlURL));

        }
        catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
            invoke.printStackTrace();

        }
    }

    public void openCheckoutFromScanner(KeyEvent keyEvent){
        int studentIDLength = 6;

        if(keyEvent.getCharacter().matches("^[0-9]$")) {
            studentIDArray.add(keyEvent.getCode().toString());
            if (studentIDArray.size() == studentIDLength) {
                newStage("CheckOutItems.fxml");
            }
        }
    }


}