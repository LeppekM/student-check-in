package Tables;

import App.StudentCheckIn;
import Database.ObjectClasses.Part;
import HelperClasses.ExportToExcel;
import Controllers.TableScreensController;
import HelperClasses.StageUtils;
import Popups.EditPartController;
import Popups.ViewTotalPartController;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;


/**
 * This class manages backend functionality for the Tab labeled Total Inventory in the inventory page
 */
public class CompleteInventoryTable extends TSCTable {

    private JFXTreeTableColumn<CIRow, String> partNameCol, locationCol, serialNumberCol;
    private JFXTreeTableColumn<CIRow, Integer> partIDCol;
    private JFXTreeTableColumn<CIRow, Long> barcodeCol;

    public CompleteInventoryTable(TableScreensController controller) {
        super(controller);
    }

    @Override
    public void initialize() {
        NUM_COLS = 5;
        table.setPlaceholder(getEmptyTableLabel());

        partNameCol = createNewCol("Part Name");
        partNameCol.setCellValueFactory(col -> col.getValue().getValue().getPartName());
        serialNumberCol = createNewCol("Serial Number");
        serialNumberCol.setCellValueFactory(col -> col.getValue().getValue().getSerialNumber());
        locationCol = createNewCol("Location");
        locationCol.setCellValueFactory(col -> col.getValue().getValue().getLocation());
        barcodeCol = createNewCol("Barcode");
        barcodeCol.setCellValueFactory(col -> col.getValue().getValue().getBarcode().asObject());
        partIDCol = createNewCol("Part ID");
        partIDCol.setCellValueFactory(col -> col.getValue().getValue().getPartID().asObject());

        rows = FXCollections.observableArrayList();

        setDoubleClickBehavior();
    }

    @Override
    public void export(ExportToExcel exportToExcel) {
        ObservableList<Part> list = database.getAllParts();  //todo: this is questionable, want to export current view w filters or all?
        exportToExcel.exportPartList(list);
    }

