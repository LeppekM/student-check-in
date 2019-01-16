package InventoryController;

import Database.CheckedOutParts;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * This class acts as the controller for the checked out items part of the inventory tab
 */
public class ControllerCheckedOutTab  extends ControllerInventoryPage implements Initializable {

    @FXML
    private AnchorPane checkedOutPage;

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

    private String studentName, partName, barcode, checkedOutAt, dueDate;

    private CheckedOutParts checkedOutParts;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label emptyTableLabel = new Label("No parts found.");
        emptyTableLabel.setFont(new Font(18));
        checkedOutTable.setPlaceholder(emptyTableLabel);

        studentNameCol = new JFXTreeTableColumn<>("Student");
        studentNameCol.setPrefWidth(150);
        studentNameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String> param) {
                return param.getValue().getValue().getStudentName();
            }
        });

        partNameCol = new JFXTreeTableColumn<>("Part Name");
        partNameCol.setPrefWidth(200);
        partNameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String> param) {
                return param.getValue().getValue().getPartName();
            }
        });

        barcodeCol = new JFXTreeTableColumn<>("Barcode");
        barcodeCol.setPrefWidth(150);
        barcodeCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String> param) {
                return param.getValue().getValue().getBarcode();
            }
        });

        checkedOutAtCol = new JFXTreeTableColumn<>("Check Out Date");
        checkedOutAtCol.setPrefWidth(150);
        checkedOutAtCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String> param) {
                return param.getValue().getValue().getCheckedOutAt();
            }
        });

        dueDateCol = new JFXTreeTableColumn<>("Due Date");
        dueDateCol.setPrefWidth(150);
        dueDateCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String> param) {
                return param.getValue().getValue().getDueDate();
            }
        });

        tableRows = FXCollections.observableArrayList();

        searchInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                checkedOutTable.setPredicate(new Predicate<TreeItem<CheckedOutTabTableRow>>() {
                    @Override
                    public boolean test(TreeItem<CheckedOutTabTableRow> tableRow) {
                        String input = newValue.toLowerCase();
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
        });

        // Click to select if unselected and deselect if selected
        checkedOutTable.setRowFactory(new Callback<TreeTableView<CheckedOutTabTableRow>, TreeTableRow<CheckedOutTabTableRow>>() {
            @Override
            public TreeTableRow<CheckedOutTabTableRow> call(TreeTableView<CheckedOutTabTableRow> param) {
                final TreeTableRow<CheckedOutTabTableRow> row = new TreeTableRow<>();
                row.addEventFilter(MouseEvent.MOUSE_PRESSED, (EventHandler<MouseEvent>) event -> {
                    final int index = row.getIndex();
                    if (index >= 0 && index < checkedOutTable.getCurrentItemsCount() && checkedOutTable.getSelectionModel().isSelected(index)) {
                        checkedOutTable.getSelectionModel().clearSelection();
                        event.consume();
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
                    list.get(i).getCheckedOutAt().getValue(), list.get(i).getDueDate().getValue()));
        }

        root = new RecursiveTreeItem<CheckedOutTabTableRow>(
                tableRows, RecursiveTreeObject::getChildren
        );

        checkedOutTable.getColumns().setAll(studentNameCol, partNameCol, barcodeCol,
                checkedOutAtCol, dueDateCol);
        checkedOutTable.setRoot(root);
        checkedOutTable.setShowRoot(false);
    }

    /**
     * This method brings up the FXML page for showing the info about the selected part
     *
     * @author Matthew Karcz
     */
    public void showInfoPage(CheckedOutItems part){
        Stage stage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ShowPart.fxml"));
            Parent root = loader.load();
            ((ControllerShowPart) loader.getController()).initPart(part);
            Scene scene = new Scene(root, 400, 400);
            stage.setTitle("Part Information");
            stage.initOwner(checkedOutPage.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("images/msoe.png"));
            stage.showAndWait();
            populateTable();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}