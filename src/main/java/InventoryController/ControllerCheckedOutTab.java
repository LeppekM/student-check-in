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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private JFXTreeTableColumn<CheckedOutTabTableRow, String> studentNameCol, partNameCol;

    private JFXTreeTableColumn<CheckedOutTabTableRow, Date> dueDateCol, checkedOutAtCol;

    @FXML
    private JFXTreeTableColumn<CheckedOutTabTableRow, Long> barcodeCol;

    @FXML
    private JFXButton searchButton;

    private String studentName, partName, barcode, checkedOutAt, dueDate;

    private CheckedOutParts checkedOutParts;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label emptyTableLabel = new Label("No parts found.");
        emptyTableLabel.setStyle("-fx-text-fill: white");
        emptyTableLabel.setFont(new Font(18));
        checkedOutTable.setPlaceholder(emptyTableLabel);

        studentNameCol = new JFXTreeTableColumn<>("Student");
        studentNameCol.prefWidthProperty().bind(checkedOutTable.widthProperty().divide(5));
        studentNameCol.setStyle("-fx-font-size: 18px");
        studentNameCol.setResizable(false);
        studentNameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String> param) {
                return param.getValue().getValue().getStudentName();
            }
        });

        partNameCol = new JFXTreeTableColumn<>("Part Name");
        partNameCol.prefWidthProperty().bind(checkedOutTable.widthProperty().divide(5));
        partNameCol.setStyle("-fx-font-size: 18px");
        partNameCol.setResizable(false);
        partNameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, String> param) {
                return param.getValue().getValue().getPartName();
            }
        });

        barcodeCol = new JFXTreeTableColumn<>("Barcode");
        barcodeCol.prefWidthProperty().bind(checkedOutTable.widthProperty().divide(5));
        barcodeCol.setStyle("-fx-font-size: 18px");
        barcodeCol.setResizable(false);
        barcodeCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, Long>, ObservableValue<Long>>() {
            @Override
            public ObservableValue<Long> call(TreeTableColumn.CellDataFeatures<CheckedOutTabTableRow, Long> param) {
                return param.getValue().getValue().getBarcode().asObject();
            }
        });

        checkedOutAtCol = new JFXTreeTableColumn<>("Check Out Date");
        checkedOutAtCol.prefWidthProperty().bind(checkedOutTable.widthProperty().divide(5));
        checkedOutAtCol.setStyle("-fx-font-size: 18px");
        checkedOutAtCol.setResizable(false);
        checkedOutAtCol.setCellValueFactory(col -> col.getValue().getValue().getDueDate());
        checkedOutAtCol.setCellFactory(col -> new TreeTableCell<CheckedOutTabTableRow, Date>(){
            @Override
            protected void updateItem(Date date, boolean empty){
                if (empty) {
                    setText("");
                } else {
                    setText(new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(date));
                }
            }
        });

        dueDateCol = new JFXTreeTableColumn<>("Due Date");
        dueDateCol.prefWidthProperty().bind(checkedOutTable.widthProperty().divide(5));
        dueDateCol.setStyle("-fx-font-size: 18px");
        dueDateCol.setResizable(false);
        dueDateCol.setCellValueFactory(col -> col.getValue().getValue().getDueDate());
        dueDateCol.setCellFactory(col -> new TreeTableCell<CheckedOutTabTableRow, Date>(){
            @Override
            protected void updateItem(Date date, boolean empty){
                if (empty) {
                    setText("");
                } else {
                    setText(new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(date));
                }
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
                        //final int index = row.getIndex();
                        if (event.getClickCount() == 2) {
                            viewPart(row.getIndex());
                        } else {
                            final int index = row.getIndex();
                            if (index >= 0 && index < checkedOutTable.getCurrentItemsCount() && checkedOutTable.getSelectionModel().isSelected(index)) {
                                checkedOutTable.getSelectionModel().clearSelection();
                                event.consume();
                            }
                        }
                    }
                });
                return row;
            }
        });
    }

    public void exportCheckedOut(){
        checkedOutParts = new CheckedOutParts();
        ObservableList<CheckedOutItems> list = checkedOutParts.getCheckedOutItems();
        export.exportCheckedOut(list);
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
                    list.get(i).getStudentEmail().get(), list.get(i).getPartName().getValue(),
                    Long.valueOf(list.get(i).getBarcode().getValue()), list.get(i).getSerialNumber().get(),
                    list.get(i).getPartID().get(), list.get(i).getCheckedOutDate().get(),
                    list.get(i).getDueDate().get(), list.get(i).getFee().getValue()));
        }

        root = new RecursiveTreeItem<CheckedOutTabTableRow>(
                tableRows, RecursiveTreeObject::getChildren
        );

        checkedOutTable.getColumns().setAll(studentNameCol, partNameCol, barcodeCol,
                checkedOutAtCol, dueDateCol);
        checkedOutTable.setRoot(root);
        checkedOutTable.setShowRoot(false);
    }

    private void viewPart(int index) {
        Stage stage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewCheckedOutPart.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("View Checked Out Part");
            stage.initOwner(checkedOutPage.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            if (index != -1) {
                TreeItem item = checkedOutTable.getSelectionModel().getModelItem(index);
                // null if user clicks on empty row
                if (item != null) {
                    CheckedOutTabTableRow row = ((CheckedOutTabTableRow) item.getValue());
                    ((ControllerViewCheckedOutPart) loader.getController()).populate(row);
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.show();
                }
            }
//                stage.setOnHiding(event1 -> fees.setText("Outstanding fees: $" + overdueFee(student)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void search() {
        checkedOutTable.setPredicate(new Predicate<TreeItem<CheckedOutTabTableRow>>() {
            @Override
            public boolean test(TreeItem<CheckedOutTabTableRow> tableRow) {
                String input = searchInput.getText().toLowerCase();
                studentName = tableRow.getValue().getStudentName().getValue();
                partName = tableRow.getValue().getPartName().getValue();
                barcode = tableRow.getValue().getBarcode().getValue().toString();
                checkedOutAt = tableRow.getValue().getCheckedOutAt().getValue().toString();
                dueDate = tableRow.getValue().getDueDate().getValue().toString();

                return ((studentName != null && studentName.toLowerCase().contains(input))
                        || (partName != null && partName.toLowerCase().contains(input))
                        || (barcode != null && barcode.toLowerCase().contains(input))
                        || (checkedOutAt != null && checkedOutAt.toLowerCase().contains(input))
                        || (dueDate != null && dueDate.toLowerCase().contains(input)));
            }
        });
    }

}