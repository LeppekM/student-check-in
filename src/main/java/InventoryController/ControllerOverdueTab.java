package InventoryController;

import Database.Database;
import Database.OverdueItem;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class ControllerOverdueTab extends ControllerInventoryPage implements Initializable {

    @FXML
    private AnchorPane overduePage;

    @FXML
    JFXTreeTableView<OverdueTabTableRow> overdueTable;

    @FXML
    ObservableList<OverdueTabTableRow> tableRows;

    private TreeItem<OverdueTabTableRow> root;

    @FXML
    private JFXTextField searchInput;

    @FXML
    JFXTreeTableColumn<OverdueTabTableRow, String> studentIDCol, partNameCol, serialNumberCol,
            dueDateCol, feeCol;

    private String studentID, partName, serialNumber, dueDate, fee;

    private Database database;
    private ObservableList<OverdueItem> list = FXCollections.observableArrayList();

    /**
     * This method puts all overdue items into the list for populating the gui table
     *
     * @param location
     * @param resources
     * @author Bailey Terry
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label emptyTableLabel = new Label("No parts found.");
        emptyTableLabel.setFont(new Font(18));
        overdueTable.setPlaceholder(emptyTableLabel);

        studentIDCol = new JFXTreeTableColumn<>("Student ID");
        studentIDCol.setPrefWidth(150);
        studentIDCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String> param) {
                return param.getValue().getValue().getStudentID();
            }
        });

        partNameCol = new JFXTreeTableColumn<>("Part Name");
        partNameCol.setPrefWidth(150);
        partNameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String> param) {
                return param.getValue().getValue().getPartName();
            }
        });

        serialNumberCol = new JFXTreeTableColumn<>("Serial Number");
        serialNumberCol.setPrefWidth(150);
        serialNumberCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String> param) {
                return param.getValue().getValue().getSerialNumber();
            }
        });

        dueDateCol = new JFXTreeTableColumn<>("Due Date");
        dueDateCol.setPrefWidth(150);
        dueDateCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String> param) {
                return param.getValue().getValue().getDueDate();
            }
        });

        feeCol = new JFXTreeTableColumn<>("Fee");
        feeCol.setPrefWidth(150);
        feeCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String> param) {
                return param.getValue().getValue().getFee();
            }
        });

        tableRows = FXCollections.observableArrayList();

        searchInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                overdueTable.setPredicate(new Predicate<TreeItem<OverdueTabTableRow>>() {
                    @Override
                    public boolean test(TreeItem<OverdueTabTableRow> tableRow) {
                        String input = newValue.toLowerCase();
                        studentID = tableRow.getValue().getStudentID().getValue();
                        partName = tableRow.getValue().getPartName().getValue();
                        serialNumber = tableRow.getValue().getSerialNumber().getValue();
                        dueDate = tableRow.getValue().getDueDate().getValue();
                        fee = tableRow.getValue().getFee().getValue();

                        return ((studentID != null && studentID.toLowerCase().contains(input))
                                || (partName != null && partName.toLowerCase().contains(input))
                                || (serialNumber != null && serialNumber.toLowerCase().contains(input))
                                || (dueDate != null && dueDate.toLowerCase().contains(input))
                                || (fee != null && fee.toLowerCase().contains(input)));
                    }
                });
            }
        });

        // Click to select if unselected and deselect if selected
        overdueTable.setRowFactory(new Callback<TreeTableView<OverdueTabTableRow>, TreeTableRow<OverdueTabTableRow>>() {
            @Override
            public TreeTableRow<OverdueTabTableRow> call(TreeTableView<OverdueTabTableRow> param) {
                final TreeTableRow<OverdueTabTableRow> row = new TreeTableRow<>();
                row.addEventFilter(MouseEvent.MOUSE_PRESSED, (EventHandler<MouseEvent>) event -> {
                    final int index = row.getIndex();
                    if (index >= 0 && index < overdueTable.getCurrentItemsCount() && overdueTable.getSelectionModel().isSelected(index)) {
                        overdueTable.getSelectionModel().clearSelection();
                        event.consume();
                    }
                });
                return row;
            }
        });
    }

    /**
     * Creates an informational pop up on double click
     *
     * @author Bailey Terry
     */
    public void popUp(MouseEvent event) {
//        if (event.getClickCount() == 2) {
//            Stage stage = new Stage();
//            try {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OverduePopup.fxml"));
//                Parent root = loader.load();
//                Scene scene = new Scene(root, 400, 400);
//                stage.setTitle("Overdue Item");
//                stage.initOwner(overduePage.getScene().getWindow());
//                stage.setScene(scene);
//                ((OverduePopUp) loader.getController()).populate(
//                        ((CheckedOutItems)overdueTable.getSelectionModel().getSelectedItem())));
//                stage.getIcons().add(new Image("images/msoe.png"));
//                stage.show();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        populateTable();
    }

    /**
     * This method populates the gui based off of the data in the Observable list
     */
    public void populateTable() {
        tableRows.clear();
        overdueTable.getColumns().clear();
        list.clear();
        database = new Database();
        list = database.getOverdue();
        DecimalFormat df = new DecimalFormat("#,###,##0.00");

        for (int i = 0; i < list.size(); i++) {
            tableRows.add(new OverdueTabTableRow("" + list.get(i).getID().getValue(),
                    list.get(i).getPart().getValue(), list.get(i).getSerial().getValue(),
                    list.get(i).getDate().getValue(), "$" +
                    df.format(Double.parseDouble(list.get(i).getPrice().getValue()))));
        }

        root = new RecursiveTreeItem<OverdueTabTableRow>(
                tableRows, RecursiveTreeObject::getChildren
        );

        overdueTable.getColumns().setAll(studentIDCol, partNameCol, serialNumberCol, dueDateCol, feeCol);
        overdueTable.setRoot(root);
        overdueTable.setShowRoot(false);
    }
}
