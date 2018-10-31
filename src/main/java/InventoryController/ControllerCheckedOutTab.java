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
import java.util.ResourceBundle;

public class ControllerCheckedOutTab  extends ControllerInventoryPage implements Initializable {

    @FXML
    private TableView<CheckedOutList> checkedOutItems;

    @FXML
    private TableColumn<CheckedOutList, Integer> quantityCol;

    @FXML
    private TableColumn<CheckedOutList, String> partNameCol, dueDateCol, sNameCol, checkOutAtCol;

    private CheckedOutParts checkedOutParts = new CheckedOutParts();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkedOutParts.getCheckedOutItems();
        populateTable(checkedOutParts.data);
    }


    public void populateTable(ObservableList<CheckedOutList> data){
        sNameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        partNameCol.setCellValueFactory(new PropertyValueFactory<>("partName"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        checkOutAtCol.setCellValueFactory(new PropertyValueFactory<>("checkedOutAt"));
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        checkedOutItems.setItems(data);
    }

}
