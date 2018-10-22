package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private VBox mainMenuScene;

    @FXML
    private Button manageWorker, manageStudent, checkInOutButtonMenuPage, inventory;

    private static Worker currentWorker;

//    public Controller(Worker worker, StackPane stackPane) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("Menu.fxml"));
//            loader.setController(this);
//            Pane mainMenuPane = loader.load();
//            stackPane.getScene().setRoot(mainMenuPane);
//            initialize(loader.getLocation(), loader.getResources());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        this.currentWorker = worker;
//    }

    public void initData(Worker worker) {
        if (currentWorker == null) {
            this.currentWorker = worker;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        manageWorker.setOnAction(event -> manageWorkers());
        manageStudent.setOnAction(event -> manageStudents());
        inventory.setOnAction(event -> openInventory());
        checkInOutButtonMenuPage.setOnAction(event -> openCheckInPage());

    }

    public void openCheckInPage() {
        newStage("checkout.fxml", "Check-in/out Parts");
    }

    public void openInventory(){
        newStage("openInventory.fxml", "Inventory");
    }

    public void manageStudents() {
        if (currentWorker instanceof Administrator) {
            newStage("ManageStudents.fxml", "Manage Students");
        } else if (currentWorker instanceof StudentWorker) {
            if (((StudentWorker) currentWorker).canManageStudents()) {
                newStage("ManageStudents.fxml", "Manage Students");
            }
        }
    }

    public void manageWorkers(){
        if (currentWorker instanceof Administrator) {
            newStage("manageWorkers.fxml", "Manage Workers");
        }
    }

    public void newStage(String fxml, String title){
        try {
            Pane pane = FXMLLoader.load(getClass().getResource(fxml));
            mainMenuScene.getScene().setRoot(pane);
        }
        catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
            invoke.printStackTrace();

        }
    }


}
