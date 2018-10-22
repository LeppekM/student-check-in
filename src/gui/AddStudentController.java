package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for AddStudent.fxml
 */
public class AddStudentController implements Initializable {

    @FXML
    private VBox scene;

    @FXML
    private Button addButtonAddStudentPage, cancelButtonAddStudentPage;

    @FXML
    private TextField studentNameInputAddStudentPage,
                    studentRFIDInputAddStudentPage,
                    studentEmailInputAddStudentPage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addButtonAddStudentPage.setAlignment(Pos.CENTER);
    }

    /**
     * Functionality for the Add Student button that adds a student if the name and email are not null.
     */
    public void addStudent() {
        String name = studentNameInputAddStudentPage.getText();
        String rfid = studentRFIDInputAddStudentPage.getText();
        String email = studentEmailInputAddStudentPage.getText();
        if(!email.matches("^(.+)@msoe\\.edu$")){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, invalid email was entered.\nNeeds to be an MSOE email");
            alert.showAndWait();
        } else if (!name.equals("") && !rfid.equals("")) {
            Student student = new Student(name, rfid, email);
            writeStudent(student);
            scene.getScene().getWindow().hide();
        }
    }

    private void writeStudent(Student student) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/students.txt", true))) {
            bw.write(student.getName() + "," + student.getRfid() + "," + student.getEmail() + "\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void cancel() {
        scene.getScene().getWindow().hide();
    }

}