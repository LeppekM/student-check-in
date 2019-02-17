package InventoryController;

import Database.*;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * This class acts as the controller for the history tab of the inventory page
 */
public class ControllerHistoryTab  extends ControllerInventoryPage implements Initializable {

    private ObservableList<HistoryTabTableRow> tableRows;

    @FXML
    private JFXTreeTableView<HistoryTabTableRow> historyTable;

    @FXML
    private JFXTextField searchInput;

    //private HistoryItems historyItems = new HistoryItems();
    private HistoryParts historyParts;

    private JFXTreeTableColumn<HistoryTabTableRow, String> studentCol, partNameCol,
    serialNumberCol, statusCol, dateCol;

    @FXML
    private JFXButton searchButton;

    private String student, partName, serialNumber, status, date;

    /**
     * This method sets the data in the history page.
     * @param location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label emptyTableLabel = new Label("No parts found.");
        emptyTableLabel.setFont(new Font(18));
        searchButton.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15pt; -fx-border-radius: 15pt; -fx-border-color: #043993; -fx-text-fill: #000000;");
        historyTable.setPlaceholder(emptyTableLabel);
//        populateTable();


        studentCol = new JFXTreeTableColumn<>("Student");
        studentCol.setPrefWidth(150);
        studentCol.setResizable(false);
        studentCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String> param) {
                return param.getValue().getValue().getStudent();
            }
        });

        partNameCol = new JFXTreeTableColumn<>("Part");
        partNameCol.setPrefWidth(200);
        partNameCol.setResizable(false);
        partNameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String> param) {
                return param.getValue().getValue().getPartName();
            }
        });

        serialNumberCol = new JFXTreeTableColumn<>("Serial Number");
        serialNumberCol.setPrefWidth(150);
        serialNumberCol.setResizable(false);
        serialNumberCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String> param) {
                return param.getValue().getValue().getSerialNumber();
            }
        });

        statusCol = new JFXTreeTableColumn<>("Status");
        statusCol.setPrefWidth(150);
        statusCol.setResizable(false);
        statusCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String> param) {
                return param.getValue().getValue().getStatus();
            }
        });

        dateCol = new JFXTreeTableColumn<>("Date");
        dateCol.setPrefWidth(150);
        dateCol.setResizable(false);
        dateCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String> param) {
                return param.getValue().getValue().getDate();
            }
        });

        tableRows = FXCollections.observableArrayList();

        searchInput.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                search();
            }
        });

        // Click to select if unselected and deselect if selected
        historyTable.setRowFactory(new Callback<TreeTableView<HistoryTabTableRow>, TreeTableRow<HistoryTabTableRow>>() {
            @Override
            public TreeTableRow<HistoryTabTableRow> call(TreeTableView<HistoryTabTableRow> param) {
                final TreeTableRow<HistoryTabTableRow> row = new TreeTableRow<>();
                row.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        final int index = row.getIndex();
                        if (index >= 0 && index < historyTable.getCurrentItemsCount() && historyTable.getSelectionModel().isSelected(index)) {
                            historyTable.getSelectionModel().clearSelection();
                            event.consume();
                        }
                    }
                });
                return row;
            }
        });
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
                    list.get(i).getStatus(), list.get(i).getDate()));
        }

        final TreeItem<HistoryTabTableRow> root = new RecursiveTreeItem<HistoryTabTableRow>(tableRows, RecursiveTreeObject::getChildren);
        historyTable.getColumns().setAll(studentCol, partNameCol, serialNumberCol, statusCol, dateCol);
        historyTable.setRoot(root);
        historyTable.setShowRoot(false);
    }

    @FXML
    private void search() {
        historyTable.setPredicate(new Predicate<TreeItem<HistoryTabTableRow>>() {
            @Override
            public boolean test(TreeItem<HistoryTabTableRow> tableRow) {
                String input = searchInput.getText().toLowerCase();
                student = tableRow.getValue().getStudent().getValue();
                partName = tableRow.getValue().getPartName().getValue();
                serialNumber = tableRow.getValue().getSerialNumber().getValue();
                status = tableRow.getValue().getStatus().getValue();
                date = tableRow.getValue().getDate().getValue().toLowerCase();

                return ((student != null && student.toLowerCase().contains(input))
                        || (partName != null && partName.toLowerCase().contains(input))
                        || (serialNumber != null && serialNumber.toLowerCase().contains(input))
                        || (status != null && status.toLowerCase().contains(input))
                        || (date != null & date.toLowerCase().contains(input)));
            }
        });
    }

}
