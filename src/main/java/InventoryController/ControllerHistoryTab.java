package InventoryController;

import Database.*;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * This class acts as the controller for the history tab of the inventory page
 */
public class ControllerHistoryTab  extends ControllerInventoryPage implements Initializable {

    ObservableList<HistoryTabTableRow> tableRows;

    @FXML
    private JFXTreeTableView<HistoryTabTableRow> historyTable;

    @FXML
    private JFXTextField searchInput;

    //private HistoryItems historyItems = new HistoryItems();
    private HistoryParts historyParts;

    private JFXTreeTableColumn<HistoryTabTableRow, String> studentCol;
    private JFXTreeTableColumn<HistoryTabTableRow, String> partNameCol;
    private JFXTreeTableColumn<HistoryTabTableRow, String> serialNumberCol;
    private JFXTreeTableColumn<HistoryTabTableRow, String> locationCol;
    private JFXTreeTableColumn<HistoryTabTableRow, String> quantityCol;
    private JFXTreeTableColumn<HistoryTabTableRow, String> dateCol;

    private String student;
    private String partName;
    private String serialNumber;
    private String loc;
    private String quantity;
    private String date;

    /**
     * This method sets the data in the history page.
     * @param location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        Label emptytableLabel = new Label("No parts found.");
//        emptytableLabel.setFont(new Font(18));
//        historyTable.setPlaceholder(emptytableLabel);
//        populateTable();


        studentCol = new JFXTreeTableColumn<>("Student");
        studentCol.setPrefWidth(150);
        studentCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String> param) {
                return param.getValue().getValue().getStudent();
            }
        });

        partNameCol = new JFXTreeTableColumn<>("Part");
        partNameCol.setPrefWidth(150);
        partNameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String> param) {
                return param.getValue().getValue().getPartName();
            }
        });

        serialNumberCol = new JFXTreeTableColumn<>("Serial Number");
        serialNumberCol.setPrefWidth(100);
        serialNumberCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String> param) {
                return param.getValue().getValue().getSerialNumber();
            }
        });

        locationCol = new JFXTreeTableColumn<>("Location");
        locationCol.setPrefWidth(150);
        locationCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String> param) {
                return param.getValue().getValue().getLocation();
            }
        });

        quantityCol = new JFXTreeTableColumn<>("Quantity");
        quantityCol.setPrefWidth(100);
        quantityCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String> param) {
                return param.getValue().getValue().getQuantity();
            }
        });

        dateCol = new JFXTreeTableColumn<>("Date");
        dateCol.setPrefWidth(150);
        dateCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String> param) {
                return param.getValue().getValue().getDate();
            }
        });

        tableRows = FXCollections.observableArrayList();

        searchInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                historyTable.setPredicate(new Predicate<TreeItem<HistoryTabTableRow>>() {
                    @Override
                    public boolean test(TreeItem<HistoryTabTableRow> tableRow) {
                        String input = newValue.toLowerCase();
                        student = tableRow.getValue().getStudent().getValue();
                        partName = tableRow.getValue().getPartName().getValue();
                        serialNumber = tableRow.getValue().getSerialNumber().getValue();
                        loc = tableRow.getValue().getLocation().getValue();
                        quantity = tableRow.getValue().getQuantity().getValue();
                        date = tableRow.getValue().getDate().getValue().toLowerCase();

                        return ((student != null && student.toLowerCase().contains(input))
                            || (partName != null && partName.toLowerCase().contains(input))
                            || (serialNumber != null && serialNumber.toLowerCase().contains(input))
                            || (loc != null && loc.toLowerCase().contains(input))
                            || (quantity != null && quantity.toLowerCase().contains(input))
                            || (date != null & date.toLowerCase().contains(input)));
                    }
                });
            }
        });

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
        tableRows = FXCollections.observableArrayList();

        for (int i = 0; i < list.size(); i++) {
            tableRows.add(new HistoryTabTableRow(list.get(i).getStudent(),
                    list.get(i).getPartName(), list.get(i).getSerialNumber(),
                    list.get(i).getLocation(), "" + list.get(i).getQuantity(),
                    list.get(i).getDate()));
        }

        final TreeItem<HistoryTabTableRow> root = new RecursiveTreeItem<HistoryTabTableRow>(tableRows, RecursiveTreeObject::getChildren);
        historyTable.getColumns().setAll(studentCol, partNameCol, serialNumberCol, locationCol, quantityCol, dateCol);
        historyTable.setRoot(root);
        historyTable.setShowRoot(false);
    }
}
