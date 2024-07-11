package Popups;

import Database.Database;
import Database.ObjectClasses.Checkout;
import Database.ObjectClasses.Student;
import Database.ObjectClasses.Worker;
import Database.OverdueItem;
import HelperClasses.StageUtils;
import Controllers.IController;
import Tables.CheckedOutInventoryTable;
import Tables.OverdueInventoryTable;
import Tables.TSCTable;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import static Controllers.CheckOutController.RFID_REGEX;

/**
 *
 */
public class EditStudentController implements IController {

    @FXML
    private AnchorPane main = new AnchorPane();

    @FXML
    private VBox vbox = new VBox();

    @FXML
    private JFXTextField studentName, email, rfid;

    @FXML
    private JFXTreeTableView coTable, oTable;

    private JFXTreeTableColumn<Checkout, String> coTableCol;

    private JFXTreeTableColumn<OverdueItem, String> oTableCol;

    private static Student student;
    private Worker worker;
    private Database database;
    private static String name;
    private static int id;
    private static String studentEmail;
    private final StageUtils stageUtils = StageUtils.getInstance();

    /**
     * This method sets the student in this class and in the window
     * @param s student
     */
    public void setStudent(Student s) {
        student = s;
        database = Database.getInstance();
        studentName.setText(student.getName());
        email.setText(student.getEmail());
        rfid.setText(student.getRFID() + "");
        StageUtils stageUtils = StageUtils.getInstance();
        stageUtils.acceptIntegerOnly(rfid);
        name = studentName.getText();
        id = Integer.parseInt(rfid.getText());
        studentEmail = email.getText();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(5);
        setTables();
    }

    /**
     * This method creates the tables and effects on the tables
     */
    private void setTables() {
        coTableCol = new JFXTreeTableColumn<>("Part Name");
        coTable.setPlaceholder(TSCTable.getEmptyTableLabel());
        coTableCol.prefWidthProperty().bind(coTable.widthProperty());
        coTableCol.setStyle("-fx-font-size: 18px");
        coTableCol.setResizable(false);
        coTableCol.setCellValueFactory(param -> param.getValue().getValue().getPartName());

        oTableCol = new JFXTreeTableColumn<>("Part Name");
        oTable.setPlaceholder(TSCTable.getEmptyTableLabel());
        oTableCol.prefWidthProperty().bind(oTable.widthProperty());
        oTableCol.setStyle("-fx-font-size: 18px");
        oTableCol.setResizable(false);
        oTableCol.setCellValueFactory(param -> param.getValue().getValue().getPartName());

        populateTables();
    }

    /**
     * This method fills the tables with data if there is any
     */
    private void populateTables() {
        if (student.getRFID()==0) {
            return;
        }

        final TreeItem<Checkout> coItems = new RecursiveTreeItem<>(student.getCheckedOut(), RecursiveTreeObject::getChildren);
        final TreeItem<OverdueItem> oItems = new RecursiveTreeItem<>(student.getOverdueItems(), RecursiveTreeObject::getChildren);
        coTable.getColumns().setAll(coTableCol);
        coTable.setRoot(coItems);
        coTable.setShowRoot(false);
        oTable.getColumns().setAll(oTableCol);
        oTable.setRoot(oItems);
        oTable.setShowRoot(false);
    }

    /**
     * This method opens the checkout pop up for a student
     * @param event double click
     */
    public void coPopUp(MouseEvent event) {
        if (event.getClickCount() == 2) {
            int index = coTable.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                Checkout item = ((Checkout) coTable.getSelectionModel().getModelItem(index).getValue());
                CheckedOutInventoryTable.createCheckoutPopup(item);
            }
        }
    }

    /**
     * This method opens the overdue pop up for a student
     * @param event double click
     */
    public void oPopUp(MouseEvent event) {
        if (event.getClickCount() == 2) {
            int index = oTable.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                OverdueItem item = ((OverdueItem) oTable.getSelectionModel().getModelItem(index).getValue());
                OverdueInventoryTable.createOverduePartPopup(item);
            }
        }
    }

    /**
     * Gets the student being edited
     * @return student
     */
    public static Student getStudent() {
        return student;
    }

    /**
     * Helper method for saving a students info
     * @return true if nothing changed
     */
    public boolean changed(){
        return !name.equals(studentName.getText()) || id != Integer.parseInt(rfid.getText()) || !studentEmail.equals(email.getText());
    }

    /**
     * This method saves the changes made to a student and ensures the user wants to
     */
    public void save() {
        if (!changed()){
            stageUtils.informationAlert("No Edits Made", "No changes detected, so no edits made");
        } else if (!rfid.getText().matches(RFID_REGEX)) {
            stageUtils.errorAlert("RFID must be at least 4 digits");
            rfid.setText(id + "");
        } else {
            String contentText = "Are you sure you want to make the following changes?\n";
            if (!name.equals(studentName.getText())) {
                contentText += "\t" + name + " --> " + studentName.getText() + "\n";
            }
            if (id != Integer.parseInt(rfid.getText())) {
                contentText += "\t" + id + " --> " + rfid.getText() + "\n";
            }
            if (!studentEmail.equals(email.getText())){
                contentText += "\t" + studentEmail + " --> " + email.getText() + "\n";
            }
            if (stageUtils.confirmationAlert("Edit Success", "Student info changing...", contentText)) {
                student.setName(studentName.getText());
                int oldRFID = student.getRFID();
                student.setRFID(Integer.parseInt(rfid.getText()));
                student.setEmail(email.getText());
                database.initWorker(worker);
                database.updateStudent(student, oldRFID);
                stageUtils.informationAlert("Student updated", "Student updated successfully");
                main.getScene().getWindow().hide();
            } else {
                studentName.setText(name);
                rfid.setText(id + "");
                email.setText(studentEmail);
            }
        }
    }

    /**
     * Used to keep track of which worker is currently logged in by passing the worker into
     * each necessary class
     * @param worker the currently logged in worker
     */
    @Override
    public void initWorker(Worker worker) {
        if (this.worker == null){
            this.worker = worker;
            database.initWorker(worker);
        }
    }
}
