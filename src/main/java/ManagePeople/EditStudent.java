package ManagePeople;

import CheckItemsController.CheckoutPopUp;
import CheckItemsController.SavedPopUp;
import Database.Database;
import Database.ObjectClasses.Worker;
import Database.OverdueItem;
import Database.ObjectClasses.SavedPart;
import Database.ObjectClasses.Student;
import HelperClasses.StageWrapper;
import InventoryController.CheckedOutItems;
import InventoryController.IController;
import InventoryController.OverduePopUpController;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditStudent implements IController {

    @FXML
    private AnchorPane main = new AnchorPane();

    @FXML
    private VBox vbox = new VBox();

    @FXML
    private JFXTextField studentName, email, RFID;

    @FXML
    private Label fees, date;

    @FXML
    private JFXTreeTableView coTable, oTable, sTable;

    @FXML
    private JFXTreeTableColumn<CheckedOutItems, String> coTableCol;

    @FXML
    private JFXTreeTableColumn<OverdueItem, String> oTableCol;

    @FXML
    private JFXTreeTableColumn<SavedPart, String> sTableCol;

    private static Student student;
    private Worker worker;
    private Database database;
    private static String name;
    private static int id;
    private static String studentEmail;

    /**
     * This method sets the student in this class and in the window
     * @param s student
     */
    public void setStudent(Student s) {
        student = s;
        database = new Database();
        studentName.setText(student.getName());
        email.setText(student.getEmail());
        RFID.setText(student.getRFID() + "");
        StageWrapper stageWrapper = new StageWrapper();
        stageWrapper.acceptIntegerOnly(RFID);
        name = studentName.getText();
        id = Integer.parseInt(RFID.getText());
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
        Label emptyTableLabel = new Label("No parts found.");
        emptyTableLabel.setStyle("-fx-text-fill: white");
        emptyTableLabel.setFont(new Font(18));
        coTable.setPlaceholder(emptyTableLabel);
        coTableCol.prefWidthProperty().bind(coTable.widthProperty());
        coTableCol.setStyle("-fx-font-size: 18px");
        coTableCol.setResizable(false);
        coTableCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CheckedOutItems, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CheckedOutItems, String> param) {
                return param.getValue().getValue().getPartName();
            }
        });

        oTableCol = new JFXTreeTableColumn<>("Part Name");
        Label emptyTableLabel2 = new Label("No parts found.");
        emptyTableLabel2.setStyle("-fx-text-fill: white");
        emptyTableLabel2.setFont(new Font(18));
        oTable.setPlaceholder(emptyTableLabel);
        oTableCol.prefWidthProperty().bind(oTable.widthProperty());
        oTableCol.setStyle("-fx-font-size: 18px");
        oTableCol.setResizable(false);
        oTableCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OverdueItem, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OverdueItem, String> param) {
                return param.getValue().getValue().getPart();
            }
        });

        sTableCol = new JFXTreeTableColumn<>("Part Name");
        Label emptyTableLabel3 = new Label("No parts found.");
        emptyTableLabel3.setStyle("-fx-text-fill: white");
        emptyTableLabel3.setFont(new Font(18));
        sTable.setPlaceholder(emptyTableLabel);
        sTableCol.prefWidthProperty().bind(sTable.widthProperty());
        sTableCol.setStyle("-fx-font-size: 18px");
        sTableCol.setResizable(false);
        sTableCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<SavedPart, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<SavedPart, String> param) {
                return param.getValue().getValue().getPartName();
            }
        });

        populateTables();
    }

    /**
     * This method fills the tables with data if there is any
     */
    private void populateTables() {
        if(student.getRFID()==0){
            return;
        }

        final TreeItem<CheckedOutItems> coItems = new RecursiveTreeItem<>(student.getCheckedOut(), RecursiveTreeObject::getChildren);
        final TreeItem<OverdueItem> oItems = new RecursiveTreeItem<>(student.getOverdueItems(), RecursiveTreeObject::getChildren);
        final TreeItem<SavedPart> sItems = new RecursiveTreeItem<>(student.getSavedItems(), RecursiveTreeObject::getChildren);
        coTable.getColumns().setAll(coTableCol);
        coTable.setRoot(coItems);
        coTable.setShowRoot(false);
        oTable.getColumns().setAll(oTableCol);
        oTable.setRoot(oItems);
        oTable.setShowRoot(false);
        sTable.getColumns().setAll(sTableCol);
        sTable.setRoot(sItems);
        sTable.setShowRoot(false);
    }

    /**
     * This method opens the checkout pop up for a student
     * @param event double click
     */
    public void coPopUp(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Stage stage = new Stage();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StudentCheckPopUp.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 400, 300);
                stage.setTitle("Checked Out Item");
                stage.initOwner(main.getScene().getWindow());
                stage.setScene(scene);
                int index = coTable.getSelectionModel().getSelectedIndex();
                if (index != -1) {
                    CheckedOutItems item = ((CheckedOutItems) coTable.getSelectionModel().getModelItem(index).getValue());
                    ((CheckoutPopUp) loader.getController()).populate(item);
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method opens the overdue pop up for a student
     * @param event double click
     */
    public void oPopUp(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Stage stage = new Stage();
            try {
                URL myFxmlURL = ClassLoader.getSystemResource("fxml/ViewOverduePart.fxml");
                FXMLLoader loader = new FXMLLoader(myFxmlURL);
                Parent root = loader.load();
                Scene scene = new Scene(root, 400, 300);
                stage.setTitle("Overdue Item");
                stage.initOwner(main.getScene().getWindow());
                stage.setScene(scene);
                int index = oTable.getSelectionModel().getSelectedIndex();
                if (index != -1) {
                    OverdueItem item = ((OverdueItem) oTable.getSelectionModel().getModelItem(index).getValue());
                    ((OverduePopUpController) loader.getController()).populate(item, null);
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method opens the saved part pop up for a student
     * @param event double click
     */
    public void sPopUp(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Stage stage = new Stage();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SavedPopUp.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 350, 400);
                stage.setTitle("Saved Item");
                stage.initOwner(main.getScene().getWindow());
                stage.setScene(scene);
                int index = sTable.getSelectionModel().getSelectedIndex();
                if (index != -1) {
                    SavedPart item = ((SavedPart) sTable.getSelectionModel().getModelItem(index).getValue());
                    ((SavedPopUp) loader.getController()).populate(item);
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
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
        return !name.equals(studentName.getText()) || id != Integer.parseInt(RFID.getText()) || !studentEmail.equals(email.getText());
    }

    /**
     * This method saves the changes made to a student and ensures the user wants to
     * @param actionEvent button
     */
    public void save(ActionEvent actionEvent) {
        Alert alert;
        if (!changed()){
            alert = new Alert(Alert.AlertType.INFORMATION, "No changes detected...");
            alert.setTitle("Edit Failure");
            alert.setHeaderText("No changes were made.");
            alert.showAndWait();
        }else if (!RFID.getText().matches("^\\D*(?:\\d\\D*){4,}$")) {
            alert = new Alert(Alert.AlertType.ERROR, "RFID must be 5 digits.");
            alert.setTitle("Edit Failure.");
            alert.setHeaderText("Student RFID is not 5 numbers.");
            alert.showAndWait();
            RFID.setText(id + "");
        }else {
            alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to make the following changes?\n");
            alert.setTitle("Edit Success");
            alert.setHeaderText("Student info changing...");
            if (!name.equals(studentName.getText())) {
                alert.setContentText(alert.getContentText() + "\t" + name + " --> " + studentName.getText() + "\n");
            }
            if (id != Integer.parseInt(RFID.getText())) {
                alert.setContentText(alert.getContentText() + "\t" + id + " --> " + RFID.getText() + "\n");
            }
            if (!studentEmail.equals(email.getText())){
                alert.setContentText(alert.getContentText() + "\t" + studentEmail + " --> " + email.getText() + "\n");
            }
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.OK){
                    student.setName(studentName.getText());
                    student.setRFID(Integer.parseInt(RFID.getText()));
                    student.setEmail(email.getText());
                    database.initWorker(worker);
                    database.updateStudent(student);
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Student updated");
                    alert1.showAndWait();
                    main.getScene().getWindow().hide();
                }else {
                    studentName.setText(name);
                    RFID.setText(id + "");
                    email.setText(studentEmail);
                }
            });
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
