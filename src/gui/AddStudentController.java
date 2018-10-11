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

public class AddStudentController implements Initializable {

    @FXML
    private VBox scene;

    @FXML
    private Button addButtonAddStudentPage, cancelButtonAddStudentPage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addButtonAddStudentPage.setAlignment(Pos.CENTER);
    }

    public void addStudent() {

    }

    public void cancel() {
        scene.getScene().getWindow().hide();
    }

}