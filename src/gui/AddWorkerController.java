package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.CheckBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddWorkerController implements Initializable {

    @FXML
    private VBox scene;

    @FXML
    private Button addButtonAddWorkerPage, cancelButtonAddWorkerPage;

    @FXML
    private TextField nameInputAddWorkerPage,
            emailInputAddWorkerPage,
            passwordInputAddWorkerPage,
            adminPinInputAddWorkerPage;

    @FXML
    private CheckBox isAdminCheckBoxAddWorkerPage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addButtonAddWorkerPage.setAlignment(Pos.CENTER);
    }

    public void addWorker() {
        String name = nameInputAddWorkerPage.getText();
        String email = emailInputAddWorkerPage.getText();
        String password = passwordInputAddWorkerPage.getText();
        boolean isAdmin = isAdminCheckBoxAddWorkerPage.isSelected();
        String adminPin = adminPinInputAddWorkerPage.getText();
        if (!email.matches("^(.+)@msoe\\.edu$")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, invalid email was entered.\nNeeds to be an MSOE email");
            alert.showAndWait();
        } else if (!name.equals("")) {
            Worker worker = null;
            if (isAdmin) {
                adminPopup();
                if (!adminPin.equals("")) {
                    worker = new Administrator(name, email, password, isAdmin, adminPin);
                }
            } else {
                worker = new StudentWorker(name, email, password);
            }
            if (worker != null) {
                writeWorker(worker);
                scene.getScene().getWindow().hide();
            }
        }
    }

    private void writeWorker(Worker worker) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/workers.txt", true))) {
            bw.write(worker.writeWorker() + "\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void cancel() {
        scene.getScene().getWindow().hide();
    }


    public void adminPopup(){
        if(isAdminCheckBoxAddWorkerPage.isSelected()){
            try {
                Stage diffStage = new Stage();
                Pane pane = FXMLLoader.load(getClass().getResource("AdminPopup.fxml"));
                Scene scene = new Scene(pane, 250, 200);
                diffStage.setScene(scene);
                diffStage.initModality(Modality.APPLICATION_MODAL);
                diffStage.setTitle("Admin Credentials Needed");
                diffStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}