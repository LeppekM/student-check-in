package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

public class CheckItemsController implements Initializable{

    @FXML
    private TableColumn studentID;

    @FXML
    private TableColumn barcode;

    @FXML
    private TableColumn partName;

    @FXML
    private TableColumn quantity;

    @FXML
    private TableColumn overnight;

    @FXML
    private TableColumn action;


    @FXML
    TableView checkOutTableView;

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
        ObservableList<CheckoutTable> data = FXCollections.observableArrayList(
            new CheckoutTable("", "","","")
        );


        studentID.setCellValueFactory(
                new PropertyValueFactory<CheckoutTable, String>("studentID")
        );
        barcode.setCellValueFactory(
                new PropertyValueFactory<CheckoutTable, String>("barcode")
        );
        partName.setCellValueFactory(
                new PropertyValueFactory<CheckoutTable, String>("partName")
        );
        quantity.setCellValueFactory(
                new PropertyValueFactory<CheckoutTable, String>("quantity")
        );
        overnight.setCellValueFactory(
                new PropertyValueFactory<CheckoutTable, String>("overnight")
        );
        action.setCellValueFactory(
                new PropertyValueFactory<CheckoutTable, String>("button")
        );

        checkOutTableView.setItems(data);

    }
    public void close(){
        scene.getScene().getWindow().hide();
    }

    public void returnHome()throws Exception{
        scene.getScene().getWindow().hide();
        Stage stage = new Stage();
        StudentCheckin studentCheckin = new StudentCheckin();
        studentCheckin.start(stage);
    }

    public void popUp(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2){
            Controller c = new Controller();
            c.newStage("infoPopUp.fxml", "Item Info");
        }
    }

}
