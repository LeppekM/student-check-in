package Controllers;

import Database.Database;
import Database.ObjectClasses.Worker;
import HelperClasses.ExportToExcel;
import HelperClasses.StageUtils;
import Tables.*;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class TableScreensController extends MenuController implements IController, Initializable {

    @FXML
    public Label titleLabel;

    @FXML
    public JFXTabPane tabPane;

    @FXML
    private VBox scene;

    @FXML
    TextField searchInput;

    @FXML
    public JFXTreeTableView<TSCTable.TableRow> table;

    @FXML
    private Button backButton, excelButton, menuButton1, menuButton2, menuButton3, menuButton4, menuButton5;

    protected static Database database = Database.getInstance();
    private final StageUtils stageUtils = StageUtils.getInstance();
    private final ExportToExcel export = new ExportToExcel();
    private Worker worker;
    private TSCTable tscTable;
    private TableScreen screen;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // init tabPane listeners
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.getText().equals("Total Inventory")) {
                    screen = TableScreen.COMPLETE_INVENTORY;
                } else if (newValue.getText().equals("Transaction History")) {
                    screen = TableScreen.HISTORY;
                } else if (newValue.getText().equals("Checked Out")) {
                    screen = TableScreen.CHECKED_OUT;
                } else {
                    screen = TableScreen.OVERDUE;
                }
            }
            reloadScreen();
        });
        tabPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            tabPane.setTabMinWidth(tabPane.getWidth() / 4.5);
            tabPane.setTabMaxWidth(tabPane.getWidth() / 4.5);
        });

        backButton.getStylesheets().add("/css/CheckButton.css");  // set button style

        // pairing button onAction behavior to correct methods
        menuButton1.setOnAction(event -> {
            if (screen == TableScreen.COMPLETE_INVENTORY) {
                deleteManyParts();
            } else if (screen == TableScreen.HISTORY) {
                clearHistory();
            } else if (screen == TableScreen.STUDENTS) {
                deleteStudent();
            } else if (screen == TableScreen.WORKERS) {
                deleteWorker();
            }
        });

        menuButton2.setOnAction(event -> {
            if (screen == TableScreen.COMPLETE_INVENTORY) {
                deletePart();
            } else if (screen == TableScreen.STUDENTS) {
                addStudent();
            } else if (screen == TableScreen.WORKERS) {
                addAdmin();
            }
        });

        menuButton3.setOnAction(event -> {
            if (screen == TableScreen.COMPLETE_INVENTORY) {
                editManyParts();
            } else if (screen == TableScreen.STUDENTS) {
                importStudents();
            } else if (screen == TableScreen.WORKERS) {
                addWorker();
            }
        });

        // add deactivate/activate behavior for buttons which need it
        table.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> disableButtons(false));

        // Updates the search if the user presses enter with the cursor in the search field
        searchInput.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                search();
            }
        });
    }

    private void reloadScreen() {
        if (screen == TableScreen.STUDENTS || screen == TableScreen.WORKERS) {
            tabPane.setVisible(false);
            tabPane.setMaxHeight(0);
        } else {
            tabPane.setVisible(true);
            tabPane.setMaxHeight(42);
        }

        disableButtons(true);

        switch (screen) {
            case COMPLETE_INVENTORY:
                tscTable = new CompleteInventoryTable(this);
                titleLabel.setText("Total Inventory");
                excelButton.setVisible(true);
                menuButton1.setVisible(true);
                menuButton1.setText("Delete Part Type");
                menuButton2.setVisible(true);
                menuButton2.setText("Delete");
                menuButton3.setVisible(true);
                menuButton3.setText("Edit Part Type");
                menuButton4.setVisible(true);
                menuButton4.setText("Edit");
                menuButton5.setVisible(true);
                menuButton5.setText("Add");
                break;
            case HISTORY:
                tscTable = new HistoryInventoryTable(this);
                titleLabel.setText("Transaction History");
                excelButton.setVisible(true);
                menuButton1.setVisible(true);
                menuButton1.setText("Clear History");
                menuButton2.setVisible(false);
                menuButton3.setVisible(false);
                menuButton4.setVisible(false);
                menuButton5.setVisible(false);
                break;
            case CHECKED_OUT:
                tscTable = new CheckedOutInventoryTable(this);
                titleLabel.setText("Checked Out");
                excelButton.setVisible(true);
                menuButton1.setVisible(false);
                menuButton2.setVisible(false);
                menuButton3.setVisible(false);
                menuButton4.setVisible(false);
                menuButton5.setVisible(false);
                break;
            case OVERDUE:
                tscTable = new OverdueInventoryTable(this);
                titleLabel.setText("Overdue Parts");
                excelButton.setVisible(true);
                menuButton1.setVisible(false);
                menuButton2.setVisible(false);
                menuButton3.setVisible(false);
                menuButton4.setVisible(false);
                menuButton5.setVisible(false);
                break;
            case WORKERS:
                tscTable = new ManageWorkersTable(this);
                titleLabel.setText("Manage Workers");
                excelButton.setVisible(false);
                menuButton1.setVisible(true);
                menuButton1.setText("Delete");
                menuButton2.setVisible(true);
                menuButton2.setText("Add Admin");
                menuButton3.setVisible(true);
                menuButton3.setText("Add Worker");
                menuButton4.setVisible(false);
                menuButton5.setVisible(false);
                break;
            case STUDENTS:
                tscTable = new ManageStudentsTable(this);
                titleLabel.setText("Manage Students");
                excelButton.setVisible(false);
                menuButton1.setVisible(true);
                menuButton1.setText("Delete");
                menuButton2.setVisible(true);
                menuButton2.setText("Add");
                menuButton3.setVisible(true);
                menuButton3.setText("Import Students");
                menuButton4.setVisible(false);
                menuButton5.setVisible(false);
                break;
        }
        tscTable.initialize();
        tscTable.populateTable();
    }

    private void disableButtons(boolean bool) {
        if (screen == TableScreen.COMPLETE_INVENTORY) {
            menuButton1.setDisable(bool);
            menuButton2.setDisable(bool);
            menuButton3.setDisable(bool);
            menuButton4.setDisable(bool);
        } else if (screen == TableScreen.HISTORY) {
            menuButton1.setDisable(!worker.isAdmin());
        } else if (screen == TableScreen.STUDENTS) {
            menuButton1.setDisable(bool);
        } else if (screen == TableScreen.WORKERS) {
            menuButton1.setDisable(bool);
        }
    }

    /**
     * Used to keep track of which worker is currently logged in by passing the worker into
     * each necessary class
     * @param worker the currently logged in worker
     */
    @Override
    public void initWorker(Worker worker) {
        if (this.worker == null) {
            this.worker = worker;
        }
    }

    /**
     * Updates the table based on the input text for the search
     */
    @FXML
    private void search() {
        String filter = searchInput.getText();
        String[] filters = filter.split(" ");
        if (!filter.isEmpty()) {
            TreeItem<TSCTable.TableRow> filteredRoot = new TreeItem<>();
            for (String f : filters) {
                f = f.trim();
                tscTable.filter(f, filteredRoot);
                table.setRoot(filteredRoot);
            }
        }
        disableButtons(true);
    }

    @FXML
    public void goBack() {
        stageUtils.goBack(scene, worker);
    }

    /**
     * This exports the current table of the inventory as an Excel file
     */
    @FXML
    public void export() {
        tscTable.export(export);
    }

    @FXML
    public void addPart() {
        if (tscTable instanceof CompleteInventoryTable) {
            ((CompleteInventoryTable) tscTable).addPart();
        }
    }

    public void deleteManyParts() {
        if (tscTable instanceof CompleteInventoryTable) {
            ((CompleteInventoryTable) tscTable).deletePartType();
        }
    }

    public void deletePart() {
        if (tscTable instanceof CompleteInventoryTable) {
            ((CompleteInventoryTable) tscTable).deletePart();
        }
    }

    public void editManyParts() {
        if (tscTable instanceof CompleteInventoryTable) {
            ((CompleteInventoryTable) tscTable).editPartType();
        }
    }

    @FXML
    public void editPart() {
        if (tscTable instanceof CompleteInventoryTable) {
            ((CompleteInventoryTable) tscTable).editPart();
        }
    }

    public void clearHistory() {
        if (tscTable instanceof HistoryInventoryTable) {
            ((HistoryInventoryTable) tscTable).clearHistory();
        }
    }

    public void importStudents() {
        if (tscTable instanceof ManageStudentsTable) {
            ((ManageStudentsTable) tscTable).importStudents();
        }
    }

    public void addStudent() {
        if (tscTable instanceof ManageStudentsTable) {
            ((ManageStudentsTable) tscTable).addStudent();
        }
    }

    public void deleteStudent() {
        if (tscTable instanceof ManageStudentsTable) {
            ((ManageStudentsTable) tscTable).deleteStudent();
        }
    }

    public void addWorker() {
        if (tscTable instanceof ManageWorkersTable) {
            ((ManageWorkersTable) tscTable).addWorker();
        }
    }

    public void addAdmin() {
        if (tscTable instanceof ManageWorkersTable) {
            ((ManageWorkersTable) tscTable).addAdmin();
        }
    }

    public void deleteWorker() {
        if (tscTable instanceof ManageWorkersTable) {
            ((ManageWorkersTable) tscTable).deleteWorker();
        }
    }

    public VBox getScene() {
        return scene;
    }

    public void setScreen(TableScreen tableScreen) {
        screen = tableScreen;
        reloadScreen();
    }
}
