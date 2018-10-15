package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ManageWorkersController implements Initializable {

    @FXML
    private VBox scene;

    @FXML
    private Button addWorkerButtonManageWorkersPage,
            viewWorkerButtonManageWrokersPage,
            deleteWorkerButtonManageWorkersPage,
            backToHomeButtonManageWorkersPage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void backToHome() {
        try {
            Pane pane = FXMLLoader.load(getClass().getResource("Menu.fxml"));
            scene.getScene().setRoot(pane);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
        }
    }

    public void addWorker() {
        try {
            Stage diffStage = new Stage();
            Pane pane = FXMLLoader.load(getClass().getResource("AddWorker.fxml"));
            Scene scene = new Scene(pane);
            diffStage.setScene(scene);
            diffStage.initModality(Modality.APPLICATION_MODAL);
            diffStage.setTitle("Add Worker");
            diffStage.showAndWait();
        } catch (IOException e) {

        }
    }

    public void viewWorker() {

    }

    public void deleteWorker() {

    }

}