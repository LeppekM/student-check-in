package InventoryController;

import Database.Database;
import Database.Part;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerTotalTab  extends ControllerInventoryPage implements Initializable {

//    @FXML
//    private TextField searchTotal;

    @FXML
    public AnchorPane totalTabPage;

    @FXML
    private Button add, remove, refresh;

    @FXML
    private TableView<Part> tableView;

    @FXML
    private TableColumn<Part,String> partName, serialNumber, manufacturer, price, vendor, location,
            barcode, fault, partID;

    private static ObservableList<Part> data
            = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = new Database();
        populateTable();
    }

    /*
     * Sets the values for each table column, empties the current table, then calls selectParts to populate it.
     */
    @FXML
    private void populateTable() {
        partName.setCellValueFactory(new PropertyValueFactory("partName"));
        serialNumber.setCellValueFactory(new PropertyValueFactory("serialNumber"));
        manufacturer.setCellValueFactory(new PropertyValueFactory("manufacturer"));
        price.setCellValueFactory(new PropertyValueFactory("price"));
        vendor.setCellValueFactory(new PropertyValueFactory("vendor"));
        location.setCellValueFactory(new PropertyValueFactory("location"));
        barcode.setCellValueFactory(new PropertyValueFactory("barcode"));
        fault.setCellValueFactory(new PropertyValueFactory("fault"));
        partID.setCellValueFactory(new PropertyValueFactory("partID"));

        this.data.clear();
        tableView.getItems().clear();

        this.data = selectParts("SELECT * from parts WHERE isDeleted = 0 ORDER BY partID", this.data);

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

    /*
     * Called to bring up the "AddPart" FXML scene.
     */
    @FXML
    public void addPart(){
        Stage stage = new Stage();
        try{
            URL myFxmlURL = ClassLoader.getSystemResource("AddPart.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            Parent root = loader.load(myFxmlURL);
            Scene scene = new Scene(root, 400, 400);
            stage.setTitle("Add a Part");
            stage.initOwner(totalTabPage.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e){
            e.printStackTrace();
        }
        //Called when the "Add" button is clicked
    }

    /*
     * Called when a part is double-clicked in the table. Brings up the EditPart FXML scene
     * @param part the part that was double clicked
     */
    @FXML
    public void editPart(Part part){
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
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            populateTable();
        }

    }

    /*
     * Called to delete the currently selected part in the table if one is selected.
     */
    @FXML
    public void removePart(){
        if(tableView.getSelectionModel().getSelectedItems().size() == 1){
            database.deleteItem(tableView.getSelectionModel().getSelectedItem().getPartID());
        }
        tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem());
    }

//    @FXML
//    public void search(){
//        System.out.println(searchTotal.getText());
//    }
}