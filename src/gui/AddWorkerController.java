package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddWorkerController implements Initializable {

    @FXML
    private VBox scene;

    @FXML
    private Button addButtonAddWorkerPage, cancelButtonAddWorkerPage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addButtonAddWorkerPage.setAlignment(Pos.CENTER);
    }

    public void addWorker() {

    }

    public void cancel() {
        scene.getScene().getWindow().hide();
    }

}