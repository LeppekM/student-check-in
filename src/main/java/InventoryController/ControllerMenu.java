package InventoryController;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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

    @FXML
    private ImageView msoeBackgroundImage;

    private List <String> studentIDArray = new ArrayList<>();
    private StageWrapper stageWrapper = new StageWrapper();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inventory.setOnAction(event -> openInventory());
        Image image = new Image("images/msoeBackgroundImage.png");
        this.msoeBackgroundImage.setImage(image);
    }

    public void openInventory(){
        newStage("fxml/InventoryPage.fxml");
    }

    public void openCheckItemsPage(){ newStage("fxml/CheckOutItems.fxml"); }

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
        studentIDArray.add(keyEvent.getCharacter());
        if(stageWrapper.getStudentID(studentIDArray).matches("^(rfid)$")) {
                newStage("fxml/CheckOutItems.fxml");
            }
    }


}