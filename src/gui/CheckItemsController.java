package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;
import java.util.ResourceBundle;

public class CheckItemsController implements Initializable{

    @FXML
    TableColumn studentID;

    @FXML
    MenuItem quit;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setItems();
    }

    public void setItems() {

        VBox headerGraphic = new VBox();
        headerGraphic.setAlignment(Pos.CENTER);
        studentID.setGraphic(headerGraphic);
    }
    public void close(){
        System.out.println("HI");
    }
}
