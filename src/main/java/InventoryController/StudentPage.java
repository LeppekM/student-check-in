package InventoryController;

import Database.OverdueItems;
import Database.Student;
import Database.Part;
import HelperClasses.StageWrapper;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class StudentPage /*implements Initializable*/ {

    @FXML
    private AnchorPane main;

    @FXML
    private Label studentName, email, RFID;

    @FXML
    private JFXTreeTableView coTable, oTable, sTable;

    @FXML
    private JFXTreeTableColumn<Student, String> coTableCol, oTableCol, sTableCol;

//    private Database database;
    private Student student;
    private StageWrapper stageWrapper = new StageWrapper();


//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        database = new Database();
//    }

    public void setStudent(Student s){
        student = s;
        studentName.setText(student.getName());
        email.setText(student.getEmail());
        RFID.setText(student.getID() + "");
        setTables();
    }

    private void setTables() {
        coTableCol = new JFXTreeTableColumn<>("Part Name");
        coTableCol.setPrefWidth(200);
        coTableCol.setCellValueFactory((Callback<TreeTableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>) param ->
                new SimpleStringProperty(param.getValue().getValue().getCheckedOut().get(0).getPartName()));

        oTableCol = new JFXTreeTableColumn<>("Part Name");
        oTableCol.setPrefWidth(200);
        oTableCol.setCellValueFactory((Callback<TreeTableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>) param ->
                new SimpleStringProperty(param.getValue().getValue().getOverdueItems().get(0).getPart()));

        sTableCol = new JFXTreeTableColumn<>("Part Name");
        sTableCol.setPrefWidth(200);
        sTableCol.setCellValueFactory((Callback<TreeTableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>) param ->
                new SimpleStringProperty(param.getValue().getValue().getSavedItems().get(0).getPartName()));

        populateTables();
    }

    private void populateTables(){
        coTable.getColumns().clear();
        oTable.getColumns().clear();
        sTable.getColumns().clear();
        coTable.getColumns().setAll(student.getCheckedOut());
        oTable.getColumns().setAll(student.getOverdueItems());
        sTable.getColumns().setAll(student.getSavedItems());
    }

    public void goBack() {
        stageWrapper.newStage("CheckoutItems.fxml", main);
    }

    public void goHome() {
        stageWrapper.newStage("Menu.fxml", main);
    }
}
