package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
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

    public void popUp(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2){
            Controller c = new Controller();
            c.newStage("infoPopUp.fxml", "Item Info");
        }
    }
}
