package InventoryController;

import Database.Database;
import Database.Part;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class ControllerTotalTab extends ControllerInventoryPage implements Initializable {

//    @FXML
//    private TextField searchTotal;

    @FXML
    public AnchorPane totalTabPage;

    @FXML
    private Button add, remove, refresh;

    @FXML
    private ObservableList<TotalTabTableRow> tableRows;

    @FXML
    private JFXTextField searchInput;

    @FXML
    private JFXTreeTableView<TotalTabTableRow> totalTable;

    @FXML
    private JFXTreeTableColumn<TotalTabTableRow, String> actionButtonsCol;

    @FXML JFXTreeTableColumn<TotalTabTableRow, JFXTreeTableColumn> outerPartNameCol;

    @FXML
    private JFXTreeTableColumn<TotalTabTableRow, String> partNameCol, serialNumberCol, locationCol,
            barcodeCol, partIDCol;

    @FXML
    private JFXTreeTableColumn<TotalTabTableRow, Boolean> faultCol;

    private String partName, serialNumber, loc, barcode, partID;

    private static ObservableList<Part> data
            = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label emptytableLabel = new Label("No parts found.");
        emptytableLabel.setFont(new Font(18));
        totalTable.setPlaceholder(emptytableLabel);

        outerPartNameCol = new JFXTreeTableColumn<>("Outer Part Name Col");
        outerPartNameCol.setPrefWidth(150);
        outerPartNameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TotalTabTableRow, JFXTreeTableColumn>, ObservableValue<JFXTreeTableColumn>>() {
            @Override
            public ObservableValue<JFXTreeTableColumn> call(TreeTableColumn.CellDataFeatures<TotalTabTableRow, JFXTreeTableColumn> param) {
                return null;
            }
        });

        partNameCol = new JFXTreeTableColumn<>("Part Name");
        partNameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TotalTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TotalTabTableRow, String> param) {
                return param.getValue().getValue().getPartName();
            }
        });

        partNameCol.setPrefWidth(150);
        partNameCol.setCellFactory(new Callback<TreeTableColumn<TotalTabTableRow, String>, TreeTableCell<TotalTabTableRow, String>>() {
            @Override
            public TreeTableCell<TotalTabTableRow, String> call(TreeTableColumn<TotalTabTableRow, String> param) {
                final TreeTableCell<TotalTabTableRow, String> cell = new TreeTableCell<TotalTabTableRow, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (this.getTreeTableRow().getItem() != null) {
                            String name = this.getTreeTableRow().getItem().getPartName().getValue();
                            Label partName = new Label();
                            partName.setText(name);
                            if (empty) {
                                setGraphic(null);
                                setText(null);
                            } else {
                                final JFXButton editOne = new JFXButton("Edit");
                                HBox content = new HBox();
                                editOne.setButtonType(JFXButton.ButtonType.RAISED);
                                editOne.setOnAction(event -> {
                                    editPart();
                                });
                                content.getChildren().addAll(partName, editOne);
                                setGraphic(content);
                                setText(null);
                            }
                        }

                    }
                };
                return cell;
            }
        });

        serialNumberCol = new JFXTreeTableColumn<>("Serial Number");
        serialNumberCol.setPrefWidth(150);
        serialNumberCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TotalTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TotalTabTableRow, String> param) {
                return param.getValue().getValue().getSerialNumber();
            }
        });

        locationCol = new JFXTreeTableColumn<>("Location");
        locationCol.setPrefWidth(150);
        locationCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TotalTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TotalTabTableRow, String> param) {
                return param.getValue().getValue().getLocation();
            }
        });

        barcodeCol = new JFXTreeTableColumn<>("Barcode");
        barcodeCol.setPrefWidth(150);
        barcodeCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TotalTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TotalTabTableRow, String> param) {
                return param.getValue().getValue().getBarcode();
            }
        });

        faultCol = new JFXTreeTableColumn<>("Fault?");
        faultCol.setPrefWidth(100);
