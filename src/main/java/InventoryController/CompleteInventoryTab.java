package InventoryController;

import Database.ObjectClasses.DBObject;
import Database.ObjectClasses.Part;
import HelperClasses.ExportToExcel;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static InventoryController.ControllerInventoryPage.database;


/**
 * This class manages backend functionality for the Tab labeled Total Inventory in the inventory page
 */
public class CompleteInventoryTab extends TSCTable {

    public CompleteInventoryTab(TableScreensController controller) {
        super(controller);
    }

    @Override
    public void initialize() {

    }

    @Override
    public ObservableList<DBObject> getParts() {
        return null;
    }

    @Override
    public void export(ExportToExcel exportToExcel) {

    }

    @Override
    public void populateTable() {
        super.populateTable();
        //stuff
    }

    @Override
    protected boolean isMatch(TableRow value, String filter) {
        return false; //todo
    }

    /** TODO: fix/detatch from FXML
     * Called to bring up the "AddPart" FXML scene.
     */
    public void addPart(Window owner) {
        Stage stage = new Stage();
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("fxml/AddPart.fxml");
            Parent root = FXMLLoader.load(myFxmlURL);
            Scene scene = new Scene(root, 400, 450);
            stage.setTitle("Add a Part");
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> {
                populateTable();
                stage.close();
            });
            stage.show();
        } catch (IOException e) {
            StudentCheckIn.logger.error("IOException: Loading Add Part.");
            e.printStackTrace();
        }
    }

    /** TODO dc from fxmls
     * Called when a row is highlighted in the table and the edit button is clicked.
     */
    public void editPart(String partID, boolean isBatchEdit, Window owner) {
        try {
            Part part = database.selectPart(Integer.parseInt(partID));
            FXMLLoader loader;
            if (isBatchEdit) {
                loader = new FXMLLoader(getClass().getResource("/fxml/EditPartType.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("/fxml/EditOnePart.fxml"));
            }
            Parent root = loader.load();
            ((ControllerEditPart) loader.getController()).initPart(part);
            Scene scene = new Scene(root, 400, 500);
            Stage stage = new Stage();
            stage.setMinWidth(400);
            stage.setMaxWidth(400);
            stage.setMaxHeight(550);
            stage.setMinHeight(550);
            if (isBatchEdit) {
                stage.setTitle("Edit all " + part.getPartName());
            } else {
                String partName = part.getPartName();
                if (part.getPartName().substring(part.getPartName().length() - 1).equals("s")) {
                    partName = part.getPartName().substring(0, part.getPartName().length() - 1);
                }
                stage.setTitle("Edit a " + partName);
            }
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("images/msoe.png"));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    currentFilters.add("All");
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
     * This method calls the database method to soft delete an item from the inventory list
     * this then updates the gui table
     */
    public void deletePart(String partID) {
        database.initWorker(worker);
        try {
            if (database.selectPart(Integer.parseInt(partID)) != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you wish to delete the part with ID = " + partID + "?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    database.deleteItem(Integer.parseInt(partID));
                    populateTable();
                }
            }
        } catch (Exception e) {
            StudentCheckIn.logger.error("Exception while deleting part.");
            e.printStackTrace();
        }
    }

    public void deletePartType(String partName) {
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
    }

    /**
     * Alert that the part is currently checked out, so it cannot be deleted
     */
    private void deleteCheckedOutPartAlert() {
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
        StudentCheckIn.logger.error("At least one {} is currently checked out, so {} parts cannot be deleted.", partName, partName);
        alert.showAndWait();
    }

    /**
     * Alert that the part is currently checked out, so it cannot be deleted
     */
    private void deleteAllCheckedOutPartAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("One part of this type is checked out. You cannot delete all of these parts.");
        StudentCheckIn.logger.error("One part of this type is checked out. You cannot delete all of these parts.");
        alert.showAndWait();
    }
}
