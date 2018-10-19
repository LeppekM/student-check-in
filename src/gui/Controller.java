package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private VBox mainMenuScene;

    @FXML
    private Button manageWorkers;

    @FXML
    private Button manageStudents;

    @FXML
    private Button checkin;

    @FXML
    private Button inventory;




    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void openCheckInPage(){
        newStage("checkout.fxml", "Check-in/out Parts");
    }

    public void openInventory(){
        newStage("openInventory.fxml", "Inventory");
    }

    public void manageStudents() {
        newStage("ManageStudents.fxml", "Manage Students");
    }

    public void manageWorkers(){
        newStage("manageWorkers.fxml", "Manage Workers");
    }

    public void newStage(String fxml, String title){
        try {
            Pane pane = FXMLLoader.load(getClass().getResource(fxml));
            mainMenuScene.getScene().setRoot(pane);
        }
        catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
            invoke.printStackTrace();

        }
    }


}
