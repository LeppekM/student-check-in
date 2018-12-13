package InventoryController;

import Database.Database;
import Database.Student;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class StudentPage implements Initializable {

    @FXML
    private Label studentName, email, RFID;

    @FXML
    private JFXTreeTableView coTable, oTable, sTable;

    private Database database;
    private Student student;

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
}
