package InventoryController;

import Database.Database;
import Database.OverdueItems;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class ControllerOverdueTab extends ControllerInventoryPage implements Initializable {

    @FXML
    private AnchorPane overduePage;

    @FXML
    public TableView overdueTable;

    @FXML
    TableColumn<OverdueItems, String> partID, serial, date;

    @FXML
    TableColumn<OverdueItems, Integer> studentID, price;

    private Database database;

    /**
     * This method puts all overdue items into the list for populating the gui table
     *
     * @param location
     * @param resources
     * @author Bailey Terry
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = new Database();
        Label emptytableLabel = new Label("No parts found.");
        emptytableLabel.setFont(new Font(18));
        overdueTable.setPlaceholder(emptytableLabel);
    }

    /**
     * Creates an informational pop up on double click
     *
     * @author Bailey Terry
     */
    public void popUp(){
        Stage stage = new Stage();
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("OverduePopup.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            Parent root = loader.load(myFxmlURL);
            Scene scene = new Scene(root, 400, 400);
            stage.setTitle("Overdue Item");
            stage.initOwner(overduePage.getScene().getWindow());
            stage.setScene(scene);
            stage.getIcons().add(new Image("msoe.png"));
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * This method populates the gui based off of the data in the Observable list
     *
     * @author Bailey Terry
     */
    public void populateTable() {
        ObservableList<OverdueItems> list = database.getOverdue();
        overdueTable.getItems().clear();
        overdueTable.getColumns().clear();

        // SET COLUMN WIDTH HERE (TOTAL = 800)
        overdueTable.getColumns().add(createColumn(0, "Student"));
        overdueTable.getColumns().add(createColumn(1, "Part Name"));
        overdueTable.getColumns().add(createColumn(2, "Serial Number"));
        overdueTable.getColumns().add(createColumn(3, "Date"));
        overdueTable.getColumns().add(createColumn(4, "Price"));


        for (int i = 0; i < list.size(); i++) {
            for (int columnIndex = overdueTable.getColumns().size(); columnIndex < list.size(); columnIndex++) {
                overdueTable.getColumns().add(createColumn(columnIndex, ""));
            }
            ObservableList<StringProperty> data = FXCollections.observableArrayList();
            data.add(new SimpleStringProperty("" + list.get(i).getID()));
            data.add(new SimpleStringProperty(list.get(i).getPart()));
            data.add(new SimpleStringProperty(list.get(i).getSerial()));
            data.add(new SimpleStringProperty("" + list.get(i).getDate()));
            data.add(new SimpleStringProperty(list.get(i).getPrice()));
            DecimalFormat df = new DecimalFormat("#,###,##0.00");
            for (int j = 0; j < list.size(); j++){
                if (!list.get(j).getPrice().substring(0,1).equals("$")) {
                    double p = Double.parseDouble(list.get(j).getPrice());
                    list.get(j).setPrice("$" + df.format(p));
                }
            }
            overdueTable.getItems().add(data);
        }
    }

    /**
     * This method creates a column with the correct format for the table
     * @param columnIndex
     * @param columnTitle
     * @return
     */
    private TableColumn<ObservableList<StringProperty>, String> createColumn(
            final int columnIndex, String columnTitle) {
        TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();
        column.setPrefWidth(150);
        String title;
        if (columnTitle == null || columnTitle.trim().length() == 0) {
            title = "Column " + (columnIndex + 1);  // DELETE??
        } else {
            title = columnTitle;
        }
        column.setText(title);
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<StringProperty>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(
                    TableColumn.CellDataFeatures<ObservableList<StringProperty>, String> cellDataFeatures) {
                ObservableList<StringProperty> values = cellDataFeatures.getValue();
                if (columnIndex >= values.size()) {
                    return new SimpleStringProperty("");
                } else {
                    return cellDataFeatures.getValue().get(columnIndex);
                }
            }
        });
        // width of column set to width of table / number of columns
        column.setPrefWidth(800 / 5);
        return column;
    }
}
