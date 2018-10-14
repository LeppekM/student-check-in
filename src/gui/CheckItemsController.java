package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
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
    private TableColumn studentIDCheckin;

    @FXML
    private TableColumn barcodeCheckin;

    @FXML
    private TableColumn partNameCheckin;

    @FXML
    private TableColumn quantityCheckin;

    @FXML
    private TableColumn fault;

    @FXML
    private TableColumn actionCheckin;

    @FXML
    TableView checkOutTableView;

    @FXML
    TableView checkInTableView;

    @FXML
    private VBox scene;

    @FXML
    ListView checkOutTable, savedTable;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setCheckoutItems();
        setCheckinItems();
    }

    private void setCheckinItems() {
        generateInfo(studentIDCheckin, barcodeCheckin, partNameCheckin, quantityCheckin, fault, actionCheckin, checkInTableView);
    }
    private void setCheckoutItems() {
        generateInfo(studentID, barcode, partName, quantity, overnight, action, checkOutTableView);
    }

    private void generateInfo(TableColumn studentID, TableColumn barcode, TableColumn partName, TableColumn quantity, TableColumn checkBox, TableColumn action, TableView tableView) {
        ObservableList<CheckItemsTable> data = FXCollections.observableArrayList(new CheckItemsTable("", "","",""));


        studentID.setCellValueFactory(
                new PropertyValueFactory<CheckItemsTable, String>("studentID")
        );
        barcode.setCellValueFactory(
                new PropertyValueFactory<CheckItemsTable, String>("barcode")
        );
        partName.setCellValueFactory(
                new PropertyValueFactory<CheckItemsTable, String>("partName")
        );
        quantity.setCellValueFactory(
                new PropertyValueFactory<CheckItemsTable, String>("quantity")
        );
        checkBox.setCellValueFactory(
                new PropertyValueFactory<CheckItemsTable, String>("overnight")
        );
        action.setCellValueFactory(
                new PropertyValueFactory<CheckItemsTable, String>("button")
        );

        tableView.setItems(data);

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
