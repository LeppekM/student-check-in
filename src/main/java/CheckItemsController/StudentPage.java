package CheckItemsController;

import Database.OverdueItem;
import Database.SavedPart;
import Database.Student;
import HelperClasses.StageWrapper;
import InventoryController.CheckedOutItems;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class StudentPage {

    @FXML
    private Pane main = new Pane();

    @FXML
    private VBox vbox = new VBox();

    @FXML
    private Label studentName, email, RFID;

    @FXML
    private TableView coTable, oTable, sTable;

    @FXML
    private TableColumn<Student, String> coTableCol, oTableCol, sTableCol;

    private Student student;
    private StageWrapper stageWrapper = new StageWrapper();


    public void setStudent(Student s){
        student = s;
        studentName = new Label("");
        studentName.setText(student.getName());
        studentName.getStylesheets().add(getClass().getResource("/HeaderStyle.css").toExternalForm());
        email = new Label("");
        email.setText(student.getEmail());
        email.getStylesheets().add(getClass().getResource("/HeaderStyle.css").toExternalForm());
        RFID = new Label( "");
        RFID.setText(student.getID() + "");
        RFID.getStylesheets().add(getClass().getResource("/HeaderStyle.css").toExternalForm());
        vbox.getChildren().add(studentName);
        vbox.getChildren().add(email);
        vbox.getChildren().add(RFID);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(35);
        setTables();
    }

    private void setTables() {
        coTableCol.setCellValueFactory(new PropertyValueFactory<>("parts.partName"));
        oTableCol.setCellValueFactory(new PropertyValueFactory<>("parts.partName"));
        sTableCol.setCellValueFactory(new PropertyValueFactory<>("parts.partName"));
//        coTableCol = new TableColumn<>("Part Name");
//        coTableCol.setPrefWidth(200);
//        coTableCol.setCellValueFactory((Callback<TreeTableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>) param ->
//                new SimpleStringProperty(param.getValue().getValue().getCheckedOut().get(0).getPartName()));
//
//        oTableCol = new TableColumn<>("Part Name");
//        oTableCol.setPrefWidth(200);
//        oTableCol.setCellValueFactory((Callback<TreeTableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>) param ->
//                new SimpleStringProperty(param.getValue().getValue().getOverdueItems().get(0).getPart()));
//
//        sTableCol = new TableColumn<>("Part Name");
//        sTableCol.setPrefWidth(200);
//        sTableCol.setCellValueFactory((Callback<TreeTableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>) param ->
//                new SimpleStringProperty(param.getValue().getValue().getSavedItems().get(0).getPartName()));

        populateTables();
    }

    private void populateTables(){
        coTable.getItems().clear();
        oTable.getItems().clear();
        sTable.getItems().clear();
        ObservableList<CheckedOutItems> coItems = student.getCheckedOut();
        ObservableList<OverdueItem> oItems = student.getOverdueItems();
        ObservableList<SavedPart> sItems = student.getSavedItems();
        coTable.getColumns().setAll(coItems);
        oTable.getColumns().setAll(oItems);
        sTable.getColumns().setAll(sItems);
    }

    public void goBack() {
        stageWrapper.newStage("CheckoutItems.fxml", main);
    }

    public void goHome() {
        stageWrapper.newStage("Menu.fxml", main);
    }
}
