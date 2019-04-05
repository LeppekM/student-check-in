package InventoryController;

import Database.CheckedOutParts;
import Database.ObjectClasses.Part;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * This class acts as the controller for the checked out items part of the inventory tab
 */
public class ControllerCheckedOutTab  extends ControllerInventoryPage implements Initializable {

    @FXML
    private VBox checkedOutPage;

    @FXML
    private ObservableList<CheckedOutTabTableRow> tableRows;

    @FXML
    private JFXTextField searchInput;

    @FXML
    private JFXTreeTableView<CheckedOutTabTableRow> checkedOutTable;

    private TreeItem<CheckedOutTabTableRow> root;

    @FXML
    private JFXTreeTableColumn<CheckedOutTabTableRow, String> studentNameCol, partNameCol,
            barcodeCol, checkedOutAtCol, dueDateCol;

    @FXML
    private JFXButton searchButton;

    private String studentName, partName, barcode, checkedOutAt, dueDate;

    private CheckedOutParts checkedOutParts;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label emptyTableLabel = new Label("No parts found.");
        emptyTableLabel.setFont(new Font(18));
        searchButton.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15pt; -fx-border-radius: 15pt; -fx-border-color: #043993; -fx-text-fill: #000000;");
        checkedOutTable.setPlaceholder(emptyTableLabel);

        studentNameCol = new JFXTreeTableColumn<>("Student");
        studentNameCol.prefWidthProperty().bind(checkedOutTable.widthProperty().divide(5));
        studentNameCol.setResizable(false);
        studentNameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String> param) {
                return param.getValue().getValue().getStudentName();
            }
        });

        partNameCol = new JFXTreeTableColumn<>("Part Name");
        partNameCol.prefWidthProperty().bind(checkedOutTable.widthProperty().divide(5));
        partNameCol.setResizable(false);
        partNameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String> param) {
                return param.getValue().getValue().getPartName();
            }
        });

        barcodeCol = new JFXTreeTableColumn<>("Barcode");
        barcodeCol.prefWidthProperty().bind(checkedOutTable.widthProperty().divide(5));
        barcodeCol.setResizable(false);
        barcodeCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String> param) {
                return param.getValue().getValue().getBarcode();
            }
        });

        checkedOutAtCol = new JFXTreeTableColumn<>("Check Out Date");
        checkedOutAtCol.prefWidthProperty().bind(checkedOutTable.widthProperty().divide(5));
        checkedOutAtCol.setResizable(false);
        checkedOutAtCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String> param) {
                return param.getValue().getValue().getCheckedOutAt();
            }
        });

        dueDateCol = new JFXTreeTableColumn<>("Due Date");
        dueDateCol.prefWidthProperty().bind(checkedOutTable.widthProperty().divide(5));
        dueDateCol.setResizable(false);
        dueDateCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String> param) {
                return param.getValue().getValue().getDueDate();
            }
        });

        tableRows = FXCollections.observableArrayList();

        // Click to select if unselected and deselect if selected
        checkedOutTable.setRowFactory(new Callback<TreeTableView<CheckedOutTabTableRow>, TreeTableRow<CheckedOutTabTableRow>>() {
            @Override
            public TreeTableRow<CheckedOutTabTableRow> call(TreeTableView<CheckedOutTabTableRow> param) {
                final TreeTableRow<CheckedOutTabTableRow> row = new TreeTableRow<>();
                row.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        final int index = row.getIndex();
                        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                            Part rowData = database.selectPart(checkedOutTable.getSelectionModel().getModelItem(index).getValue().getPartID().get());
                            if(!rowData.equals(null)) {
                                showInfoPage(rowData, "checkedOut");
                            }
                            checkedOutTable.getSelectionModel().clearSelection();
                            event.consume();
                        } else if (index >= 0 && index < checkedOutTable.getCurrentItemsCount() && checkedOutTable.getSelectionModel().isSelected(index)) {
                            checkedOutTable.getSelectionModel().clearSelection();
                            event.consume();
                        }
                    }
                });
                return row;
            }
        });
    }

    /**
     * This sets each column table to the corresponding field in the CheckedOutItems class, and then populates it.
     */
    public void populateTable(){
        tableRows.clear();
        checkedOutTable.getColumns().clear();
        checkedOutParts = new CheckedOutParts();
        ObservableList<CheckedOutItems> list = checkedOutParts.getCheckedOutItems(); //Queries database, populating the Observable Arraylist in that class

        for (int i = 0; i < list.size(); i++) {
            tableRows.add(new CheckedOutTabTableRow(list.get(i).getStudentName().getValue(),
                    list.get(i).getPartName().getValue(), "" + list.get(i).getBarcode().getValue(),
                    list.get(i).getCheckedOutAt().getValue(), list.get(i).getDueDate().getValue(), list.get(i).getPartID().getValue()));
        }

        root = new RecursiveTreeItem<CheckedOutTabTableRow>(
                tableRows, RecursiveTreeObject::getChildren
        );

        checkedOutTable.getColumns().setAll(studentNameCol, partNameCol, barcodeCol,
                checkedOutAtCol, dueDateCol);
        checkedOutTable.setRoot(root);
        checkedOutTable.setShowRoot(false);
    }

    @FXML
    private void search() {
        checkedOutTable.setPredicate(new Predicate<TreeItem<CheckedOutTabTableRow>>() {
            @Override
            public boolean test(TreeItem<CheckedOutTabTableRow> tableRow) {
                String input = searchInput.getText().toLowerCase();
                studentName = tableRow.getValue().getStudentName().getValue();
                partName = tableRow.getValue().getPartName().getValue();
                barcode = tableRow.getValue().getBarcode().getValue();
                checkedOutAt = tableRow.getValue().getCheckedOutAt().getValue();
                dueDate = tableRow.getValue().getDueDate().getValue();

                return ((studentName != null && studentName.toLowerCase().contains(input))
                        || (partName != null && partName.toLowerCase().contains(input))
                        || (barcode != null && barcode.toLowerCase().contains(input))
                        || (checkedOutAt != null && checkedOutAt.toLowerCase().contains(input))
                        || (dueDate != null && dueDate.toLowerCase().contains(input)));
            }
        });
    }

}