package InventoryController;

import Database.Database;
import Database.Student;
import HelperClasses.StageWrapper;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class StudentPage implements Initializable {

    @FXML
    private AnchorPane main;

    @FXML
    private Label studentName, email, RFID;

    @FXML
    private JFXTreeTableView coTable, oTable, sTable;

    private Database database;
    private Student student;
    private StageWrapper stageWrapper = new StageWrapper();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = new Database();
    }

    public void setStudent(String ID){
        student = database.selectStudent(Integer.parseInt(ID));
        studentName.setText(student.getName());
        email.setText(student.getEmail());
        RFID.setText(student.getID() + "");
    }

    public void goBack() {
        stageWrapper.newStage("CheckoutItems.fxml", main);
    }

    public void goHome() {
        stageWrapper.newStage("Menu.fxml", main);
    }
}
