package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

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
        try {
            Stage designCar = new Stage();
            VBox anchorPane = (VBox) FXMLLoader.load(getClass().getResource("checkout.fxml"));
            Scene scene = new Scene(anchorPane);
            designCar.setScene(scene);
            designCar.initModality(Modality.APPLICATION_MODAL);
            designCar.setTitle("Design a Car");
            designCar.showAndWait();
        }
        catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();

        }

    }



    public void openInventory(){
        try {
            Stage designCar = new Stage();
            VBox anchorPane = (VBox) FXMLLoader.load(getClass().getResource("openInventory.fxml"));
            Scene scene = new Scene(anchorPane);
            designCar.setScene(scene);
            designCar.initModality(Modality.APPLICATION_MODAL);
            designCar.setTitle("Design a Car");
            designCar.showAndWait();
        }
        catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();

        }


    }
    public void manageStudents(){
        try {
            Stage designCar = new Stage();
            VBox anchorPane = (VBox) FXMLLoader.load(getClass().getResource("manageStudents.fxml"));
            Scene scene = new Scene(anchorPane);
            designCar.setScene(scene);
            designCar.initModality(Modality.APPLICATION_MODAL);
            designCar.setTitle("Design a Car");
            designCar.showAndWait();
        }
        catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();

        }


    }
    public void manageWorkers(){
        try {
            Stage designCar = new Stage();
            VBox anchorPane = (VBox) FXMLLoader.load(getClass().getResource("manageWorkers.fxml"));
            Scene scene = new Scene(anchorPane);
            designCar.setScene(scene);
            designCar.initModality(Modality.APPLICATION_MODAL);
            designCar.setTitle("Design a Car");
            designCar.showAndWait();
        }
        catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();

        }



    }


}
