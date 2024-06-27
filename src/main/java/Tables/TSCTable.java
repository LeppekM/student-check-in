package Tables;

import Database.Database;
import Database.ObjectClasses.Worker;
import HelperClasses.ExportToExcel;
import HelperClasses.StageUtils;
import Controllers.TableScreensController;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

/**
 * This
 */
public abstract class TSCTable {

    protected static TableScreensController controller;

    protected JFXTreeTableView<TableRow> table;
    protected ObservableList<TableRow> rows = FXCollections.observableArrayList();
    protected TreeItem<TableRow> root;
    protected Worker worker;
    protected Database database = Database.getInstance();
    protected StageUtils stageUtils = StageUtils.getInstance();

    protected static double NUM_COLS;
    protected static double SCROLLBAR_BUFFER = 15;

    public TSCTable(TableScreensController controller) {
        TSCTable.controller = controller;
        table = controller.table;
    }

    public abstract void initialize();

    /**
     * This method exports the table as an Excel spreadsheet
     * @param exportToExcel the helper class made to export the
     */
    public abstract void export(ExportToExcel exportToExcel); // TODO: look into making export generic & if the class can be static

    /**
     * Adds the current worker to the class, so that the class knows whether an administrator
     * or student worker is currently logged in.
     *
     * @param worker the currently logged in worker
     */
    public void initWorker(Worker worker) {
        if (this.worker == null) {
            this.worker = worker;
        }
    }


    /**
     * Sets the values for each table column, empties the current table, then calls selectParts to populate it.
     */
    public abstract void populateTable();

    public void filter(String filter, TreeItem<TableRow> filteredRoot) {
        filter(root, filter, filteredRoot);
    }

    /**
     * Determines which rows fit the search input
     */
    private void filter(TreeItem<TableRow> root, String filter, TreeItem<TableRow> filteredRoot) {
        TreeItem<TableRow> filteredChild;
        for (TreeItem<TableRow> child : root.getChildren()) {
            filteredChild = new TreeItem<>();
            filteredChild.setValue(child.getValue());
            filteredChild.setExpanded(true);
            filter(child, filter, filteredChild);
            if (!filteredChild.getChildren().isEmpty() || isMatch(filteredChild.getValue(), filter)) {
                filteredRoot.getChildren().add(filteredChild);
            }
        }
    }

    /**
     * Determines whether a specific row matches the search input
     *
     * @param value  the tested row
     * @param filter the search input
     * @return true if the row matches the search criteria; false otherwise
     */
    protected abstract boolean isMatch(TableRow value, String filter);

    /**
     * Displays a pop-up for viewing everything about the part
     * @param index index in the table for the part to be viewed
     */
    protected abstract void popupRow(int index);


    /**
     * Creates a new column for the table with
     * @param colName
     * @return
     */
    protected JFXTreeTableColumn createNewCol(String colName) {
        JFXTreeTableColumn tempCol = new JFXTreeTableColumn<>(colName);
        tempCol.prefWidthProperty().bind(table.widthProperty().subtract(SCROLLBAR_BUFFER).divide(NUM_COLS));
        tempCol.setStyle("-fx-font-size: 18px");
        tempCol.setResizable(false);

        return tempCol;
    }

    protected Label getEmptyTableLabel() {
        Label emptyTableLabel = new Label("No parts found.");
        emptyTableLabel.setStyle("-fx-text-fill: white");
        emptyTableLabel.setFont(new Font(18));
        return emptyTableLabel;
    }

    protected void setDoubleClickBehavior() {
        table.setRowFactory(param -> {
            final TreeTableRow<TableRow> row = new TreeTableRow<>();
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (event.getClickCount() == 2) {
                    popupRow(row.getIndex());
                } else {
                    final int index = row.getIndex();
                    if (index >= 0 && index < table.getCurrentItemsCount() && table.getSelectionModel().isSelected(index)) {
                        table.getSelectionModel().clearSelection();
                        event.consume();
                    }
                }
            });
            return row;
        });
    }

    public class TableRow extends RecursiveTreeObject<TableRow> {

    }

}
