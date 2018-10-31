package InventoryController;

import Database.CheckedOutParts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class ControllerCheckedOutTab  extends ControllerInventoryPage implements Initializable {

    @FXML
    private TableView<CheckedOutList> checkedOutItems;

    @FXML
    private TableColumn<CheckedOutList, Integer> partIDCol, quantityCol;

    @FXML
    private TableColumn<CheckedOutList, String> partNameCol;

    @FXML
    private TableColumn<CheckedOutList, String> dueDateCol;

    private CheckedOutParts checkedOutParts = new CheckedOutParts();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTable();

    }


    public void populateTable(){

        checkedOutParts.getCheckedOutItems();
        //System.out.println(checkedOutParts.data.get(0).getPartID());

        partIDCol.setCellValueFactory(new PropertyValueFactory<>("partID"));
        partNameCol.setCellValueFactory(new PropertyValueFactory<>("partName"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        checkedOutItems.setItems(checkedOutParts.data);

    }



}
