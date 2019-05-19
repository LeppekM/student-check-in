package InventoryController;

import Database.*;
import Database.ObjectClasses.Worker;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * This class acts as the controller for the history tab of the inventory page
 */
public class ControllerHistoryTab  extends ControllerInventoryPage implements Initializable {

    private Worker worker;

    @FXML
    private VBox inventoryHistoryPage;

    private ObservableList<HistoryTabTableRow> tableRows;

    @FXML
    private JFXTreeTableView<HistoryTabTableRow> historyTable;

    @FXML
    private JFXTextField searchInput;

    //private HistoryItems historyItems = new HistoryItems();
    private HistoryParts historyParts;

    private JFXTreeTableColumn<HistoryTabTableRow, String> studentCol, partNameCol,
    serialNumberCol, actionCol, dateCol;

    @FXML
    private JFXButton searchButton, clearOldHistory;

    private String student, partName, serialNumber, action, date;

    /**
     * This method sets the data in the history page.
     * @param location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label emptyTableLabel = new Label("No parts found.");
        emptyTableLabel.setStyle("-fx-text-fill: white");
        emptyTableLabel.setFont(new Font(18));
        historyTable.setPlaceholder(emptyTableLabel);


        studentCol = new JFXTreeTableColumn<>("Student");
        studentCol.prefWidthProperty().bind(historyTable.widthProperty().divide(5));
        studentCol.setStyle("-fx-font-size: 18px");
        studentCol.setResizable(false);
        studentCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String> param) {
                return param.getValue().getValue().getStudentName();
            }
        });

        partNameCol = new JFXTreeTableColumn<>("Part");
        partNameCol.prefWidthProperty().bind(historyTable.widthProperty().divide(5));
        partNameCol.setStyle("-fx-font-size: 18px");
        partNameCol.setResizable(false);
        partNameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String> param) {
                return param.getValue().getValue().getPartName();
            }
        });

        serialNumberCol = new JFXTreeTableColumn<>("Serial Number");
        serialNumberCol.prefWidthProperty().bind(historyTable.widthProperty().divide(5));
        serialNumberCol.setStyle("-fx-font-size: 18px");
        serialNumberCol.setResizable(false);
        serialNumberCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String> param) {
                return param.getValue().getValue().getSerialNumber();
            }
        });

        actionCol = new JFXTreeTableColumn<>("Action");
        actionCol.prefWidthProperty().bind(historyTable.widthProperty().divide(5));
        actionCol.setStyle("-fx-font-size: 18px");
        actionCol.setResizable(false);
        actionCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String> param) {
                return param.getValue().getValue().getAction();
            }
        });

        dateCol = new JFXTreeTableColumn<>("Date");
        dateCol.prefWidthProperty().bind(historyTable.widthProperty().divide(5));
        dateCol.setStyle("-fx-font-size: 18px");
        dateCol.setResizable(false);
        dateCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<HistoryTabTableRow, String> param) {
                return param.getValue().getValue().getDate();
            }
        });

        tableRows = FXCollections.observableArrayList();

        // sets that the search method will be called every time the user types in the search field
        searchInput.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                search();
            }
        });

        // Click to select if unselected and deselect if selected; view part on double click
        historyTable.setRowFactory(new Callback<TreeTableView<HistoryTabTableRow>, TreeTableRow<HistoryTabTableRow>>() {
            @Override
            public TreeTableRow<HistoryTabTableRow> call(TreeTableView<HistoryTabTableRow> param) {
                final TreeTableRow<HistoryTabTableRow> row = new TreeTableRow<>();
                row.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == 2) {
                            viewPart(row.getIndex());
                        } else {
                            final int index = row.getIndex();
                            if (index >= 0 && index < historyTable.getCurrentItemsCount() && historyTable.getSelectionModel().isSelected(index)) {
                                historyTable.getSelectionModel().clearSelection();
                                event.consume();
                            }
                        }
                    }
                });
                return row;
            }
        });
    }

    /**
     *
     */
    public void importTransaction(){
        historyParts = new HistoryParts();
        ObservableList<HistoryItems> list = historyParts.getHistoryItems();
        export.exportTransactionHistory(list);
    }

