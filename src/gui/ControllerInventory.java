package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerInventory implements Initializable {

    private TableView tableInv = new TableView();

    @FXML
    private Button print;

    @FXML
    private Button back;




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTable();
    }

    private void populateTable(){
        tableInv.setEditable(true);

        TableColumn firstNameCol = new TableColumn("First Name");
        TableColumn lastNameCol = new TableColumn("Last Name");
        TableColumn emailCol = new TableColumn("Email");

        tableInv.getColumns().addAll(firstNameCol, lastNameCol, emailCol);
    }

    public void goBack(){
        Stage stage = (Stage) back.getScene().getWindow();
        stage.close();
    }
    public void printReport(){
        System.out.println("You're printing something I guess...");
        return;
    }

}
