package InventoryController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerMenu implements Initializable {

    @FXML
    private VBox mainMenuScene;

    @FXML
    private Button inventory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inventory.setOnAction(event -> openInventory());
    }

    public void openInventory(){
        newStage("InventoryPage.fxml");
    }

    public void openCheckItemsPage(){
        Stage stage = new Stage();
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("CheckOutItems.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            Parent root = loader.load(myFxmlURL);
            Scene scene = new Scene(root, 600, 400);
            stage.setTitle("Checkout Tab");
            stage.initOwner(mainMenuScene.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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


}