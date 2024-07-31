package Tables;

import Controllers.TableScreensController;
import HelperClasses.AutoCompleteTextField;
import HelperClasses.ExportToExcel;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

public class InventoryPartsTable extends TSCTable {

    private JFXTreeTableColumn<IPRow, String> serialCol;
    private JFXTreeTableColumn<IPRow, Long> barcodeCol;
    private JFXTreeTableColumn<IPRow, Boolean> isPresentCol;
    private AutoCompleteTextField searchField;
    private final AtomicReference<String> input = new AtomicReference<>("");

    public InventoryPartsTable(TableScreensController controller) {
        super(controller);
    }

    @Override
    public void initialize() {
        NUM_COLS = 3;
        table.setPlaceholder(getEmptyTableLabel());

        barcodeCol = createNewCol("Barcode");
        barcodeCol.setCellValueFactory(col -> col.getValue().getValue().getBarcode().asObject());
        barcodeCol.setCellFactory(column -> new TreeTableCell<IPRow, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Text text = new Text(String.format("%06d", item));
                    IPRow row = getTreeTableRow().getItem();
                    if (row != null) {
                        if (row.getIsPresent().get()) {
                            text.setFill(Color.FORESTGREEN);
                        } else {
                            text.setFill(Color.FIREBRICK);
                        }
                        setGraphic(text);
                    }
                }
            }
        });
        serialCol = createNewCol("Serial Number");
        serialCol.setCellValueFactory(col -> col.getValue().getValue().getSerialNumber());
        serialCol.setCellFactory(column -> new TreeTableCell<IPRow, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Text text = new Text(item);
                    IPRow row = getTreeTableRow().getItem();
                    if (row != null && row.getIsPresent().get()) {
                        text.setFill(Color.FORESTGREEN);
                    } else {
                        text.setFill(Color.FIREBRICK);
                    }
                    setGraphic(text);
                }
            }
        });
        isPresentCol = createNewCol("Is Present");
        isPresentCol.setCellValueFactory(param -> {
            TreeItem<IPRow> treeItem = param.getValue();
            IPRow tRow = treeItem.getValue();
            SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(tRow.getIsPresent().get());
            booleanProp.addListener((observable, oldValue, newValue) -> tRow.setIsPresent(newValue));
            return booleanProp;
        });
        isPresentCol.setCellFactory(p -> {
            CheckBoxTreeTableCell<IPRow, Boolean> cell = new CheckBoxTreeTableCell<>();
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        isPresentCol.setEditable(true);
        table.setEditable(true);

        searchField = controller.getSearchInput();
        searchField.initEntrySet(new TreeSet<>(database.getAllPartNames()));
        searchField.setOnKeyPressed(event -> {
            if (searchField.focusedProperty().get()) {
                if (event.getCode().equals(KeyCode.TAB)
                        || event.getCode().equals(KeyCode.ENTER)) {
                    searchField.setText(searchField.getFilteredEntries().get(0));
                    populateTable();
                    table.requestFocus();
                }
            }
        });

        controller.getScene().getScene().setOnKeyPressed(event -> {
            if (event.getCode().isDigitKey()) {
                input.updateAndGet(currentValue -> currentValue + event.getText());
            }
            if (input.get().trim().length() == 6) {
                int index = -1;
                for (int i = 0; i < rows.size(); i++) {
                    IPRow row = (IPRow) rows.get(i);
                    if (Long.parseLong(input.get().trim()) == row.getBarcode().get()) {
                        index = i;
                    }
                }
                if (index != -1) {
                    ((IPRow) rows.get(index)).setIsPresent(true);
                }
                input.set("");
            } else if (event.getCode().equals(KeyCode.BACK_SPACE)) {
                input.set("");
            }
        });

        rows = FXCollections.observableArrayList();

        setDoubleClickBehavior();
    }

    @Override
    public void export(ExportToExcel exportToExcel) {
        // does not export
    }

    @Override
    public void populateTable() {
        rows.clear();

        ArrayList<String> barcodes = database.getAllBarcodesForPartName(searchField.getText());
        int uniqueBarcodes = (int) barcodes.stream().distinct().count();
        if (uniqueBarcodes > 1) {
            ArrayList<String> serialNumbers = database.getAllSerialNumbersForPartName(searchField.getText());
            for (int i = 0; i < barcodes.size(); i++) {
                rows.add(new IPRow(Long.parseLong(barcodes.get(i)), serialNumbers.get(i)));
            }
        } else if (uniqueBarcodes == 1) {
            stageUtils.informationAlert("Only One Barcode", "Only one barcode is associated " +
                    "with this part name. The part is either unique or many parts use the same barcode.");
            searchField.requestFocus();
        }
        root = new RecursiveTreeItem<>(rows, RecursiveTreeObject::getChildren);

        TreeTableColumn<TableRow, Long> barcodeTemp = (TreeTableColumn<TableRow, Long>) (TreeTableColumn) barcodeCol;
        TreeTableColumn<TableRow, String> serialTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) serialCol;
        TreeTableColumn<TableRow, Boolean> isPresentTemp =
                (TreeTableColumn<TableRow, Boolean>) (TreeTableColumn) isPresentCol;

        table.getColumns().setAll(barcodeTemp, serialTemp, isPresentTemp);
        table.setRoot(root);
        table.setShowRoot(false);

        root.getChildren().forEach(item -> {
            IPRow row = (IPRow) item.getValue();
            row.getIsPresent().addListener((obs, oldVal, newVal) -> {
                table.refresh(); // Refresh the table to reflect changes
            });
        });
    }

    @Override
    protected boolean isMatch(TableRow value, String[] filter) {
        return false;  // this code cannot be reached, as search calls setTableSearch
    }

    @Override
    protected void popupRow(int index) {
        // does not have popup info
    }

    public class IPRow extends TableRow {
        private final LongProperty barcode;
        private final StringProperty serialNumber;
        private final BooleanProperty isPresent;

        public IPRow(long barcode, String serialNumber) {
            this.barcode = new SimpleLongProperty(barcode);
            this.serialNumber = new SimpleStringProperty(serialNumber);
            this.isPresent = new SimpleBooleanProperty(false);
        }

        public LongProperty getBarcode() {
            return barcode;
        }

        public StringProperty getSerialNumber() {
            return serialNumber;
        }

        public BooleanProperty getIsPresent() {
            return isPresent;
        }

        public void setIsPresent(boolean value) {
            isPresent.set(value);
        }
    }
}
