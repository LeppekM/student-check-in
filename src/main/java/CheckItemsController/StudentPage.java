package CheckItemsController;

import Database.OverdueItem;
import Database.SavedPart;
import Database.Student;
import HelperClasses.StageWrapper;
import InventoryController.CheckedOutItems;
import InventoryController.OverduePopUp;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

public class StudentPage {

    @FXML
    private Pane main = new Pane();

    @FXML
    private VBox vbox = new VBox();

    @FXML
    private Label studentName, email, RFID;

    @FXML
    private JFXTreeTableView coTable;

    @FXML
    private JFXTreeTableView oTable;

    @FXML
    private JFXTreeTableView sTable;

    @FXML
    private JFXTreeTableColumn<CheckedOutItems, String> coTableCol;

    @FXML
    private JFXTreeTableColumn<OverdueItem, String> oTableCol;

    @FXML
    private JFXTreeTableColumn<SavedPart, String> sTableCol;

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
        coTableCol = new JFXTreeTableColumn<>("Part Name");
        coTableCol.setPrefWidth(197);
        coTableCol.setResizable(false);
        coTableCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CheckedOutItems, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CheckedOutItems, String> param) {
                return param.getValue().getValue().getPartName();
            }
        });

        oTableCol = new JFXTreeTableColumn<>("Part Name");
        oTableCol.setPrefWidth(197);
        oTableCol.setResizable(false);
        oTableCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OverdueItem, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OverdueItem, String> param) {
                return param.getValue().getValue().getPart();
            }
        });

        sTableCol = new JFXTreeTableColumn<>("Part Name");
        sTableCol.setPrefWidth(197);
        sTableCol.setResizable(false);
        sTableCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<SavedPart, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<SavedPart, String> param) {
                return param.getValue().getValue().getPartName();
            }
        });

        populateTables();
    }

    private void populateTables(){
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

    public void goBack() {
        stageWrapper.newStage("CheckoutItems.fxml", main);
    }

    public void goHome() {
        stageWrapper.newStage("Menu.fxml", main);
    }

    public void coPopUp(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Stage stage = new Stage();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/StudentCheckPopUp.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 400, 300);
                stage.setTitle("Checked Out Item");
                stage.initOwner(main.getScene().getWindow());
                stage.setScene(scene);
                int index = coTable.getSelectionModel().getSelectedIndex();
                CheckedOutItems item = ((CheckedOutItems) coTable.getSelectionModel().getModelItem(index).getValue());
                ((CheckoutPopUp) loader.getController()).populate(item);
                stage.getIcons().add(new Image("msoe.png"));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void oPopUp(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Stage stage = new Stage();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/OverduePopup.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 400, 300);
                stage.setTitle("Overdue Item");
                stage.initOwner(main.getScene().getWindow());
                stage.setScene(scene);
                int index = oTable.getSelectionModel().getSelectedIndex();
                OverdueItem item = ((OverdueItem) oTable.getSelectionModel().getModelItem(index).getValue());
                ((OverduePopUp) loader.getController()).populate(item);
                stage.getIcons().add(new Image("msoe.png"));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sPopUp(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Stage stage = new Stage();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SavedPopUp.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 600, 400);
                stage.setTitle("Saved Item");
                stage.initOwner(main.getScene().getWindow());
                stage.setScene(scene);
                int index = sTable.getSelectionModel().getSelectedIndex();
                SavedPart item = ((SavedPart) sTable.getSelectionModel().getModelItem(index).getValue());
                ((SavedPopUp) loader.getController()).populate(item);
                stage.getIcons().add(new Image("msoe.png"));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
