package gui;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.security.Security;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ControllerInventory implements Initializable {

    @FXML
    private VBox sceneInv;

    @FXML
    private Button print, back, add, remove;

    @FXML
    private TextField searchField;

    @FXML public TableView<Part> tableView;

    @FXML private TableColumn<Part,String> partName, serialNumber, manufacturer, quantity, price, vendor, location,
            barcode, fault, studentId;

    private final ObservableList<Part> data
            = FXCollections.observableArrayList(
            new Part("HDMI Cable", "234567", "Sony", 2, 5.99, "MSOE", "OUT", "H233J788", false, 533277),
            new Part("Raspberry Pi", "567890", "Pi Foundation", 3, 29.99, "MSOE", "IN", "P845J788", true, 000000)
    );


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTable();
        tableView.setRowFactory(tv -> {
            TableRow<Part> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Part rowData = row.getItem();
                    editItem(rowData);
                }
            });
            return row ;
        });
    }

    private void populateTable() {
        partName.setCellValueFactory(new PropertyValueFactory("partName"));
        serialNumber.setCellValueFactory(new PropertyValueFactory("serialNumber"));
        manufacturer.setCellValueFactory(new PropertyValueFactory("manufacturer"));
        quantity.setCellValueFactory(new PropertyValueFactory("quantity"));
        price.setCellValueFactory(new PropertyValueFactory("price"));
        vendor.setCellValueFactory(new PropertyValueFactory("vendor"));
        location.setCellValueFactory(new PropertyValueFactory("location"));
        barcode.setCellValueFactory(new PropertyValueFactory("barcode"));
        fault.setCellValueFactory(new PropertyValueFactory("fault"));
        studentId.setCellValueFactory(new PropertyValueFactory("studentId"));
        grabTable();
        tableView.getItems().setAll(this.data);
    }

    private void grabTable(){
        int listLength = 2;
        Part part;
        for(int x=0; x<listLength; x++){
            part = new Part("", "", "", 3, 3, "", "", "", false, 12345);
            data.add(part);
        }

    }

    @FXML
    public void search(){

    }

    @FXML
    public void goBack(){
        try {
            Pane pane = FXMLLoader.load(getClass().getResource("Menu.fxml"));
            sceneInv.getScene().setRoot(pane);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
        }
    }

    @FXML
    public void printReport(){
        String report = "Test";
        try {
            OutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("C:"));
            byte[] bytes = report.getBytes();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            int token = -1;

            while ((token = inputStream.read()) != -1) {
                bufferedOutputStream.write(token);
            }
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            inputStream.close();
        }catch(IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, problems loading part list.");
            alert.showAndWait();
        }
    }

    @FXML
    public void addItem(){
        try {
            Stage diffStage = new Stage();
            Pane pane = FXMLLoader.load(getClass().getResource("AddItem.fxml"));
            Scene scene = new Scene(pane);
            diffStage.setScene(scene);
            diffStage.initModality(Modality.APPLICATION_MODAL);
            diffStage.setTitle("Add Part");
            diffStage.showAndWait();
        }
        catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();

        }
    }

    @FXML
    public void editItem(Part part){
        try {
            Stage diffStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditItem.fxml"));
            Scene scene = new Scene((Pane) loader.load());
            ControllerEditItem controller = loader.<ControllerEditItem>getController();
            controller.initData(part);
            diffStage.setScene(scene);
            diffStage.initModality(Modality.APPLICATION_MODAL);
            diffStage.setTitle("Edit Part");
            diffStage.showAndWait();
        }
        catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();

        }
    }

    @FXML
    public void removeItem(){
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setHeaderText("Please Confirm");
        dialog.setContentText("Are you sure you want to delete this item?");
        dialog.setResizable(true);
        dialog.getDialogPane().setPrefSize(350, 200);
        final Optional<ButtonType> result = dialog.showAndWait();
        if(result.get() == ButtonType.OK){
            Part part = tableView.getSelectionModel().getSelectedItem();
            deleteFromTable(part);
            System.out.println("Part removed!");
        }
    }

    private void deleteFromTable(Part part){
        try {
            if (part != null) {
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/parts", "root", "xam54678");
                String deleteFromDB = "DELETE FROM parts WHERE serialNumber="+part.getSerial()+" AND barcode="
                        + part.getBarcode() + ";";
                executeSQLCommand(con, deleteFromDB);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void executeSQLCommand(Connection conn, String rawStatement) {
        Statement currentStatement = null;
        try {
            currentStatement = conn.createStatement();
            currentStatement.execute(rawStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (currentStatement != null) {
                try {
                    currentStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            currentStatement = null;
        }
    }
}
