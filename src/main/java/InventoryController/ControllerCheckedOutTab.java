package InventoryController;

import Database.CheckedOutParts;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class acts as the controller for the checked out items part of the inventory tab
 */
public class ControllerCheckedOutTab  extends ControllerInventoryPage implements Initializable {

    @FXML
    private TableView checkedOutTable;

    @FXML
    private TableColumn<CheckedOutItems, Integer> quantityCol;

    @FXML
    private TableColumn<CheckedOutItems, String> partNameCol, dueDateCol, sNameCol, checkOutAtCol;

    private CheckedOutParts checkedOutParts;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label emptytableLabel = new Label("No parts found.");
        emptytableLabel.setFont(new Font(18));
        checkedOutTable.setPlaceholder(emptytableLabel);
    }

    /**
     * This sets each column table to the corresponding field in the CheckedOutItems class, and then populates it.
     */
    public void populateTable(){
        checkedOutParts = new CheckedOutParts();
        ObservableList<CheckedOutItems> list = checkedOutParts.getCheckedOutItems(); //Queries database, populating the Observable Arraylist in that class
        checkedOutTable.getItems().clear();
        checkedOutTable.getColumns().clear();

        // SET COLUMN WIDTH HERE (TOTAL = 800)
        checkedOutTable.getColumns().add(createColumn(0, "Student"));
        checkedOutTable.getColumns().add(createColumn(1, "Part Name"));
        checkedOutTable.getColumns().add(createColumn(4, "Quantity"));
        checkedOutTable.getColumns().add(createColumn(2, "CheckedOutAt"));
        checkedOutTable.getColumns().add(createColumn(5, "Date"));

        for (int i = 0; i < list.size(); i++) {
            for (int columnIndex = checkedOutTable.getColumns().size(); columnIndex < list.size(); columnIndex++) {
                checkedOutTable.getColumns().add(createColumn(columnIndex, ""));
            }
            ObservableList<StringProperty> data = FXCollections.observableArrayList();
            data.add(new SimpleStringProperty(list.get(i).getStudentName()));
            data.add(new SimpleStringProperty(list.get(i).getPartName()));
            data.add(new SimpleStringProperty("" + list.get(i).getQuantity()));
            data.add(new SimpleStringProperty(list.get(i).getCheckedOutAt()));
            data.add(new SimpleStringProperty(list.get(i).getDueDate()));
            checkedOutTable.getItems().add(data);
        }

//        ObservableList<CheckedOutItems> asd = checkedOutParts.data;
//        sNameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
//        partNameCol.setCellValueFactory(new PropertyValueFactory<>("partName"));
//        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
//        checkOutAtCol.setCellValueFactory(new PropertyValueFactory<>("checkedOutAt"));
//        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
//        checkedOutTable.setItems(checkedOutParts.data);

    }

    /**
     * This method creates a column with the correct format for the table
     * @param columnIndex
     * @param columnTitle
     * @return
     */
    private TableColumn<ObservableList<StringProperty>, String> createColumn(
            final int columnIndex, String columnTitle) {
        TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();
        column.setPrefWidth(150);
        String title;
        if (columnTitle == null || columnTitle.trim().length() == 0) {
            title = "Column " + (columnIndex + 1);  // DELETE??
        } else {
            title = columnTitle;
        }
        column.setText(title);
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<StringProperty>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(
                    TableColumn.CellDataFeatures<ObservableList<StringProperty>, String> cellDataFeatures) {
                ObservableList<StringProperty> values = cellDataFeatures.getValue();
                if (columnIndex >= values.size()) {
                    return new SimpleStringProperty("");
                } else {
                    return cellDataFeatures.getValue().get(columnIndex);
                }
            }
        });
        // width of column set to width of table / number of columns
        column.setPrefWidth(800 / 6);
        return column;
    }
}