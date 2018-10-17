package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerInventory implements Initializable {

    @FXML
    private VBox sceneInv;

    @FXML
    private Button print, back, add, remove;

    @FXML private TableView<Part> tableView;

    @FXML private TableColumn<Part,String> partName, serialNumber, manufacturer, quantity;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTable();
    }

    private void populateTable() {

    }


    public void goBack(){
        try {
            Pane pane = FXMLLoader.load(getClass().getResource("Menu.fxml"));
            sceneInv.getScene().setRoot(pane);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
        }
    }
    public void printReport(){
        try {
            Pane pane = FXMLLoader.load(getClass().getResource("manageWorkers.fxml"));
            sceneInv.getScene().setRoot(pane);
        }
        catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();

        }
    }

    public void addItem(){
        try {
            Stage diffStage = new Stage();
            Pane pane = FXMLLoader.load(getClass().getResource("addItem.fxml"));
            Scene scene = new Scene(pane);
            diffStage.setScene(scene);
            diffStage.initModality(Modality.APPLICATION_MODAL);
            diffStage.setTitle("Add Part");
            diffStage.showAndWait();
        }
        catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();

        }
    }

    public void removeItem(){
        try {
            Stage diffStage = new Stage();
            Pane pane = FXMLLoader.load(getClass().getResource("removeConfirmation.fxml"));
            Scene scene = new Scene(pane);
            diffStage.setScene(scene);
            diffStage.initModality(Modality.APPLICATION_MODAL);
            diffStage.setTitle("Are you sure?");
            diffStage.showAndWait();
        }
        catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();

        }
    }

}
