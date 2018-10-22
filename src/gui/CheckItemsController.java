package gui;

import database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Observable;
import java.util.ResourceBundle;

public class CheckItemsController implements Initializable{

    @FXML
    private TableColumn studentID, barcode, partName, quantity, overnight, action, studentIDCheckin, barcodeCheckin, partNameCheckin, quantityCheckin, fault, actionCheckin;

    @FXML
    TableView checkOutTableView, checkInTableView;

    @FXML
    private VBox scene;

    @FXML
    ListView checkOutTable, savedTable;

    @FXML
    TabPane tabPane;

    @FXML
    Tab checkOutTab, checkInTab;

    @FXML
    Button submit;

    @FXML
    TextField name, ID, email, date;

    private ObservableList<CheckItemsTable> checkoutData = FXCollections.observableArrayList(new CheckItemsTable("", "","",""));
    private ObservableList<CheckItemsTable> checkinData = FXCollections.observableArrayList(new CheckItemsTable("", "","",""));
    Database database = new Database();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setCheckoutItems();
        setCheckinItems();
        checkOutTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2){
                String item = checkOutTable.getSelectionModel().getSelectedItem().toString();
                try {
                    Stage diffStage = new Stage();
                    Pane pane = FXMLLoader.load(getClass().getResource("infoPopUp.fxml"));
                    Scene scene = new Scene(pane);
                    diffStage.setScene(scene);
                    diffStage.initModality(Modality.APPLICATION_MODAL);
                    diffStage.setTitle("Item Info");
                    diffStage.showAndWait();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        checkOutTableView.setOnMouseClicked(event -> {
            if (checkOutTableView.getSelectionModel().getSelectedItems().size() == 1 &&
                    checkoutData.get(checkOutTableView.getSelectionModel().getSelectedIndex()).getStudentID() != null){
                try {
                    FileReader fr = new FileReader("src/students.txt");
                    BufferedReader br = new BufferedReader(fr);
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.contains(checkoutData.get(checkOutTableView.getSelectionModel().getSelectedIndex()).getStudentID())) {
                            name.setText(line.substring(0, line.indexOf(',')));
                            ID.setText(line.substring(line.indexOf(',') + 1, line.lastIndexOf(',')));
                            email.setText(line.substring(line.lastIndexOf(',') + 1));
                        }
                    }
                    Student student = new Student(name.getText(), ID.getText(), email.getText());
                    br.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void setCheckinItems() {
        generateTables(fault, actionCheckin, checkInTableView, checkinData);
    }
    private void setCheckoutItems() {
        generateTables(overnight, action, checkOutTableView, checkoutData);
    }

    private void generateTables(TableColumn checkBox, TableColumn action, TableView tableView, ObservableList data) {
        setCheckoutTableEditableFields();

        checkBox.setCellValueFactory(
                new PropertyValueFactory<CheckItemsTable, String>("checkBox")
        );
        tableView.setItems(data);
    }


    public void close(){
        scene.getScene().getWindow().hide();
    }

    public void returnHome()throws Exception{
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Menu.fxml"));
            //loader.setController(new Controller());
            Pane pane = loader.load();
            scene.getScene().setRoot(pane);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
        }
            //        scene.getScene().getWindow().hide();
            //        Stage stage = new Stage();
            //        StudentCheckin studentCheckin = new StudentCheckin();
            //        studentCheckin.start(stage);
    }

    public void popUp(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2){
            newStage("infoPopUp.fxml", "Item Info");
            //Controller c = new Controller();
            //c.newStage("infoPopUp.fxml", "Item Info");
        }
    }

    public void newStage(String fxml, String title){
        try {
            Pane pane = FXMLLoader.load(getClass().getResource(fxml));
            scene.getScene().setRoot(pane);
        }
        catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
            invoke.printStackTrace();

        }
    }

    private void makeStudentIDEditable(){
        studentID.setCellFactory(TextFieldTableCell.forTableColumn());
        studentID.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<CheckItemsTable, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<CheckItemsTable, String> event) {
                    ((CheckItemsTable) event.getTableView().getItems().get(
                        event.getTablePosition().getRow())
                    ).setStudentID(event.getNewValue());

                }

            }
        );
        studentIDCheckin.setCellFactory(TextFieldTableCell.forTableColumn());
        studentIDCheckin.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<CheckItemsTable, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<CheckItemsTable, String> event) {
                    ((CheckItemsTable) event.getTableView().getItems().get(
                        event.getTablePosition().getRow())
                    ).setStudentID(event.getNewValue());
                }
            }
        );
    }

    private void makeBarcodeEditable(){
        barcode.setCellFactory(TextFieldTableCell.forTableColumn());
        barcode.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<CheckItemsTable, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<CheckItemsTable, String> event) {
                    ((CheckItemsTable) event.getTableView().getItems().get(
                        event.getTablePosition().getRow())
                    ).setBarcode(event.getNewValue());
                }
            }
        );
        barcodeCheckin.setCellFactory(TextFieldTableCell.forTableColumn());
        barcodeCheckin.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<CheckItemsTable, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<CheckItemsTable, String> event) {
                    ((CheckItemsTable) event.getTableView().getItems().get(
                        event.getTablePosition().getRow())
                    ).setBarcode(event.getNewValue());
                }
            }
        );
    }

    private void makePartNameEditable(){
        partName.setCellFactory(TextFieldTableCell.forTableColumn());
        partName.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<CheckItemsTable, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<CheckItemsTable, String> event) {
                    ((CheckItemsTable) event.getTableView().getItems().get(
                        event.getTablePosition().getRow())
                    ).setPartName(event.getNewValue());
                }
            }
        );
        partNameCheckin.setCellFactory(TextFieldTableCell.forTableColumn());
        partNameCheckin.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<CheckItemsTable, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<CheckItemsTable, String> event) {
                    ((CheckItemsTable) event.getTableView().getItems().get(
                        event.getTablePosition().getRow())
                    ).setPartName(event.getNewValue());
                }
            }
        );
    }

    private void makeQuantityEditable(){
        quantity.setCellFactory(TextFieldTableCell.forTableColumn());
        quantity.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<CheckItemsTable, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<CheckItemsTable, String> event) {
                    ((CheckItemsTable) event.getTableView().getItems().get(
                        event.getTablePosition().getRow())
                    ).setQuantity(event.getNewValue());
                }
            }
        );
        quantityCheckin.setCellFactory(TextFieldTableCell.forTableColumn());
        quantityCheckin.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<CheckItemsTable, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<CheckItemsTable, String> event) {
                    ((CheckItemsTable) event.getTableView().getItems().get(
                        event.getTablePosition().getRow())
                    ).setQuantity(event.getNewValue());
                }
            }
        );
    }

    private void setCheckoutTableEditableFields(){
        makeStudentIDEditable();
        makeBarcodeEditable();
        makePartNameEditable();
        makeQuantityEditable();
    }

    public void clearFields(){
        clearCheckinData();
        clearCheckoutdata();
        checkInTableView.refresh();
        checkOutTableView.refresh();
    }


    private void clearCheckinData(){
        clearData(checkinData);
    }

    private void clearCheckoutdata(){
        clearData(checkoutData);
    }

    /**
      * Not very elegant solution, but works. Will remake after the spike.
      */
    private void clearData(ObservableList<CheckItemsTable> data) {
        for(int i = 0; i< data.size(); i++){
            data.get(i).setQuantity("");
            data.get(i).setStudentID("");
            data.get(i).setPartName("");
            data.get(i).setBarcode("");
            data.get(i).getCheckBox().setSelected(false);
        }
    }

    public void newRow(){
        checkoutData.add(new CheckItemsTable("","","",""));
        generateTables(fault, action,checkOutTableView, checkoutData );
    }


    public void submit(){
        if(checkOutTab.isSelected()){
            overnightPopup();
            database.addStudentID(checkoutData.get(0).getStudentID(), Integer.parseInt(checkoutData.get(0).getBarcode()));
        }
        if(checkInTab.isSelected()){
            faultPopup();
            database.removeStudentID(checkinData.get(0).getStudentID());
        }
        String finalDate = LocalDate.now().toString();
        System.out.println(finalDate);
        finalDate = finalDate.substring(5,7) + "/" + finalDate.substring(8) + "/" + finalDate.substring(0,4);
        date.setText(finalDate);
        checkOutTable.getItems().add(checkoutData.get(checkOutTableView.getSelectionModel().getSelectedIndex()).getPartName());
    }

    public void overnightPopup(){
        if(checkoutData.get(0).getCheckBox().isSelected()){
            showNewStage("overnightPopup.fxml", "Overnight Checkout");
        }
    }

    public void faultPopup(){
        if(checkinData.get(0).getCheckBox().isSelected()){
            showNewStage("faultPopup.fxml", "Fault Information");
        }
    }

    public void showNewStage(String fxml, String title){
        try {
            Stage diffStage = new Stage();
            Pane pane = FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = new Scene(pane);
            diffStage.setScene(scene);
            diffStage.initModality(Modality.APPLICATION_MODAL);
            diffStage.setTitle(title);
            diffStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}