package InventoryController;

import Database.Database;
import Database.Part;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerTotalTab extends ControllerInventoryPage implements Initializable {

//    @FXML
//    private TextField searchTotal;

    @FXML
    public AnchorPane totalTabPage;

    @FXML
    private Button add, remove, refresh;

    @FXML
    private TableView<Part> tableView;

    @FXML
    private TableColumn<Part, String> partName, serialNumber, location,
            barcode, partID;
    @FXML
    private TableColumn<Part, Boolean> fault;

    private static ObservableList<Part> data
            = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = new Database();
        populateTable();
    }

    /**
     * Sets the values for each table column, empties the current table, then calls selectParts to populate it.
     * @author Matthew Karcz
     */
    @FXML
    private void populateTable() {
        this.data.clear();
        this.data = selectParts("SELECT * from parts WHERE isDeleted = 0 ORDER BY partID", this.data);

        partName.setCellValueFactory(new PropertyValueFactory("partName"));
        serialNumber.setCellValueFactory(new PropertyValueFactory("serialNumber"));
        location.setCellValueFactory(new PropertyValueFactory("location"));
        barcode.setCellValueFactory(new PropertyValueFactory("barcode"));
        partID.setCellValueFactory(new PropertyValueFactory("partID"));
        fault.setCellFactory(CheckBoxTableCell.forTableColumn(fault));
        fault.setCellValueFactory(new PropertyValueFactory("fault"));
        tableView.getItems().clear();
        tableView.getItems().setAll(this.data);

        tableView.setRowFactory(tv -> {
            TableRow<Part> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Part rowData = row.getItem();
                    editPart(rowData);
                }
            });
            return row;
        });
    }

    /**
     * Called to bring up the "AddPart" FXML scene.
     */
    @FXML
    public void addPart() {
        Stage stage = new Stage();
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("AddPart.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            Parent root = loader.load(myFxmlURL);
            Scene scene = new Scene(root, 400, 400);
            stage.setTitle("Add a Part");
            stage.initOwner(totalTabPage.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when a part is double-clicked in the table. Brings up the EditPart FXML scene
     * @param part the part that was double clicked
     */
    @FXML
    public void editPart(Part part) {
        //Called when a part is double clicked in a table.
        //@param part the part that was double clicked
        Stage stage = new Stage();
        try {
            //URL myFxmlURL = ClassLoader.getSystemResource("EditPart.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditPart.fxml"));
            Parent root = loader.load();

            ((ControllerEditPart) loader.getController()).initPart(part);
            Scene scene = new Scene(root, 400, 400);
            stage.setTitle("Edit a Part");
            stage.initOwner(totalTabPage.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            populateTable();
        }

    }

    /**
     * This method calls the database method to soft delete an item from the inventory list
     * this then updates the gui table
     *
     * @author Bailey Terry
     */
    @FXML
    public void removePart() {
        if (tableView.getSelectionModel().getSelectedItems().size() == 1) {
            database.deleteItem(tableView.getSelectionModel().getSelectedItem().getPartID());
        }
        tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem());
    }

//    @FXML
//    public void search(){
//        System.out.println(searchTotal.getText());
//    }
}