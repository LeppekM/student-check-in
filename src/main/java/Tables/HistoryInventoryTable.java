package Tables;

import Database.ObjectClasses.Checkout;
import HelperClasses.ExportToExcel;
import Controllers.TableScreensController;
import Popups.Popup;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class HistoryInventoryTable extends TSCTable {

    private JFXTreeTableColumn<HIRow, String> studentNameCol, partNameCol, actionCol;
    private JFXTreeTableColumn<HIRow, Date> dateCol;
    private JFXTreeTableColumn<HIRow, Long> barcodeCol;

    public HistoryInventoryTable(TableScreensController controller) {
        super(controller);
    }

    @Override
    public void initialize() {
        NUM_COLS = 5;
        table.setPlaceholder(getEmptyTableLabel());

        studentNameCol = createNewCol("Student");
        studentNameCol.setCellValueFactory(col -> col.getValue().getValue().getStudentName());
        partNameCol = createNewCol("Part", 0.25);
        partNameCol.setCellValueFactory(col -> col.getValue().getValue().getPartName());
        barcodeCol = createNewCol("Barcode", 0.1);
        barcodeCol.setCellValueFactory(col -> col.getValue().getValue().getBarcode().asObject());
        actionCol = createNewCol("Action");
        actionCol.setCellValueFactory(col -> col.getValue().getValue().getAction());
        dateCol = createNewCol("Date", 0.25);
        dateCol.setCellValueFactory(col -> col.getValue().getValue().getDate());
        dateCol.setCellFactory(dateColFormat());

        setDoubleClickBehavior();
    }

    @Override
    public void export(ExportToExcel exportToExcel) {
        ObservableList<Checkout> list = database.getAllCheckoutHistory();
        exportToExcel.exportTransactionHistory(list);
    }

    @Override
    public void populateTable() {
        // clear previous data
        rows.clear();
        table.getColumns().clear();
        // get and add all rows
        ObservableList<Checkout> list = database.getAllCheckoutHistory();
        rows = FXCollections.observableArrayList();

        for (Checkout c : list) {
            rows.add(new HIRow(c.getStudentName().get(), c.getStudentEmail().get(), c.getPartName().get(),
                    c.getBarcode().get(), c.getAction().get(), c.getDate().get()));
        }
        root = new RecursiveTreeItem<>(rows, RecursiveTreeObject::getChildren);

        // unfortunately, this cast needs to be here to add the cols to the table
        TreeTableColumn<TableRow, String> studentNameTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) studentNameCol;
        TreeTableColumn<TableRow, String> partNameTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) partNameCol;
        TreeTableColumn<TableRow, Long> barcodeTemp = (TreeTableColumn<TableRow, Long>) (TreeTableColumn) barcodeCol;
        TreeTableColumn<TableRow, String> actionTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) actionCol;
        TreeTableColumn<TableRow, String> dateTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) dateCol;

        table.getColumns().setAll(studentNameTemp, partNameTemp, barcodeTemp, actionTemp, dateTemp);
        table.setRoot(root);
        table.setShowRoot(false);
    }

    @Override
    protected boolean isMatch(TableRow value, String filter) {
        HIRow val = (HIRow) value;
        String input = filter.toLowerCase();
        String student = val.getStudentName().getValue();
        String partName = val.getPartName().getValue();
        String serialNumber = val.getBarcode().getValue().toString();
        String action = val.getAction().getValue();
        String date = val.getDate().getValue().toString().toLowerCase();

        return ((student != null && student.toLowerCase().contains(input))
                || (partName != null && partName.toLowerCase().contains(input))
                || (serialNumber != null && serialNumber.toLowerCase().contains(input))
                || (action != null && action.toLowerCase().contains(input))
                || (date != null && date.toLowerCase().contains(input)));
    }

    @Override
    protected void popupRow(int index) {
        Stage stage = new Stage();

        VBox root = new VBox();
        Scene scene = new Scene(root);
        stage.setTitle("View Transaction");
        stage.initOwner(scene.getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        if (index != -1) {
            TreeItem item = table.getSelectionModel().getModelItem(index);
            // null if user clicks on empty row
            if (item != null) {
                HIRow row = ((HIRow) item.getValue());

                Popup historyPopup = new Popup(root) {

                    @Override
                    public void populate() {
                        add("Student Name: ", row.getStudentName().get(), false);
                        add("Student Email: ", row.getStudentEmail().get(), false);
                        add("Part Name: ", row.getPartName().get(), false);
                        add("Barcode: ", "" + row.getBarcode().get(), false);
                        add("Action: ", row.getAction().get(), false);
                        add("Date: ", new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(row.getDate().get()), false);

                        submitButton.setText("Close");
                    }

                    @Override
                    public void submit() {
                        stage.close();
                    }
                };

                stage.getIcons().add(new Image("images/msoe.png"));
                stage.show();
            }
        }
    }

    public void clearHistory() {
        if (this.worker != null && this.worker.isAdmin()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete Old History");
            alert.setContentText("Are you sure you want to clear the transaction history for parts older than 2 years?");
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                database.clearOldHistory();
                populateTable();
            }
        }
    }

    public class HIRow extends TableRow {
        private final StringProperty studentName;
        private final StringProperty studentEmail;
        private final StringProperty partName;
        private final LongProperty barcode;

        private final StringProperty action;
        private final ObjectProperty<Date> date;

        public HIRow(String studentName, String studentEmail, String partName, long barcode,
                                  String action, Date date) {
            this.studentName = new SimpleStringProperty(studentName);
            this.studentEmail = new SimpleStringProperty(studentEmail);
            this.partName = new SimpleStringProperty(partName);
            this.barcode = new SimpleLongProperty(barcode);
            this.action = new SimpleStringProperty(action);
            this.date = new SimpleObjectProperty<Date>(date) {
            };
        }

        public StringProperty getStudentName() {
            return studentName;
        }

        public StringProperty getStudentEmail() {
            return studentEmail;
        }

        public StringProperty getPartName() {
            return partName;
        }

        public LongProperty getBarcode() {
            return barcode;
        }

        public StringProperty getAction() {
            return action;
        }

        public ObjectProperty<Date> getDate() {
            return date;
        }
    }
}
