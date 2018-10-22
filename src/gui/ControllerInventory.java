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

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.security.Security;
import java.sql.*;
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
            = FXCollections.observableArrayList();

    static final String dbdriver = "com.mysql.jdbc.Driver";
    static final String dburl = "jdbc:mysql://localhost";
    static final String dbname = "parts";


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
        updateTable();
    }

    public void updateTable(){
        grabTable();
        tableView.getItems().setAll(this.data);
    }

    private void grabTable(){
        ResultSet rs = null;
        String statement = "SELECT * FROM parts";
        rs = executeSQLCommand(statement);

        try {
            while (rs.next()) {
                String serialNumber = rs.getString("serialNumber");
                String partName = rs.getString("partName");
                double price = rs.getDouble("price");
                String vendor = rs.getString("vendor");
                String manufacturer = rs.getString("manufacturer");
                String location = rs.getString("location");
                String barcode = rs.getString("barcode");
                boolean fault = rs.getBoolean("fault");
                long studentID = rs.getLong("studentID");
                Part part = new Part(partName, serialNumber, manufacturer, 1, price, vendor, location, barcode, fault, studentID);
                data.add(part);
            }
        }catch(SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't grab part list");
            alert.showAndWait();
            e.printStackTrace();
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
            OutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(System.getProperty("user.dir") + "/test.txt"));
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
        updateTable();
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
        updateTable();
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
        }
        updateTable();
    }

    private void deleteFromTable(Part part){
        if (part != null) {
            String deleteFromDB = "DELETE FROM parts WHERE serialNumber='"+part.getSerial()+"' AND barcode='"
                    + part.getBarcode() + "';";
            executeSQLCommand(deleteFromDB);
        }
    }

    public static ResultSet executeSQLCommand(String rawStatement){
        Connection conn = makeDBConnection();
        if(conn==null) {
            System.out.println("Connection was null, SQL command not executed.");
            return null;
        }
        Statement currentStatement = null;
        try {
            currentStatement = conn.createStatement();
            if(rawStatement.toLowerCase().contains("select")) {
                ResultSet rs = currentStatement.executeQuery(rawStatement);
                return rs;
            }
            else
                currentStatement.execute(rawStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (currentStatement != null) {
                try {
                    currentStatement.close();
                    System.out.println("Connection closed!");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            currentStatement = null;
        }
        return null;
    }

    public static Connection makeDBConnection(){
        String user = JOptionPane.showInputDialog("Enter username to update Parts database");
        String userPass = JOptionPane.showInputDialog("Enter password");
        try {
            Class.forName(dbdriver);
        } catch (ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Class not found");
            alert.showAndWait();
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection((dburl + "/" + dbname), user, userPass);
            connection.setClientInfo("autoReconnect", "true");
        }catch (SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Connection failure");
            alert.showAndWait();
        }catch(NullPointerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Connection failure");
            alert.showAndWait();
        }
        return connection;
    }
}
