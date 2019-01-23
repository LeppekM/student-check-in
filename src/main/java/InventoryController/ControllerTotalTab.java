package InventoryController;

import Database.Part;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.controlsfx.control.CheckComboBox;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class ControllerTotalTab extends ControllerInventoryPage implements Initializable {

//    @FXML
//    private TextField searchTotal;

    @FXML
    public AnchorPane totalTabPage;

    @FXML
    private ObservableList<TotalTabTableRow> tableRows;

    @FXML
    private JFXTextField searchInput;

    @FXML
    private JFXTreeTableView<TotalTabTableRow> totalTable;

    private TreeItem<TotalTabTableRow> root;

    @FXML JFXTreeTableColumn<TotalTabTableRow, JFXTreeTableColumn> outerPartNameCol;

    @FXML
    private JFXTreeTableColumn<TotalTabTableRow, String> partNameCol, serialNumberCol, locationCol,
            barcodeCol, partIDCol;

    @FXML
    private JFXTreeTableColumn<TotalTabTableRow, Boolean> faultCol;

    @FXML
    private CheckComboBox<String> sortCheckBox;

    private String partName, serialNumber, loc, barcode, partID, sortFilter = "";

    private static ObservableList<Part> data
            = FXCollections.observableArrayList();

    @FXML
    private JFXButton add;

    private final ObservableList<String> types = FXCollections.observableArrayList(new String[] { "All", "Checked Out", "Overdue", "Faulty"});

    private final int CHECKBOX_X = 450, CHECKBOX_Y = 25, CHECKBOX_PREF_HEIGHT = 10, CHECKBOX_PREF_WIDTH = 150;

    private ArrayList<String> selectedFilters = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        add.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15pt; -fx-border-radius: 15pt; -fx-border-color: #043993; -fx-text-fill: #000000;");

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
                                Image editOneImage = new Image("images/edit.png");
                                ImageView editOneImageView = new ImageView(editOneImage);
                                editOneImageView.setFitHeight(12);
                                editOneImageView.setFitWidth(12);
                                final JFXButton editOneButton = new JFXButton();
                                editOneButton.setGraphic(editOneImageView);
                                editOneButton.setButtonType(JFXButton.ButtonType.RAISED);
                                editOneButton.setOnAction(event -> {
                                    editPart(getTreeTableRow().getItem().getPartID().getValue(), false);
                                });
                                editOneButton.setTooltip(new Tooltip("Edit this part"));

                                Image editAllImage = new Image("images/edit_all.png");
                                ImageView editAllImageView = new ImageView(editAllImage);
                                editAllImageView.setFitHeight(12);
                                editAllImageView.setFitWidth(12);
                                final JFXButton editAllButton = new JFXButton();
                                editAllButton.setGraphic(editAllImageView);
                                editAllButton.setButtonType(JFXButton.ButtonType.RAISED);
                                editAllButton.setOnAction(event -> {
                                    editPart(getTreeTableRow().getItem().getPartID().getValue(), true);
                                });
                                Tooltip editAllTip = new Tooltip("Edit all parts named: " + partName.getText());
                                editAllButton.setTooltip(editAllTip);

                                Image deleteOneImage = new Image("images/delete.png");
                                ImageView deleteOneImageView = new ImageView(deleteOneImage);
                                deleteOneImageView.setFitHeight(12);
                                deleteOneImageView.setFitWidth(12);
                                final JFXButton deleteOneButton = new JFXButton();
                                deleteOneButton.setGraphic(deleteOneImageView);
                                deleteOneButton.setButtonType(JFXButton.ButtonType.RAISED);
                                deleteOneButton.setOnAction(event -> {
                                    deletePart(getTreeTableRow().getItem().getPartID().getValue());
                                });
                                Tooltip deleteOneTip = new Tooltip("Delete this part");
                                deleteOneButton.setTooltip(deleteOneTip);

                                Image deleteAllImage = new Image("images/delete_all.png");
                                ImageView deleteAllImageView = new ImageView(deleteAllImage);
                                deleteAllImageView.setFitHeight(12);
                                deleteAllImageView.setFitWidth(12);
                                final JFXButton deleteAllButton = new JFXButton();
                                deleteAllButton.setGraphic(deleteAllImageView);
                                deleteAllButton.setButtonType(JFXButton.ButtonType.RAISED);
                                deleteAllButton.setOnAction(event -> {
                                    deletePartType(getTreeTableRow().getItem().getPartName().getValue());
                                });
                                Tooltip deleteAllTip = new Tooltip("Delete all parts named: " + partName.getText());
                                deleteAllButton.setTooltip(deleteAllTip);

                                VBox column = new VBox();
                                HBox actionButtons = new HBox();
                                actionButtons.setPrefHeight(column.getHeight()/4);
                                actionButtons.setAlignment(Pos.TOP_RIGHT);
                                actionButtons.setOpacity(0);
                                actionButtons.hoverProperty().addListener(observable -> {
                                    if (actionButtons.isHover() && actionButtons != null) {
                                        actionButtons.setOpacity(1);
                                    } else {
                                        actionButtons.setOpacity(0);
                                    }
                                });
                                actionButtons.getChildren().addAll(editOneButton, editAllButton, deleteOneButton, deleteAllButton);
                                actionButtons.setMaxHeight(12);
                                VBox text = new VBox();
//                                text.setMinHeight(30);
                                text.setAlignment(Pos.TOP_LEFT);
                                text.getChildren().add(partName);
                                column.getChildren().addAll(actionButtons, text);
                                setGraphic(column);
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
                filterChanged(newValue);
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
                        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                            Part rowData = database.selectPart(Integer.parseInt(totalTable.getSelectionModel().getModelItem(index).getValue().getPartID().get()));
                            showInfoPage(rowData, "total");
                            totalTable.getSelectionModel().clearSelection();
                            event.consume();
                            System.out.println("Hi " + rowData.toString());
                        } else if (index >= 0 && index < totalTable.getCurrentItemsCount() && totalTable.getSelectionModel().isSelected(index)) {
                            totalTable.getSelectionModel().clearSelection();
                            event.consume();
                        }
                    }
                });
                return row;
            }
        });
        sortCheckBox = new CheckComboBox<>(types);
        sortCheckBox.getCheckModel().checkIndices(0);
        selectedFilters.add("All");
        sortCheckBox.setLayoutX(CHECKBOX_X);
        sortCheckBox.setLayoutY(CHECKBOX_Y);
        sortCheckBox.setPrefSize(CHECKBOX_PREF_WIDTH, CHECKBOX_PREF_HEIGHT);
        totalTabPage.getChildren().add(sortCheckBox);

        sortCheckBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
            public void onChanged(ListChangeListener.Change<? extends String> s) {
                while (s.next()) {
                    if (s.wasAdded()) {
                        if (s.toString().contains("All")) {
                            //manually clear other selections when "All" is chosen
                            for (int i = 1; i < types.size(); i++) {
                                sortCheckBox.getCheckModel().clearCheck(i);
                                selectedFilters.clear();
                            }
                        } else {
                            // check if the "All" option is selected and if so remove it
                            if (sortCheckBox.getCheckModel().isChecked(0)) {
                                sortCheckBox.getCheckModel().clearCheck(0);
                            }

                        }
                    }
                }
                selectedFilters.clear();
                selectedFilters.addAll(sortCheckBox.getCheckModel().getCheckedItems());
                populateTable();
            }
        });
        populateTable();

    }

    private void filter(TreeItem<TotalTabTableRow> root, String filter, TreeItem<TotalTabTableRow> filteredRoot) {
        for (TreeItem<TotalTabTableRow> child : root.getChildren()) {
            TreeItem<TotalTabTableRow> filteredChild = new TreeItem<>();
            filteredChild.setValue(child.getValue());
            filteredChild.setExpanded(true);
            filter(child, filter, filteredChild);
            if (!filteredChild.getChildren().isEmpty() || isMatch(filteredChild.getValue(), filter)) {
                filteredRoot.getChildren().add(filteredChild);
            }
        }
    }

    private void filterChanged(String filter) {
        if (filter.isEmpty()) {
            totalTable.setRoot(root);
        }
        else {
            TreeItem<TotalTabTableRow> filteredRoot = new TreeItem<>();
            filter(root, filter, filteredRoot);
            totalTable.setRoot(filteredRoot);
        }
    }

    private boolean isMatch(TotalTabTableRow value, String filter) {
        String input = filter.toLowerCase();
        partName = value.getPartName().getValue();
        serialNumber = value.getSerialNumber().getValue();
        loc = value.getLocation().getValue();
        barcode = value.getBarcode().getValue();
        partID = value.getPartID().getValue();

        return ((partName != null && partName.toLowerCase().contains(input))
                || (serialNumber != null && serialNumber.toLowerCase().contains(input))
                || (loc != null && loc.toLowerCase().contains(input))
                || (barcode != null && barcode.toLowerCase().contains((input))
                || (partID != null && partID.toLowerCase().contains(input))));
    }

    public ArrayList<String> getSelctedFilters(){
        return selectedFilters;
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
        ArrayList<String> types = getSelctedFilters();
        System.out.println(types);
        if(!types.isEmpty()) {
            this.data = selectParts("SELECT * from parts WHERE isDeleted = 0" + getSortTypes(types) + " ORDER BY partID", this.data);

            for (int i = 0; i < data.size(); i++) {
                Button button = new Button("Edit");
                tableRows.add(new TotalTabTableRow(data.get(i).getPartName(), new HBox(button),
                        data.get(i).getSerialNumber(), data.get(i).getLocation(),
                        data.get(i).getBarcode(), data.get(i).getFault(), "" + data.get(i).getPartID()));
            }

            root = new RecursiveTreeItem<TotalTabTableRow>(
                    tableRows, RecursiveTreeObject::getChildren
            );
            totalTable.getColumns().setAll(partNameCol, serialNumberCol, locationCol, barcodeCol, faultCol, partIDCol);
            totalTable.setRoot(root);
            totalTable.setShowRoot(false);
        }
    }

    /**
     * Searches through the sort filter to grab what categories to return
     * @return String to be input into raw SQL statement
     * @author Matt Karcz
     */
    public String getSortTypes(ArrayList<String> types){
        String result = "";
        if (types.contains("All")){
            return result;
        } else{
            result = " AND ";
        }
        if (types.contains("Checked Out")){
            if(!result.equals(" AND "))
                result = result + " OR ";
            result = result + " isCheckedOut = 1";
        }
        if (types.contains("Overdue")){
            if(!result.equals(" AND "))
                result = result + " OR ";
            long longDate = System.currentTimeMillis();
            Date date = new java.sql.Date(longDate);
            result = result + " checkout_parts.dueAt < date('" + date.toString() + "')";
        }
        if (types.contains("Faulty")){
            if(!result.equals(" AND "))
                result = result + " OR ";
            result = result + "isFaulty = 1";
        }
        return result;
    }

    /**
     * Called to bring up the "AddPart" FXML scene.
     */
    @FXML
    public void addPart() {
        Stage stage = new Stage();
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("fxml/AddPart.fxml");
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
    public void editPart(String partID, boolean isBatchEdit) {
        try {
            Part part = database.selectPart(Integer.parseInt(partID));
            FXMLLoader loader;
            if (isBatchEdit) {
                loader = new FXMLLoader(getClass().getResource("/fxml/EditPartType.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("/fxml/EditOnePart.fxml"));
            }
            Parent root = loader.load();
            ((ControllerEditPart) loader.getController()).initPart(part );
            Scene scene = new Scene(root, 400, 400);
            Stage stage = new Stage();
            stage.setTitle("Edit a Part");
            stage.initOwner(totalTabPage.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("images/msoe.png"));
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
     * This method calls the database method to soft delete an item from the inventory list
     * this then updates the gui table
     *
     * @author Bailey Terry
     */
    public void deletePart(String partID) {
        try {
            if (database.selectPart(Integer.parseInt(partID)) != null) {
                if (JOptionPane.showConfirmDialog(null, "Are you sure you wish to delete the part with ID = " + partID + "?") == JOptionPane.YES_OPTION) {
                    database.deleteItem(Integer.parseInt(partID));
                    populateTable();
                }
            }

//            tableView.getItems().remove(part);
//            populateTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void deletePartType(String partName) {
        try {
            if (database.hasPartName(partName)) {
                if (JOptionPane.showConfirmDialog(null, "Are you sure you wish to delete the part " + partName) == JOptionPane.YES_OPTION) {
                    database.deleteParts(partName);
                    populateTable();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}