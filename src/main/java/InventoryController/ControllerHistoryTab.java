package InventoryController;

import Database.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class acts as the controller for the history tab of the inventory page
 */
public class ControllerHistoryTab  extends ControllerInventoryPage implements Initializable {

    @FXML
    private TableView historyTable;

    @FXML
    private TableColumn<HistoryItems, Integer> quantity;

    @FXML
    private TableColumn<HistoryItems,String> studentName, partName, serialNumber, location, date;

    //private HistoryItems historyItems = new HistoryItems();
    private HistoryParts historyParts;

    /**
     * This method sets the data in the history page.
     * @param location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label emptytableLabel = new Label("No parts found.");
        emptytableLabel.setFont(new Font(18));
        historyTable.setPlaceholder(emptytableLabel);
//        populateTable();
//        historyTable.setRowFactory(tv -> {
//            TableRow<Part> row = new TableRow<>();
//            row.setOnMouseClicked(event -> {
//                if (event.getClickCount() == 2 && (!row.isEmpty())) {
//                    Part rowData = row.getItem();
//                    editPart(rowData);
//                }
//            });
//            return row;
//        });
        //database.getHistory();
    }

    /**
     * This method adds content to the table.
     */
    public void populateTable() {
        historyParts = new HistoryParts();
        ObservableList<HistoryItems> list = historyParts.getHistoryItems();
        historyTable.getItems().clear();
        historyTable.getColumns().clear();

        // SET COLUMN WIDTH HERE (TOTAL = 800)
        historyTable.getColumns().add(createColumn(0, "Student"));
        historyTable.getColumns().add(createColumn(1, "Part Name"));
        historyTable.getColumns().add(createColumn(2, "Serial Number"));
        historyTable.getColumns().add(createColumn(3, "Location"));
        historyTable.getColumns().add(createColumn(4, "Quantity"));
        historyTable.getColumns().add(createColumn(5, "Date"));

        for (int i = 0; i < list.size(); i++) {
            for (int columnIndex = historyTable.getColumns().size(); columnIndex < list.size(); columnIndex++) {
                historyTable.getColumns().add(createColumn(columnIndex, ""));
            }
            ObservableList<StringProperty> data = FXCollections.observableArrayList();
            data.add(new SimpleStringProperty(list.get(i).getStudent()));
            data.add(new SimpleStringProperty(list.get(i).getPartName()));
            data.add(new SimpleStringProperty(list.get(i).getSerialNumber()));
            data.add(new SimpleStringProperty(list.get(i).getLocation()));
            data.add(new SimpleStringProperty("" + list.get(i).getQuantity()));
            data.add(new SimpleStringProperty(list.get(i).getDate()));
            historyTable.getItems().add(data);
        }
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

//    public void search() {
//
//    }
}
