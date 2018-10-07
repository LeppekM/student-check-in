package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CheckItemsController implements Initializable{

    @FXML
    TableColumn studentID;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setItems();
    }

    public void setItems() {

        VBox headerGraphic = new VBox();
        headerGraphic.setAlignment(Pos.CENTER);
        studentID.setGraphic(headerGraphic);
    }
}
