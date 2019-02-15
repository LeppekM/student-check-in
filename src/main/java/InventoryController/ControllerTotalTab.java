package InventoryController;

import Database.Part;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.controlsfx.control.CheckComboBox;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class ControllerTotalTab extends ControllerInventoryPage implements Initializable {

    @FXML
    public AnchorPane totalTabPage;

    @FXML
    private ObservableList<TotalTabTableRow> tableRows;

    @FXML
    private JFXTextField searchInput;

    @FXML
    private JFXTreeTableView<TotalTabTableRow> totalTable;

    private TreeItem<TotalTabTableRow> root;

    @FXML
    private JFXTreeTableColumn<TotalTabTableRow, String> partNameCol, serialNumberCol, locationCol,
            barcodeCol, partIDCol;

    @FXML
    private JFXTreeTableColumn<TotalTabTableRow, Boolean> faultCol;

    @FXML
    private CheckComboBox<String> sortCheckBox;

    private String partName, serialNumber, loc, barcode, partID;

    private static ObservableList<Part> data
            = FXCollections.observableArrayList();

    @FXML
    private JFXButton add, searchButton;

    private final ObservableList<String> types = FXCollections.observableArrayList(new String[] { "All", "Checked Out", "Overdue", "Faulty"});

    private final int CHECKBOX_X = 310, CHECKBOX_Y = 25, CHECKBOX_PREF_HEIGHT = 10, CHECKBOX_PREF_WIDTH = 150;

    private ArrayList<String> selectedFilters = new ArrayList<>();

    private Image editOneImage = new Image("images/edit.png");
    private Image editAllImage = new Image("images/edit_all.png");
    private Image deleteOneImage = new Image("images/delete.png");
    private Image deleteAllImage = new Image("images/delete_all.png");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        add.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15pt; -fx-border-radius: 15pt; -fx-border-color: #043993; -fx-text-fill: #000000;");
        searchButton.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15pt; -fx-border-radius: 15pt; -fx-border-color: #043993; -fx-text-fill: #000000;");

        Label emptytableLabel = new Label("No parts found.");
        emptytableLabel.setFont(new Font(18));
        totalTable.setPlaceholder(emptytableLabel);

        partNameCol = new JFXTreeTableColumn<>("Part Name");
        partNameCol.setCellValueFactory(col -> col.getValue().getValue().getPartName());

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

                                ImageView deleteOneImageView = new ImageView(deleteOneImage);
                                deleteOneImageView.setFitHeight(12);
                                deleteOneImageView.setFitWidth(12);
                                final JFXButton deleteOneButton = new JFXButton();
                                deleteOneButton.setGraphic(deleteOneImageView);
                                deleteOneButton.setButtonType(JFXButton.ButtonType.RAISED);
                                deleteOneButton.setOnAction(event -> {
                                    if (!database.getIsCheckedOut(getTreeTableRow().getItem().getPartID().getValue())) {
                                        deletePart(getTreeTableRow().getItem().getPartID().getValue());
                                    } else {
                                        deleteCheckedOutPartAlert();
                                    }
                                });
                                Tooltip deleteOneTip = new Tooltip("Delete this part");
                                deleteOneButton.setTooltip(deleteOneTip);

                                ImageView deleteAllImageView = new ImageView(deleteAllImage);
                                deleteAllImageView.setFitHeight(12);
                                deleteAllImageView.setFitWidth(12);
                                final JFXButton deleteAllButton = new JFXButton();
                                deleteAllButton.setGraphic(deleteAllImageView);
                                deleteAllButton.setButtonType(JFXButton.ButtonType.RAISED);
                                deleteAllButton.setOnAction(event -> {
                                    boolean typeHasOneCheckedOut = false;
                                    ArrayList<String> partIDs = database.getAllPartIDsForPartName(getTreeTableRow().getItem().getPartID().getValue());
                                    for (String id : partIDs) {
                                        if (database.getIsCheckedOut(id)) {
                                            typeHasOneCheckedOut = true;
                                        }
                                    }
                                    if (!typeHasOneCheckedOut) {
                                        deletePartType(getTreeTableRow().getItem().getPartName().getValue());
                                    } else {
                                        typeHasOneCheckedOutError(getTreeTableRow().getItem().getPartName().getValue());
                                    }
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
        serialNumberCol.setCellValueFactory(col -> col.getValue().getValue().getSerialNumber());

        locationCol = new JFXTreeTableColumn<>("Location");
        locationCol.setPrefWidth(150);
        locationCol.setCellValueFactory(col -> col.getValue().getValue().getLocation());

        barcodeCol = new JFXTreeTableColumn<>("Barcode");
        barcodeCol.setPrefWidth(150);

        barcodeCol.setCellValueFactory(col -> col.getValue().getValue().getBarcode());

        faultCol = new JFXTreeTableColumn<>("Fault?");
        faultCol.setPrefWidth(100);

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
        partIDCol.setCellValueFactory(col -> col.getValue().getValue().getPartID());

        tableRows = FXCollections.observableArrayList();

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
                            if(!rowData.equals(null)) {
                                if(rowData.getFault())
                                    showInfoPage(rowData, "fault");
                                else if(rowData.getCheckedOut())
                                    showInfoPage(rowData, "checkedOut");
                                else
                                    showInfoPage(rowData, "total");
                            }
                            totalTable.getSelectionModel().clearSelection();
                            event.consume();
                        } else if (index >= 0 && index < totalTable.getCurrentItemsCount() && totalTable.getSelectionModel().isSelected(index)) {
                            totalTable.getSelectionModel().clearSelection();
                            event.consume();
                        }
                    }
                });
                return row;
            }
        });
        getNames();

        sortCheckBox = new CheckComboBox<>(types);
        sortCheckBox.getCheckModel().checkIndices(0);
        selectedFilters.add("All");
        sortCheckBox.setLayoutX(CHECKBOX_X);
        sortCheckBox.setLayoutY(CHECKBOX_Y);
        sortCheckBox.setPrefSize(CHECKBOX_PREF_WIDTH, CHECKBOX_PREF_HEIGHT);
        totalTabPage.getChildren().add(sortCheckBox);

        searchInput.setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    search();
                }
        });

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
        TreeItem<TotalTabTableRow> filteredChild;
        for (TreeItem<TotalTabTableRow> child : root.getChildren()) {
            filteredChild = new TreeItem<>();
            filteredChild.setValue(child.getValue());
            filteredChild.setExpanded(true);
            filter(child, filter, filteredChild);
            if (!filteredChild.getChildren().isEmpty() || isMatch(filteredChild.getValue(), filter)) {
                filteredRoot.getChildren().add(filteredChild);
            }
        }
    }

    @FXML
    private void search() {
        String filter = searchInput.getText();
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

    public ArrayList<String> getSelectedFilters(){
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
        ArrayList<String> types = getSelectedFilters();

        StudentCheckIn.logger.info("Populating table, showing types: " + types);
        if(!types.isEmpty()) {
            this.data = selectParts("SELECT DISTINCT p.* from parts AS p " + getSortTypes(types) + " ORDER BY p.partID;", this.data);

            for (int i = 0; i < data.size(); i++) {
                tableRows.add(new TotalTabTableRow(data.get(i).getPartName(),
                        data.get(i).getSerialNumber(), data.get(i).getLocation(),
                        data.get(i).getBarcode(), data.get(i).getFault(),
                        "" + data.get(i).getPartID()));
            }

            root = new RecursiveTreeItem<TotalTabTableRow>(
                    tableRows, RecursiveTreeObject::getChildren
            );
            totalTable.getColumns().setAll(partNameCol, serialNumberCol, locationCol, barcodeCol,
                    faultCol, partIDCol);
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
        if(!selectedFilters.isEmpty()) {
            if (types.contains("All")) {
                result = "WHERE p.isDeleted = 0";
                return result;
            }
            if (types.contains("Overdue") && !types.contains("Checked Out")) {
                long longDate = System.currentTimeMillis();
                Date date = new java.sql.Date(longDate);
                if (result.isEmpty())
                    result = result + ", checkout AS c WHERE p.isDeleted = 0 AND (p.partID=c.partID AND c.dueAt < date('" + date.toString() + "'))";
            }
            if (types.contains("Checked Out")) {
                if (result.isEmpty())
                    result = result + "WHERE p.isDeleted = 0 AND isCheckedOut = 1";
                else
                    result = result + " OR p.isCheckedOut = 1";
            }
            if (types.contains("Faulty")) {
                if (result.isEmpty())
                    result = result + "WHERE p.isDeleted = 0 AND isFaulty = 1";
                else
                    result = result + " OR p.isFaulty = 1";
            }
            String currentFilter;
            for (int i = 0; i < selectedFilters.size(); i++) {
                currentFilter = selectedFilters.get(i);
                if (!currentFilter.equals("Overdue") && !currentFilter.equals("Checked Out") && !currentFilter.equals("Faulty")) {
                    if (result.isEmpty())
                        result = result + "WHERE p.isDeleted = 0 AND partName = '" + currentFilter + "'";
                    else
                        result = result + " OR p.partName = '" + currentFilter + "'";
                }
            }
        }
        return result;
    }

    /**
     * Called to populate an array with all unique names in the parts table from the database, to add as
     * filter options in the dropdown
     */
    public void getNames(){
        String rawStatement = "SELECT DISTINCT partName from parts;";
        Statement currentStatement = null;
        try {
            Connection connection = database.getConnection();
            currentStatement = connection.createStatement();
            ResultSet rs = currentStatement.executeQuery(rawStatement);
            String partName;
            while (rs.next()) {
                partName = rs.getString("partName");
                types.add(partName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (currentStatement != null) {
                try {
                    currentStatement.close();
                } catch (SQLException e) {
                    StudentCheckIn.logger.error("SQL error: with selecting part names from db.");
                    e.printStackTrace();
                }
            }
        }
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
            Scene scene = new Scene(root, 400, 450);
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
            StudentCheckIn.logger.error("IOException: Loading Add Part.");
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
            StudentCheckIn.logger.error("IOException: Loading Edit Part.");
            e.printStackTrace();
        }
    }

    /**
     * This method brings up the FXML page for showing the info about the selected part
     * @param part - The part that was selected
     * @param type - The type of part, determines what information is shown
     * @author Matthew Karcz
     */
    public void showInfoPage(Part part, String type){
        Stage stage = new Stage();
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("fxml/ShowPart.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            Parent root = loader.load();
            ((ControllerShowPart) loader.getController()).initPart(part, type);
            Scene scene = new Scene(root, 400, 400);
            stage.setTitle("Part Information");
            stage.initOwner(totalTabPage.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("images/msoe.png"));
            stage.showAndWait();
        } catch (IOException e) {
            StudentCheckIn.logger.error("IOException: Loading Show Part.");
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
            StudentCheckIn.logger.error("Exception while deleting part.");
            e.printStackTrace();
        }
    }
    public void deletePartType(String partName) {
        try {
            if (database.hasPartName(partName)) {
                if (JOptionPane.showConfirmDialog(null, "Are you sure you wish to delete all parts named: " + partName) == JOptionPane.YES_OPTION) {
                    database.deleteParts(partName);
                    populateTable();
                }
            }
        } catch (Exception e) {
            StudentCheckIn.logger.error("Exception while deleting part type.");
            e.printStackTrace();
        }
    }

    /**
     * Alert that the part is currently checked out, so it cannot be deleted
     */
    private void deleteCheckedOutPartAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("This part is currently checked out and cannot be deleted.");
        StudentCheckIn.logger.error("This part is currently checked out and cannot be deleted.");
        alert.showAndWait();
    }

    /**
     * Alert that the part is currently checked out, so it cannot be deleted
     */
    private void typeHasOneCheckedOutError(String partName) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("At least one " + partName + " is currently checked out, so " +
                partName + " parts cannot be deleted.");
        StudentCheckIn.logger.error("At least one " + partName + " is currently checked out, so " +
                partName + " parts cannot be deleted.");
        alert.showAndWait();
    }

    /**
     * Alert that the part is currently checked out, so it cannot be deleted
     */
    private void deleteAllCheckedOutPartAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("One part of this type is checked out. You cannot delete all of these parts.");
        StudentCheckIn.logger.error("One part of this type is checked out. You cannot delete all of these parts.");
        alert.showAndWait();
    }

}