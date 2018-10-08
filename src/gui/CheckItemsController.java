package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CheckItemsController implements Initializable{

    @FXML
    private TableColumn studentID;

    @FXML
    private MenuItem quit;

    @FXML
    private VBox scene;

    @FXML
    private Button returnHome;

    @FXML
    ListView checkOutTable, savedTable;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setItems();
    }

    private void setItems() {

    }
    public void close(){
        scene.getScene().getWindow().hide();
    }

    public void returnHome(){
        scene.getScene().getWindow().hide();
    }

    public void popUp(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2){
            Controller c = new Controller();
            c.newStage("infoPopUp.fxml", "Item Info");
        }
    }

}
