package InventoryController;

import Database.CheckedOutParts;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class acts as the controller for the checked out items part of the inventory tab
 */
public class ControllerCheckedOutTab  extends ControllerInventoryPage implements Initializable {

    @FXML
    private TableView<CheckedOutItems> checkedOutItems;

    @FXML
    private TableColumn<CheckedOutItems, Integer> quantityCol;

    @FXML
    private TableColumn<CheckedOutItems, String> partNameCol, dueDateCol, sNameCol, checkOutAtCol;

    private CheckedOutParts checkedOutParts = new CheckedOutParts();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkedOutParts.getCheckedOutItems(); //Queries database, populating the Observable Arraylist in that class
        populateTable(checkedOutParts.data);
    }

    /**
     * This sets each column table to the corresponding field in the CheckedOutItems class, and then populates it.
     * @param data An observable arraylist containing CheckedOutItems Objects.
     */
    public void populateTable(ObservableList<CheckedOutItems> data){
        sNameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        partNameCol.setCellValueFactory(new PropertyValueFactory<>("partName"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        checkOutAtCol.setCellValueFactory(new PropertyValueFactory<>("checkedOutAt"));
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        checkedOutItems.setItems(data);
    }
}