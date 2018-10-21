package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.*;
import javafx.fxml.Initializable;

public class EditStudentController  implements  Initializable{

    @FXML
    TextField studentName, studentID, studentEmail, dateOfRental;

    @FXML
    ListView checkedOut, savedItems, overdueItems;

    @FXML
    Button save, cancel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Stage stage = (Stage) save.getScene().getWindow();
        save.setOnMouseClicked(event -> {
            if(!dateOfRental.getText().matches("[0-1][0-9]\\/[0-3][0-9]\\/[0-9]{4}")){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error, invalid date was entered.\nCheck format (mm/dd/yyyy)");
                alert.showAndWait();
            }
            if(!studentEmail.getText().matches("^(\\w+)@msoe\\.edu$")){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error, invalid email was entered.\nNeeds to be an MSOE email");
                alert.showAndWait();
            }
//            Stage stage = (Stage) save.getScene().getWindow();
//            if (event.getClickCount() == 1){
                try {
                    FileReader fr = new FileReader("src/students.txt");
                    BufferedReader br = new BufferedReader(fr);
                    String line;
                    BufferedWriter w = new BufferedWriter(new FileWriter(new File("src/students.txt")));
                    while((line = br.readLine()) != null){
                        if(line.contains(studentEmail.getCharacters())){
                            line = studentName.getText() + "," + studentID.getText() + "," + studentEmail.getText();
                        }
                        w.write(line);
                    }
                    w.close();
                    br.close();
                    fr.close();
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
                stage.close();
//            }
            cancel.setOnMouseClicked(event1 -> {
//                stage = (Stage) cancel.getScene().getWindow();
                stage.close();
            });
        });
    }

    EditStudentController(String[] student){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditStudent.fxml"));
            loader.setController(this);
            Pane pane = loader.load();
            Scene scene = new Scene(pane);
            initialize(loader.getLocation(), loader.getResources());
            studentName.setText(student[0]);
            studentID.setText(student[1]);
            studentEmail.setText(student[2]);
            Stage diff = new Stage();
            diff.setScene(scene);
            diff.showAndWait();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

//    @FXML
//    private void checkDate(KeyEvent keyEvent) {
//        if (keyEvent.getCode().equals(KeyCode.ENTER)){
//            if(!dateOfRental.getText().matches("[0-1][0-9]\\/[0-3][0-9]\\/[0-9]{4}")){
//                Alert alert = new Alert(Alert.AlertType.ERROR, "Error, invalid date was entered.\nCheck format (mm/dd/yyyy)");
//                alert.showAndWait();
//            }
//        }
//    }
//
//    public void checkEmail(KeyEvent keyEvent) {
//        if (keyEvent.getCode().equals(KeyCode.ENTER)){
//            if(!studentEmail.getText().matches("^(\\w+)@msoe\\.edu$")){
//                Alert alert = new Alert(Alert.AlertType.ERROR, "Error, invalid email was entered.\nNeeds to be an MSOE email");
//                alert.showAndWait();
//            }
//        }
//    }

//    public void saveData(MouseEvent mouseEvent) {
//        Stage stage = (Stage) save.getScene().getWindow();
//        if (mouseEvent.getClickCount() == 1){
//            try {
//                FileReader fr = new FileReader("src/students.txt");
//                BufferedReader br = new BufferedReader(fr);
//                String line;
//                BufferedWriter w = new BufferedWriter(new FileWriter(new File("src/students.txt")));
//                while((line = br.readLine()) != null){
//                    if(line.contains(studentEmail.getCharacters())){
//                        line = studentName.getText() + "," + studentID.getText() + "," + studentEmail.getText();
//                    }
//                    w.write(line);
//                }
//                w.close();
//                br.close();
//                fr.close();
//            }catch (FileNotFoundException e){
//                e.printStackTrace();
//            }catch (IOException e){
//                e.printStackTrace();
//            }
//            stage.close();
//        }
//    }
//
//    public void closePage(MouseEvent mouseEvent) {
//        if (mouseEvent.getClickCount() == 1){
//            Stage stage = (Stage) cancel.getScene().getWindow();
//            stage.close();
//        }
//    }
//
//    public void setDataValues(){
//
//        studentName.setText(student[0]);
//        studentID.setText(student[1]);
//        studentEmail.setText(student[2]);
//    }
}
