package InventoryController;

import Database.HistoryItems;
import Database.HistoryParts;
import Database.Part;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerHistoryTab  extends ControllerInventoryPage implements Initializable {

    @FXML
    private TableView historyTable;

    @FXML
    private TableColumn<HistoryItems, Integer> quantity;

    @FXML
    private TableColumn<HistoryItems,String> studentName, partName, serialNumber, location, date;

    //private HistoryItems historyItems = new HistoryItems();
    private HistoryParts historyParts = new HistoryParts();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<HistoryItems> list = historyParts.getHistoryItems();
        populateTable(list);
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

    private void populateTable(ObservableList<HistoryItems> list) {

//        studentName.setCellValueFactory(new PropertyValueFactory("studentName"));
//        partName.setCellValueFactory(new PropertyValueFactory("partName"));
//        serialNumber.setCellValueFactory(new PropertyValueFactory("serialNumber"));
//        location.setCellValueFactory(new PropertyValueFactory("location"));
//        quantity.setCellValueFactory(new PropertyValueFactory("quantity"));
//        date.setCellValueFactory(new PropertyValueFactory("date"));

        historyTable.getItems().clear();
        historyTable.getColumns().clear();
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
        return column;
    }

    public void search() {

    }
}
