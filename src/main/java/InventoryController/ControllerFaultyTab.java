package InventoryController;

import Database.Database;
import Database.FaultyPartLookup;
import Database.ObjectClasses.Part;
import Database.ObjectClasses.Worker;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
             faultDescCol;

    @FXML
    private JFXTreeTableColumn<FaultyPartTabTableRow, Long> barcodeCol;

    private String partName, loc, barcode, faultDescription;

    @FXML
    private JFXTextField searchInput;

    @FXML
    private JFXButton searchButton;

    private static ObservableList<FaultyPartTabTableRow> data
            = FXCollections.observableArrayList();

    private Worker worker;
    private Database database;

    /**
     * This method sets the data in the faulty page.
     * @param location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label emptyTableLabel = new Label("No parts found.");
        emptyTableLabel.setStyle("-fx-text-fill: white");
        emptyTableLabel.setFont(new Font(18));
//        searchButton.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15pt; -fx-border-radius: 15pt; -fx-border-color: #043993; -fx-text-fill: #000000;");
        faultyTable.setPlaceholder(emptyTableLabel);
        database = new Database();

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
        barcodeCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FaultyPartTabTableRow, Long>, ObservableValue<Long>>() {
            @Override
            public ObservableValue<Long> call(TreeTableColumn.CellDataFeatures<FaultyPartTabTableRow, Long> param) {
                return param.getValue().getValue().getBarcode().asObject();
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
                return param.getValue().getValue().getDescription();
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
                        if (event.getClickCount() == 2) {
                            viewPart(row.getIndex());
                        } else {
                            final int index = row.getIndex();
                            if (index >= 0 && index < faultyTable.getCurrentItemsCount() && faultyTable.getSelectionModel().isSelected(index)) {
                                faultyTable.getSelectionModel().clearSelection();
                                event.consume();
                            }
                        }
                    }
                });
                return row;
            }
        });
    }

    public void exportFaulty(){
        export.exportFaulty(data);
    }

    private void viewPart(int index){
        Stage stage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewFaultyPart.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Checked Out Item");
            stage.initOwner(faultyPage.getScene().getWindow());
            stage.setTitle("View Faulty Part");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            if (index != -1) {
                TreeItem item = faultyTable.getSelectionModel().getModelItem(index);
                // null if user clicks on empty row
                if (item != null) {
                    FaultyPartTabTableRow row = ((FaultyPartTabTableRow) item.getValue());
                    ((ControllerViewFaultyPart) loader.getController()).populate(row);
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.show();
                }
            }
//                stage.setOnHiding(event1 -> fees.setText("Outstanding fees: $" + overdueFee(student)));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Sets the values for each table column, empties the current table, then calls selectParts to populate it.
     * @author Matthew Karcz
     */
    @FXML
    public void populateTable() {
        //tableRows.clear();
        //faultyTable.getColumns().clear();
        FaultyPartLookup faulty = new FaultyPartLookup();
        //data.clear();
        data = faulty.getDetailedFaultyInfo();
        tableRows = FXCollections.observableArrayList();

        for (int i = 0; i < data.size(); i++) {
            tableRows.add(new FaultyPartTabTableRow(data.get(i).getStudentName().getValue(), data.get(i).getStudentEmail().getValue(),
                    data.get(i).getPartName().getValue(), data.get(i).getBarcode().getValue(),
                    data.get(i).getDescription().getValue(), data.get(i).getPrice().getValue(),
                    data.get(i).getLocation().getValue(), data.get(i).getDate().getValue()));
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
                faultDescription = tableRow.getValue().getDescription().getValue();

                return ((partName != null && partName.toLowerCase().contains(input))
                        || (loc != null && loc.toLowerCase().contains(input))
                        || (barcode != null && barcode.toLowerCase().contains(input))
                        || (faultDescription != null && faultDescription.toLowerCase().contains(input)));
            }
        });
    }

    public void resolveFault(ActionEvent actionEvent) {
        if (faultyTable.getSelectionModel().getSelectedItems().size() == 1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to resolve this fault?");
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            alert.setTitle("Resolve Faulty Part?");
            alert.setHeaderText("Resolving Faulty Part...");
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.YES) {
                    int index = faultyTable.getSelectionModel().getFocusedIndex();
                    FaultyPartTabTableRow part = faultyTable.getSelectionModel().getModelItem(index).getValue();
                    database.resolveFault(part.getBarcode().getValue(), part.getPartName().get());
                    populateTable();
                } else if (buttonType == ButtonType.NO) {
                    alert.close();
                }
            });
        }
    }

    /**
     * Used to keep track of which worker is currently logged in by passing the worker into
     * each class.
     * @param worker the currently logged in worker
     */
    @Override
    public void initWorker(Worker worker) {
        if (this.worker == null){
            this.worker = worker;
            database.initWorker(worker);
        }
    }
}