    @Override
    public void populateTable() {
        // clear previous data
        rows.clear();
        table.getColumns().clear();
        // get and add all rows
        // todo add cache
        ObservableList<Part> list = database.getAllParts();
        for (Part part : list) {
            rows.add(new CIRow(part.getPartID(), part.getBarcode(), part.getSerialNumber(), part.getLocation(),
                    part.getPartName(), part.getPrice()));
        }
        root = new RecursiveTreeItem<>(rows, RecursiveTreeObject::getChildren);

        // unfortunately, this cast needs to be here to add the cols to the table
        TreeTableColumn<TableRow, String> partNameTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) partNameCol;
        TreeTableColumn<TableRow, Long> barcodeTemp = (TreeTableColumn<TableRow, Long>) (TreeTableColumn) barcodeCol;
        TreeTableColumn<TableRow, String> serialNumberTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) serialNumberCol;
        TreeTableColumn<TableRow, String> locationTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) locationCol;
        TreeTableColumn<TableRow, Integer> partIDTemp = (TreeTableColumn<TableRow, Integer>) (TreeTableColumn) partIDCol;

        table.getColumns().setAll(partNameTemp, barcodeTemp, serialNumberTemp, locationTemp, partIDTemp);
        table.setRoot(root);
        // needs to be false so that it doesn't group all elements, effectively hiding them until you drop them down
        table.setShowRoot(false);
    }

    @Override
    protected boolean isMatch(TableRow value, String filter) {
        CIRow val = (CIRow) value;
        String input = filter.toLowerCase();
        String partName = val.getPartName().getValue();
        String serialNumber = val.getSerialNumber().getValue();
        String loc = val.getLocation().getValue();
        String barcode = val.getBarcode().getValue().toString();
        String partID = val.getPartID().getValue().toString();

        return ((partName != null && partName.toLowerCase().contains(input))
                || (serialNumber != null && serialNumber.toLowerCase().contains(input))
                || (loc != null && loc.toLowerCase().contains(input))
                || barcode.toLowerCase().contains(input)
                || partID.toLowerCase().contains(input));
    }

    @Override
    protected void popupRow(int index) {
        Stage stage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewTotalPart.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("View Part");
            stage.initOwner(scene.getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            if (index != -1) {
                TreeItem item = table.getSelectionModel().getModelItem(index);
                // null if user clicks on empty row
                if (item != null) {
                    CompleteInventoryTable.CIRow row = ((CompleteInventoryTable.CIRow) item.getValue());
                    ((ViewTotalPartController) loader.getController()).populate(row);
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getRowPartID(int row) {
        return partIDCol.getCellData(row);
    }

    public void addPart() {
        Stage stage = StageUtils.getInstance().createPopupStage("fxml/AddPart.fxml", controller.getScene(), "Add a Part");
        stage.setOnCloseRequest(event -> {
            populateTable();
            stage.close();
        });
        stage.show();
    }

    public void editPart() {
        if (!table.getSelectionModel().getSelectedCells().isEmpty()) {
            if ((worker != null && (worker.canEditParts() || worker.isAdmin()))
                    || StageUtils.getInstance().requestAdminPin("edit a part", controller.getScene())) {

                int row = table.getSelectionModel().getFocusedIndex();
                int partID = getRowPartID(row);
                Part part = database.selectPart(partID);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditOnePart.fxml"));

                try {
                    Parent root = loader.load();
                    ((EditPartController) loader.getController()).initPart(part);
                    Scene scene = new Scene(root, 400, 500);
                    Stage stage = new Stage();
                    stage.setMinWidth(400);
                    stage.setMaxWidth(400);
                    stage.setMaxHeight(550);
                    stage.setMinHeight(550);
                    String partName = part.getPartName();
                    if (part.getPartName().endsWith("s")) {
                        partName = part.getPartName().substring(0, part.getPartName().length() - 1);
                    }
                    stage.setTitle("Edit a " + partName);
                    stage.initOwner(controller.getScene().getScene().getWindow());
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.setScene(scene);
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.setOnCloseRequest(ev -> {
                        populateTable();
                        stage.close();
                    });
                    stage.show();
                } catch (IOException e) {
                    StudentCheckIn.logger.error("IOException: Loading Edit Part.");
                    e.printStackTrace();
                }
            }
        }
    }

    public void editPartType() {
        if (!table.getSelectionModel().getSelectedCells().isEmpty()) {
            int row = table.getSelectionModel().getFocusedIndex();
            int partID = getRowPartID(row);
            Part part = database.selectPart(partID);

            if ((worker != null && (worker.canEditParts() || worker.isAdmin()))
                    || StageUtils.getInstance().requestAdminPin("edit all parts named " + part.getPartName(), controller.getScene())) {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditPartType.fxml"));
                try {
                    Parent root = loader.load();
                    ((EditPartController) loader.getController()).initPart(part);
                    Scene scene = new Scene(root, 400, 500);
                    Stage stage = new Stage();
                    stage.setMinWidth(400);
                    stage.setMaxWidth(400);
                    stage.setMaxHeight(550);
                    stage.setMinHeight(550);
                    stage.setTitle("Edit all " + part.getPartName());
                    stage.initOwner(controller.getScene().getScene().getWindow());
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.setScene(scene);
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.setOnCloseRequest(ev -> {
                        populateTable();
                        stage.close();
                    });
                    stage.show();
                } catch (IOException e) {
                    StudentCheckIn.logger.error("IOException: Loading Edit Part.");
                    e.printStackTrace();
                }
            }
        }
    }

    public void deletePart() {
        if (!table.getSelectionModel().getSelectedCells().isEmpty()) {
            int row = table.getSelectionModel().getFocusedIndex();
            int partID = getRowPartID(row);
            Part part = database.selectPart(partID);

            if ((worker != null && (worker.canRemoveParts() || worker.isAdmin())) || stageUtils.requestAdminPin("Delete a Part", controller.getScene())) {
                if (!part.getCheckedOut()) {
                    database.initWorker(worker);
                    try {
                        if (database.selectPart(partID) != null) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you wish to delete the part with ID = " + partID + "?", ButtonType.YES, ButtonType.NO);
                            alert.showAndWait();
                            if (alert.getResult() == ButtonType.YES) {
                                database.deletePart(partID);
                                populateTable();
                            }
                        }
                    } catch (Exception e) {
                        StudentCheckIn.logger.error("Exception while deleting part.");
                        e.printStackTrace();
                    }
                } else {
                    stageUtils.errorAlert("This part is currently checked out and cannot be deleted.");
                }
            }
        }
    }

    public void deletePartType() {
        int row = table.getSelectionModel().getFocusedIndex();
        int partID = getRowPartID(row);
        Part part = database.selectPart(partID);

        if ((worker != null && (worker.canRemoveParts() || worker.isAdmin())) || stageUtils.requestAdminPin("delete parts", controller.getScene())) {
            boolean typeHasOneCheckedOut = false;
            ArrayList<String> partIDs = database.getAllPartIDsForPartName("" + part.getPartID());
            for (String id : partIDs) {
                if (database.getIsCheckedOut(id)) {
                    typeHasOneCheckedOut = true;
                }
            }
            String partName = part.getPartName();
            if (!typeHasOneCheckedOut) {
                database.initWorker(worker);
                try {
                    if (database.hasPartName(partName)) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you wish to delete all parts named: " + partName + "?", ButtonType.YES, ButtonType.NO);
                        alert.showAndWait();
                        if (alert.getResult() == ButtonType.YES) {
                            database.deleteParts(partName);
                            populateTable();
                        }
                    }
                } catch (Exception e) {
                    StudentCheckIn.logger.error("Exception while deleting part type.");
                    e.printStackTrace();
                }
            } else {
                stageUtils.errorAlert("At least one " + partName + " is currently checked out, so "
                        + partName + " parts cannot be deleted.");
            }
        }
    }

    public class CIRow extends TableRow {
        private final StringProperty partName;
        private final StringProperty location;
        private final StringProperty serialNumber;
        private final IntegerProperty partID;
        private final DoubleProperty price;
        private final LongProperty barcode;

        public CIRow(int partID, long barcode, String serialNumber, String location, String partName, double price){
            this.partID = new SimpleIntegerProperty(partID);
            this.barcode = new SimpleLongProperty(barcode);
            this.serialNumber = new SimpleStringProperty(serialNumber);
            this.location = new SimpleStringProperty(location);
            this.partName = new SimpleStringProperty(partName);
            this.price = new SimpleDoubleProperty(price);
        }

        public DoubleProperty getPrice() {
            return price;
        }

        public StringProperty getPartName() {
            return partName;
        }

        public LongProperty getBarcode() {
            return barcode;
        }

        public StringProperty getSerialNumber() {
            return serialNumber;
        }

        public StringProperty getLocation() {
            return location;
        }

        public IntegerProperty getPartID() {
            return partID;
        }
    }
}
