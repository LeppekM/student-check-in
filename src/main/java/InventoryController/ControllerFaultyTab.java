package InventoryController;

import Database.FaultyPartLookup;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class ControllerFaultyTab  extends ControllerInventoryPage implements Initializable {

    @FXML
    public VBox faultyPage;

    @FXML
    private ObservableList<FaultyPartTabTableRow> tableRows;

    @FXML
    private JFXTreeTableView<FaultyPartTabTableRow> faultyTable;

    private TreeItem<FaultyPartTabTableRow> root;

    private JFXTreeTableColumn<FaultyPartTabTableRow,String> partNameCol, locationCol,
            barcodeCol, faultDescCol;

    private String partName, loc, barcode, faultDescription;

    @FXML
    private JFXTextField searchInput;

    @FXML
    private JFXButton searchButton;

    private static ObservableList<FaultyPartTabTableRow> data
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
        partNameCol.prefWidthProperty().bind(faultyTable.widthProperty().divide(4));
        partNameCol.setStyle("-fx-font-size: 18px");
        partNameCol.setResizable(false);
        partNameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FaultyPartTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FaultyPartTabTableRow, String> param) {
                return param.getValue().getValue().getPartName();
            }
        });

        locationCol = new JFXTreeTableColumn<>("Location");
        locationCol.prefWidthProperty().bind(faultyTable.widthProperty().divide(4));
        locationCol.setStyle("-fx-font-size: 18px");
        locationCol.setResizable(false);
        locationCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FaultyPartTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FaultyPartTabTableRow, String> param) {
                return param.getValue().getValue().getLocation();
            }
        });

        barcodeCol = new JFXTreeTableColumn<>("Barcode");
        barcodeCol.prefWidthProperty().bind(faultyTable.widthProperty().divide(4));
        barcodeCol.setStyle("-fx-font-size: 18px");
        barcodeCol.setResizable(false);
        barcodeCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FaultyPartTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FaultyPartTabTableRow, String> param) {
                return param.getValue().getValue().getBarcode();
//                return new ReadOnlyStringWrapper(param.getValue().getValue().getBarcode().toString());
            }
        });

        faultDescCol = new JFXTreeTableColumn<>("Fault Description");
        faultDescCol.prefWidthProperty().bind(faultyTable.widthProperty().divide(4));
        faultDescCol.setStyle("-fx-font-size: 18px");
        faultDescCol.setResizable(false);
        faultDescCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FaultyPartTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FaultyPartTabTableRow, String> param) {
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
        faultyTable.setRowFactory(new Callback<TreeTableView<FaultyPartTabTableRow>, TreeTableRow<FaultyPartTabTableRow>>() {
            @Override
            public TreeTableRow<FaultyPartTabTableRow> call(TreeTableView<FaultyPartTabTableRow> param) {
                final TreeTableRow<FaultyPartTabTableRow> row = new TreeTableRow<>();
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
        FaultyPartLookup faulty = new FaultyPartLookup();
        data.clear();
        data = faulty.populateFaulty();

        for (int i = 0; i < data.size(); i++) {
            tableRows.add(new FaultyPartTabTableRow(data.get(i).getPartID().getValue(), data.get(i).getPartName().getValue(),
                     data.get(i).getLocation().getValue(),
                    "" + data.get(i).getBarcode().getValue(), data.get(i).getFaultDescription().getValue()));
        }

        root = new RecursiveTreeItem<FaultyPartTabTableRow>(
                tableRows, RecursiveTreeObject::getChildren
        );

        faultyTable.getColumns().setAll(partNameCol, locationCol, barcodeCol, faultDescCol);
        faultyTable.setRoot(root);
        faultyTable.setShowRoot(false);
    }

    @FXML
    private void search() {
        faultyTable.setPredicate(new Predicate<TreeItem<FaultyPartTabTableRow>>() {
            @Override
            public boolean test(TreeItem<FaultyPartTabTableRow> tableRow) {
                String input = searchInput.getText().toLowerCase();
                partName = tableRow.getValue().getPartName().getValue();
                loc = tableRow.getValue().getLocation().getValue();
                barcode = tableRow.getValue().getBarcode().getValue().toString();
                faultDescription = tableRow.getValue().getFaultDescription().getValue();

                return ((partName != null && partName.toLowerCase().contains(input))
                        || (loc != null && loc.toLowerCase().contains(input))
                        || (barcode != null && barcode.toLowerCase().contains(input))
                        || (faultDescription != null && faultDescription.toLowerCase().contains(input)));
            }
        });
    }
}
