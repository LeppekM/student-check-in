package InventoryController;

import Database.Objects.Part;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class ControllerFaultyTab  extends ControllerInventoryPage implements Initializable {

//    @FXML
//    private TextField searchTotal;

    @FXML
    public AnchorPane faultyPage;

    @FXML
    private ObservableList<FaultyTabTableRow> tableRows;

    @FXML
    private JFXTreeTableView<FaultyTabTableRow> faultyTable;

    private TreeItem<FaultyTabTableRow> root;

    private JFXTreeTableColumn<FaultyTabTableRow,String> partNameCol, serialNumberCol, locationCol,
            barcodeCol, faultDescCol;

    private String partName, serialNumber, loc, barcode, faultDescription;

    @FXML
    private JFXTextField searchInput;

    @FXML
    private JFXButton searchButton;

    private static ObservableList<Part> data
            = FXCollections.observableArrayList();

    /**
     * This method sets the data in the faulty page.
     * @param location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label emptyTableLabel = new Label("No parts found.");
        emptyTableLabel.setFont(new Font(18));
        searchButton.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15pt; -fx-border-radius: 15pt; -fx-border-color: #043993; -fx-text-fill: #000000;");
        faultyTable.setPlaceholder(emptyTableLabel);

        partNameCol = new JFXTreeTableColumn<>("Part Name");
        partNameCol.setPrefWidth(150);
        partNameCol.setResizable(false);
        partNameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FaultyTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FaultyTabTableRow, String> param) {
                return param.getValue().getValue().getPartName();
            }
        });

        serialNumberCol = new JFXTreeTableColumn<>("Serial Number");
        serialNumberCol.setPrefWidth(150);
        serialNumberCol.setResizable(false);
        serialNumberCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FaultyTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FaultyTabTableRow, String> param) {
                return param.getValue().getValue().getSerialNumber();
            }
        });

        locationCol = new JFXTreeTableColumn<>("Location");
        locationCol.setPrefWidth(150);
        locationCol.setResizable(false);
        locationCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FaultyTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FaultyTabTableRow, String> param) {
                return param.getValue().getValue().getLocation();
            }
        });

        barcodeCol = new JFXTreeTableColumn<>("Barcode");
        barcodeCol.setPrefWidth(150);
        barcodeCol.setResizable(false);
        barcodeCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FaultyTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FaultyTabTableRow, String> param) {
                return param.getValue().getValue().getBarcode();
            }
        });

        faultDescCol = new JFXTreeTableColumn<>("Fault Description");
        faultDescCol.setPrefWidth(150);
        faultDescCol.setResizable(false);
        faultDescCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FaultyTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FaultyTabTableRow, String> param) {
                return param.getValue().getValue().getFaultDescription();
            }
        });

        tableRows = FXCollections.observableArrayList();

        searchInput.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                search();
            }
        });

        // Click to select if unselected and deselect if selected
        faultyTable.setRowFactory(new Callback<TreeTableView<FaultyTabTableRow>, TreeTableRow<FaultyTabTableRow>>() {
            @Override
            public TreeTableRow<FaultyTabTableRow> call(TreeTableView<FaultyTabTableRow> param) {
                final TreeTableRow<FaultyTabTableRow> row = new TreeTableRow<>();
                row.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        final int index = row.getIndex();
                        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                            Part rowData = database.selectPart(faultyTable.getSelectionModel().getModelItem(index).getValue().getPartID().get());
                            if(!rowData.equals(null)) {
                                showInfoPage(rowData, "fault");
                            }
                            faultyTable.getSelectionModel().clearSelection();
                            event.consume();
                        } else if (index >= 0 && index < faultyTable.getCurrentItemsCount() && faultyTable.getSelectionModel().isSelected(index)) {
                            faultyTable.getSelectionModel().clearSelection();
                            event.consume();
                        }
                    }
                });
                return row;
            }
        });
    }

    /**
     * Sets the values for each table column, empties the current table, then calls selectParts to populate it.
     * @author Matthew Karcz
     */
    @FXML
    public void populateTable() {
        tableRows.clear();
        faultyTable.getColumns().clear();
        this.data.clear();
        this.data = selectParts("SELECT * from parts WHERE isDeleted = 0 AND isFaulty = 1 ORDER BY partID", this.data);

        for (int i = 0; i < data.size(); i++) {
            tableRows.add(new FaultyTabTableRow(data.get(i).getPartName(),
                    data.get(i).getSerialNumber(), data.get(i).getLocation(),
                    data.get(i).getBarcode(), data.get(i).getFaultDesc(), data.get(i).getPartID()));
        }

        root = new RecursiveTreeItem<FaultyTabTableRow>(
                tableRows, RecursiveTreeObject::getChildren
        );

        faultyTable.getColumns().setAll(partNameCol, serialNumberCol, locationCol, barcodeCol, faultDescCol);
        faultyTable.setRoot(root);
        faultyTable.setShowRoot(false);
    }

    @FXML
    private void search() {
        faultyTable.setPredicate(new Predicate<TreeItem<FaultyTabTableRow>>() {
            @Override
            public boolean test(TreeItem<FaultyTabTableRow> tableRow) {
                String input = searchInput.getText().toLowerCase();
                partName = tableRow.getValue().getPartName().getValue();
                serialNumber = tableRow.getValue().getSerialNumber().getValue();
                loc = tableRow.getValue().getLocation().getValue();
                barcode = tableRow.getValue().getBarcode().getValue();
                faultDescription = tableRow.getValue().getFaultDescription().getValue();

                return ((partName != null && partName.toLowerCase().contains(input))
                        || (serialNumber != null && serialNumber.toLowerCase().contains(input))
                        || (loc != null && loc.toLowerCase().contains(input))
                        || (barcode != null && barcode.toLowerCase().contains(input))
                        || (faultDescription != null && faultDescription.toLowerCase().contains(input)));
            }
        });
    }
}
