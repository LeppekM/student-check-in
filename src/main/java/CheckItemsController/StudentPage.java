package CheckItemsController;

import Database.ObjectClasses.SavedPart;
import Database.ObjectClasses.Student;
import Database.ObjectClasses.Worker;
import Database.OverdueItem;
import HelperClasses.StageWrapper;
import InventoryController.CheckedOutItems;
import InventoryController.IController;
import InventoryController.OverduePopUpController;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.value.ObservableValue;
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
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.IntStream;

public class StudentPage implements IController {

    @FXML
    private VBox main = new VBox();

    @FXML
    private VBox vbox = new VBox();

    @FXML
    private Label studentName, email, RFID, fees, date;

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

    private Worker worker;

    private static Student student;
    private CheckoutObject checkoutObject;
    private StageWrapper stageWrapper = new StageWrapper();

    public void initCheckoutObject(CheckoutObject checkoutObject) {
        this.checkoutObject = checkoutObject;
    }

    public void setStudent(Student s) {
        student = s;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        double overdueFees = overdueFee(student);
        studentName = new Label("");
        studentName.setText(student.getName());
        studentName.getStylesheets().add(getClass().getResource("/css/HeaderStyle.css").toExternalForm());
        studentName.setStyle("-fx-font-size: 45px");
        email = new Label("");
        email.setText(student.getEmail());
        email.getStylesheets().add(getClass().getResource("/css/HeaderStyle.css").toExternalForm());
        email.setStyle("-fx-font-size: 45px");
        RFID = new Label("");
        RFID.setText(student.getRFID() + "");
        RFID.getStylesheets().add(getClass().getResource("/css/HeaderStyle.css").toExternalForm());
        RFID.setStyle("-fx-font-size: 45px");
        fees = new Label("");
        fees.setText("Outstanding fees: $" + overdueFees);
        fees.getStylesheets().add(getClass().getResource("/css/HeaderStyle.css").toExternalForm());
        fees.setStyle("-fx-font-size: 45px");
        date = new Label("");
        if(student.getDate() == null){
            date.setText("Date of last rental: Never");
        }else {
            date.setText("Date of last rental: " + sdf.format(new Date(student.getDate())));
        }
        date.getStylesheets().add(getClass().getResource("/css/HeaderStyle.css").toExternalForm());
        date.setStyle("-fx-font-size: 45px");
        vbox.getChildren().add(studentName);
        vbox.getChildren().add(email);
        vbox.getChildren().add(RFID);
        vbox.getChildren().add(date);
        vbox.getChildren().add(fees);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(5);
        setTables();
    }

    private double overdueFee(Student s){
        double overdueFees = 0.0;
        int[] sID = new int[s.getSavedItems().size()];
        for (int i = 0; i < s.getSavedItems().size(); i++) {
            if (s.getSavedItems().get(i).getCheckID().matches("^[0-9]*")){
                sID[i] = Integer.parseInt(s.getSavedItems().get(i).getCheckID());
            }else {
                sID[i] = Integer.parseInt(s.getSavedItems().get(i).getCheckID().substring(13));
            }
        }
        for (int j = 0; j < s.getOverdueItems().size(); j++) {
            int oID = Integer.parseInt(s.getOverdueItems().get(j).getCheckID());
            boolean result = IntStream.of(sID).anyMatch(x -> x == oID);
            if (!result) {
                overdueFees += Double.parseDouble(s.getOverdueItems().get(j).getPrice().get());
            }
        }
        return overdueFees;
    }

    private void setTables() {
        coTableCol = new JFXTreeTableColumn<>("Part Name");
        coTableCol.prefWidthProperty().bind(coTable.widthProperty());
        coTableCol.setResizable(false);
        coTableCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CheckedOutItems, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CheckedOutItems, String> param) {
                return param.getValue().getValue().getPartName();
            }
        });

        oTableCol = new JFXTreeTableColumn<>("Part Name");
        oTableCol.prefWidthProperty().bind(oTable.widthProperty());
        oTableCol.setResizable(false);
        oTableCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OverdueItem, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OverdueItem, String> param) {
                return param.getValue().getValue().getPart();
            }
        });

        sTableCol = new JFXTreeTableColumn<>("Part Name");
        sTableCol.prefWidthProperty().bind(sTable.widthProperty());
        sTableCol.setResizable(false);
        sTableCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<SavedPart, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<SavedPart, String> param) {
                return param.getValue().getValue().getPartName();
            }
        });

        populateTables();
    }

    private void populateTables() {
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CheckoutItems.fxml"));
            Parent root = loader.load();
            main.getScene().setRoot(root);
            ((ControllerCheckoutPage) loader.getController()).initCheckoutObject(checkoutObject);
            ((ControllerCheckoutPage) loader.getController()).initWorker(worker);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goHome() {
        stageWrapper.newStage("/fxml/Menu.fxml", main, worker);
    }

    public void coPopUp(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Stage stage = new Stage();
            try {
                URL myFMLURL = ClassLoader.getSystemResource("fxml/StudentCheckPopUp.fxml");
                FXMLLoader loader = new FXMLLoader(myFMLURL);
                Parent root = loader.load();
                ((IController) loader.getController()).initWorker(worker);
                Scene scene = new Scene(root, 400, 300);
                stage.setTitle("Checked Out Item");
                stage.initOwner(main.getScene().getWindow());
                stage.setScene(scene);
                int index = coTable.getSelectionModel().getSelectedIndex();
                if (index != -1) {
                    CheckedOutItems item = ((CheckedOutItems) coTable.getSelectionModel().getModelItem(index).getValue());
                    ((CheckoutPopUp) loader.getController()).populate(item);
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.showAndWait();
                }
                fees.setText("Outstanding fees: $" + overdueFee(student));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void oPopUp(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Stage stage = new Stage();
            try {
                URL myFxmlURL = ClassLoader.getSystemResource("fxml/OverduePopup.fxml");
                FXMLLoader loader = new FXMLLoader(myFxmlURL);
                Parent root = loader.load();
                ((IController) loader.getController()).initWorker(worker);
                Scene scene = new Scene(root, 400, 300);
                stage.setTitle("Overdue Item");
                stage.initOwner(main.getScene().getWindow());
                stage.setScene(scene);
                int index = oTable.getSelectionModel().getSelectedIndex();
                if (index != -1) {
                    OverdueItem item = ((OverdueItem) oTable.getSelectionModel().getModelItem(index).getValue());
                    ((OverduePopUpController) loader.getController()).populate(item, null);
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.showAndWait();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sPopUp(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Stage stage = new Stage();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SavedPopUp.fxml"));
                Parent root = loader.load();
                ((IController) loader.getController()).initWorker(worker);
                Scene scene = new Scene(root, 350, 400);
                stage.setTitle("Saved Item");
                stage.initOwner(main.getScene().getWindow());
                stage.setScene(scene);
                int index = sTable.getSelectionModel().getSelectedIndex();
                if (index != -1) {
                    SavedPart item = ((SavedPart) sTable.getSelectionModel().getModelItem(index).getValue());
                    ((SavedPopUp) loader.getController()).populate(item);
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.showAndWait();
                }
                fees.setText("Outstanding fees: $" + overdueFee(student));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initWorker(Worker worker) {
        if (this.worker == null) {
            this.worker = worker;
        }
    }

    public static Student getStudent() {
        return student;
    }
}