    /**
     * This method adds content to the table.
     */
    public void populateTable() {
        historyParts = new HistoryParts();
        ObservableList<HistoryItems> list = historyParts.getHistoryItems();
        tableRows = FXCollections.observableArrayList();

        for (int i = 0; i < list.size(); i++) {
            tableRows.add(new HistoryTabTableRow(list.get(i).getStudentName(),
                    list.get(i).getStudentEmail(), list.get(i).getPartName(),
                    list.get(i).getSerialNumber(), list.get(i).getAction(),
                    list.get(i).getDate()));
        }

        final TreeItem<HistoryTabTableRow> root = new RecursiveTreeItem<HistoryTabTableRow>(tableRows, RecursiveTreeObject::getChildren);
        historyTable.getColumns().setAll(studentCol, partNameCol, serialNumberCol, actionCol, dateCol);
        historyTable.setRoot(root);
        historyTable.setShowRoot(false);
    }

    /**
     * This method checks whether the search field's input matches each row in the table and only
     * displays the matching rows.
     */
    @FXML
    private void search() {
        historyTable.setPredicate(new Predicate<TreeItem<HistoryTabTableRow>>() {
            @Override
            public boolean test(TreeItem<HistoryTabTableRow> tableRow) {
                String input = searchInput.getText().toLowerCase();
                student = tableRow.getValue().getStudentName().getValue();
                partName = tableRow.getValue().getPartName().getValue();
                serialNumber = tableRow.getValue().getSerialNumber().getValue();
                action = tableRow.getValue().getAction().getValue();
                date = tableRow.getValue().getDate().getValue().toLowerCase();

                return ((student != null && student.toLowerCase().contains(input))
                        || (partName != null && partName.toLowerCase().contains(input))
                        || (serialNumber != null && serialNumber.toLowerCase().contains(input))
                        || (action != null && action.toLowerCase().contains(input))
                        || (date != null & date.toLowerCase().contains(input)));
            }
        });
    }

    /**
     * This method confirms that the worker is an admin, in which case it calls the method in the database to
     * clear the checkout table to remove transactions older than 2 years old.
     */
    public void clearOldHistory() {
        if (confirmDeleteOldHistory()) {
            database.clearOldHistory();
            populateTable();
        }
    }

    /**
     * Asks whether the user really wants to clear the 2-year-old history.
     * @return true if yes; false otherwise
     */
    public boolean confirmDeleteOldHistory() {
        if (this.worker != null && this.worker.isAdmin()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete Old History");
            alert.setContentText("Are you sure you want to clear the transaction history for non-faulty parts older than 2 years?");
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);
            Optional<ButtonType> result = alert.showAndWait();
            return result.get() == ButtonType.YES;
        }
        return false;
    }

    /**
     * This part brings up a pop up to show info about the part and it's last transaction
     * @param index
     */
    private void viewPart(int index) {
        Stage stage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewHistoryPart.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("View Transaction");
            stage.initOwner(inventoryHistoryPage.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            if (index != -1) {
                TreeItem item = historyTable.getSelectionModel().getModelItem(index);
                // null if user clicks on empty row
                if (item != null) {
                    HistoryTabTableRow row = ((HistoryTabTableRow) item.getValue());
                    ((ControllerViewHistoryPart) loader.getController()).populate(row);
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method adds the current worker to the class, so that the class can confirm whether the
     * current worker is an administrator if the worker tries to clear the history.
     * @param worker the currently logged in worker
     */
    @Override
    public void initWorker(Worker worker) {
        if (this.worker == null) {
            this.worker = worker;
            if (this.worker.isAdmin()) {
                clearOldHistory.setDisable(false);
            }
        }
    }

}
