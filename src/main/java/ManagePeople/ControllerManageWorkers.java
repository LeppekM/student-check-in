package ManagePeople;

import Database.*;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;

public class ControllerManageWorkers {
    private ObservableList<ManageWorkersTabTableRow> tableRows;

    private Database database;

    @FXML
    private AnchorPane manageWorkersScene;

    @FXML
    private JFXTreeTableView<ManageWorkersTabTableRow> manageWorkersTable;
    private TreeItem<ManageWorkersTabTableRow> root;

    @FXML
    private JFXTextField searchInput;

    @FXML
    private Button addWorker;

    private JFXTreeTableColumn<ManageWorkersTabTableRow, String> nameCol, emailCol, adminCol;

    private String name, email, admin;

    private static ObservableList<Worker> data = FXCollections.observableArrayList();

    public void addWorker(ActionEvent actionEvent) {
    }

    public void goBack(ActionEvent actionEvent) {
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("fxml/Menu.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            manageWorkersScene.getChildren().clear();
            manageWorkersScene.getScene().setRoot(loader.load(myFxmlURL));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
        }
    }
}
