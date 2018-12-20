package InventoryController;

import Database.Database;
import Database.Part;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerFaultyTab  extends ControllerInventoryPage implements Initializable {

//    @FXML
//    private TextField searchTotal;

    @FXML
    public AnchorPane faultyPage;

    @FXML
    private TableView<Part> tableView;

    @FXML
    private TableColumn<Part,String> partName, serialNumber, location,
            barcode, faultDesc, partID;

    @FXML
    private TableColumn<Part, Boolean> fault;

    private static ObservableList<Part> data
            = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label emptytableLabel = new Label("No parts found.");
        emptytableLabel.setFont(new Font(18));
        tableView.setPlaceholder(emptytableLabel);
        tableView.setRowFactory( tv -> {
            TableRow<Part> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Part rowData = row.getItem();
                    showInfoPage(rowData);
                    System.out.println("Hi, " + rowData.toString());
                }
            });
            return row ;
        });
    }

    /**
     * Sets the values for each table column, empties the current table, then calls selectParts to populate it.
     * @author Matthew Karcz
     */
    @FXML
    public void populateTable() {
        this.data.clear();
        database = new Database();
        this.data = selectParts("SELECT * from parts WHERE isDeleted = 0 AND isFaulty = 1 ORDER BY partID", this.data);

        //Add student ID to faults
        partName.setCellValueFactory(new PropertyValueFactory("partName"));
        serialNumber.setCellValueFactory(new PropertyValueFactory("serialNumber"));
        location.setCellValueFactory(new PropertyValueFactory("location"));
        barcode.setCellValueFactory(new PropertyValueFactory("barcode"));
        faultDesc.setCellValueFactory(new PropertyValueFactory("faultDesc"));
        partID.setCellValueFactory(new PropertyValueFactory("partID"));

        this.tableView.getItems().clear();
        this.tableView.getItems().setAll(this.data);
    }

    /**
     * This method brings up the FXML page for showing the info about the selected part
     *
     * @author Matthew Karcz
     */
    public void showInfoPage(Part part){
        Stage stage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowPart.fxml"));
            Parent root = loader.load();
            ((ControllerShowPart) loader.getController()).initPart(database.selectPart(part.getPartID()), "fault");
            Scene scene = new Scene(root, 400, 400);
            stage.setTitle("Part Information");
            stage.initOwner(faultyPage.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("msoe.png"));
            stage.showAndWait();
            populateTable();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