//        faultCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TotalTabTableRow, CheckBoxTableCell>, ObservableValue<CheckBoxTableCell>>() {
//            @Override
//            public ObservableValue<CheckBoxTableCell> call(TreeTableColumn.CellDataFeatures<TotalTabTableRow, CheckBoxTableCell> param) {
//                return null;
//            }
//        });

        faultCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TotalTabTableRow, Boolean>,
                ObservableValue<Boolean>>() {

            @Override
            public ObservableValue<Boolean> call(TreeTableColumn.CellDataFeatures<TotalTabTableRow, Boolean> param) {
                TreeItem<TotalTabTableRow> treeItem = param.getValue();
                TotalTabTableRow tRow = treeItem.getValue();
                SimpleBooleanProperty booleanProp= new SimpleBooleanProperty(tRow.getFault());
                return booleanProp;
            }
        });

        faultCol.setCellFactory(new Callback<TreeTableColumn<TotalTabTableRow, Boolean>, TreeTableCell<TotalTabTableRow, Boolean>>() {
            @Override
            public TreeTableCell<TotalTabTableRow, Boolean> call( TreeTableColumn<TotalTabTableRow, Boolean> p ) {
                CheckBoxTreeTableCell<TotalTabTableRow,Boolean> cell = new CheckBoxTreeTableCell<TotalTabTableRow,Boolean>();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });

        partIDCol = new JFXTreeTableColumn<>("Part ID");
        partIDCol.setPrefWidth(100);
        partIDCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TotalTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TotalTabTableRow, String> param) {
                return param.getValue().getValue().getPartID();
            }
        });

        tableRows = FXCollections.observableArrayList();

        searchInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                totalTable.setPredicate(new Predicate<TreeItem<TotalTabTableRow>>() {
                    @Override
                    public boolean test(TreeItem<TotalTabTableRow> tableRow) {
                        String input = newValue.toLowerCase();
                        partName = tableRow.getValue().getPartName().getValue();
                        serialNumber = tableRow.getValue().getSerialNumber().getValue();
                        loc = tableRow.getValue().getLocation().getValue();
                        barcode = tableRow.getValue().getBarcode().getValue();
                        partID = tableRow.getValue().getPartID().getValue();

                        return ((partName != null && partName.toLowerCase().contains(input))
                            || (serialNumber != null && serialNumber.toLowerCase().contains(input))
                            || (loc != null && loc.toLowerCase().contains(input))
                            || (barcode != null && barcode.toLowerCase().contains((input))
                            || (partID != null && partID.toLowerCase().contains(input))));
                    }
                });
            }
        });

        // Click to select if unselected and unselect if selected
        totalTable.setRowFactory(new Callback<TreeTableView<TotalTabTableRow>, TreeTableRow<TotalTabTableRow>>() {
            @Override
            public TreeTableRow<TotalTabTableRow> call(TreeTableView<TotalTabTableRow> param) {
                final TreeTableRow<TotalTabTableRow> row = new TreeTableRow<>();
                row.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        final int index = row.getIndex();
                        if (index >= 0 && index < totalTable.getCurrentItemsCount() && totalTable.getSelectionModel().isSelected(index)) {
                            totalTable.getSelectionModel().clearSelection();
                            event.consume();
                        }
                    }
                });
                row.hoverProperty().addListener(observable -> {
                    if (row.isHover() && row != null && row.getItem() != null) {
                        System.out.println("TEST");
                    }
                });
                return row;
            }
        });

        populateTable();

    }

    /**
     * Sets the values for each table column, empties the current table, then calls selectParts to populate it.
     * @author Matthew Karcz
     */
    @FXML
    public void populateTable() {
        tableRows.clear();
        totalTable.getColumns().clear();
        this.data.clear();
        this.data = selectParts("SELECT * from parts WHERE isDeleted = 0 ORDER BY partID", this.data);

//        partName.setCellValueFactory(new PropertyValueFactory("partName"));
//        serialNumber.setCellValueFactory(new PropertyValueFactory("serialNumber"));
//        location.setCellValueFactory(new PropertyValueFactory("location"));
//        barcode.setCellValueFactory(new PropertyValueFactory("barcode"));
//        partID.setCellValueFactory(new PropertyValueFactory("partID"));
//        fault.setCellFactory(CheckBoxTableCell.forTableColumn(fault));
//        fault.setCellValueFactory(new PropertyValueFactory("fault"));
//        tableView.getItems().clear();
//        tableView.getItems().setAll(this.data);

        for (int i = 0; i < data.size(); i++) {
            Button button = new Button("Edit");
            tableRows.add(new TotalTabTableRow(data.get(i).getPartName(), new HBox(button),
                    data.get(i).getSerialNumber(), data.get(i).getLocation(),
                    data.get(i).getBarcode(), data.get(i).getFault(), "" + data.get(i).getPartID()));
        }

        final TreeItem<TotalTabTableRow> root = new RecursiveTreeItem<TotalTabTableRow>(
                tableRows, RecursiveTreeObject::getChildren
        );
        totalTable.getColumns().setAll(partNameCol, serialNumberCol, locationCol, barcodeCol, faultCol, partIDCol);
        totalTable.setRoot(root);
        totalTable.setShowRoot(false);

//        tableView.setRowFactory(tv -> {
//            TableRow<Part> row = new TableRow<>();
//            row.setOnMouseClicked(event -> {
//                if (event.getClickCount() == 2 && (!row.isEmpty())) {
//                    Part rowData = row.getItem();
//                    editPart(rowData);
//                }
//            });
//            return row;
//        });
    }

    /**
     * Called to bring up the "AddPart" FXML scene.
     */
    @FXML
    public void addPart() {
        Stage stage = new Stage();
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("AddPart.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            Parent root = loader.load(myFxmlURL);
            Scene scene = new Scene(root, 400, 400);
            stage.setTitle("Add a Part");
            stage.initOwner(totalTabPage.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    populateTable();
                    stage.close();
                }
            });
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when a row is highlighted in the table and the edit button is clicked.
     */
    @FXML
    public void editPart() {
        if (totalTable.getSelectionModel().getSelectedItems().size() == 1) {
            Stage stage = new Stage();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditPart.fxml"));
                Parent root = loader.load();
                ((ControllerEditPart) loader.getController()).initPart(
                        database.selectPart(
                        Integer.parseInt(totalTable.getSelectionModel().getSelectedItem().getValue().getPartID().getValue())));
                Scene scene = new Scene(root, 400, 400);
                stage.setTitle("Edit a Part");
                stage.initOwner(totalTabPage.getScene().getWindow());
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setScene(scene);
                stage.getIcons().add(new Image("msoe.png"));
                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        populateTable();
                        stage.close();
                    }
                });
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method calls the database method to soft delete an item from the inventory list
     * this then updates the gui table
     *
     * @author Bailey Terry
     */
    @FXML
    public void removePart() {
        String partID = totalTable.getSelectionModel().getSelectedItem().getValue().getPartID().getValue();
        if(JOptionPane.showConfirmDialog(null, "Are you sure you wish to delete the part with ID = " + partID + "?") == JOptionPane.YES_OPTION) {
            if (totalTable.getSelectionModel().getSelectedItems().size() == 1) {
                try {
                    database.deleteItem(Integer.parseInt(partID));
                    populateTable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            tableView.getItems().remove(part);
            populateTable();
        }
    }
}