package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CheckItemsController implements Initializable{

    @FXML
    TableColumn studentID;

    @FXML
    MenuItem quit;

    @FXML
    ListView checkOutTable;


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
