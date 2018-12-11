package InventoryController;

import Database.Database;
import Database.OverdueItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class ControllerOverdueTab extends ControllerInventoryPage implements Initializable {

    @FXML
    private AnchorPane overduePage;

    @FXML
    private TableView overdueTable;

    @FXML
    TableColumn<OverdueItem, String> partID, serial, date;

    @FXML
    TableColumn<OverdueItem, Integer> studentID, price;

    private Database database;
    private ObservableList<OverdueItem> list = FXCollections.observableArrayList();

    /**
     * This method puts all overdue items into the list for populating the gui table
     *
     * @param location
     * @param resources
     * @author Bailey Terry
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label emptytableLabel = new Label("No parts found.");
        emptytableLabel.setFont(new Font(18));
//        overdueTable.setPlaceholder(emptytableLabel);
    }

    /**
     * Creates an informational pop up on double click
     *
     * @author Bailey Terry
     */
    public void popUp(MouseEvent event){
        if (event.getClickCount() == 2) {
            Stage stage = new Stage();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/OverduePopup.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 400, 400);
                stage.setTitle("Overdue Item");
                stage.initOwner(overduePage.getScene().getWindow());
                stage.setScene(scene);
                ((OverduePopUp) loader.getController()).populate(
                        (((OverdueItem) overdueTable.getSelectionModel().getSelectedItem())));
                stage.getIcons().add(new Image("msoe.png"));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method populates the gui based off of the data in the Observable list
     *
     * @author Joe Gilpin
     */
    public void populateTable() {
        database = new Database();
        list.clear();
        list = database.getOverdue(list);
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        for (int j = 0; j < list.size(); j++){
            if (!list.get(j).getPrice().substring(0,1).equals("$")) {
                double p = Double.parseDouble(list.get(j).getPrice());
                list.get(j).setPrice("$" + df.format(p));
            }else {
                String p = list.get(j).getPrice();
                list.get(j).setPrice(p.substring(1));
            }
        }
        partID.setCellValueFactory(new PropertyValueFactory<>("part"));
        serial.setCellValueFactory(new PropertyValueFactory<>("serial"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        studentID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        overdueTable.setItems(list);
        }
    }
