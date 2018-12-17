package InventoryController;

import Database.CheckedOutParts;
import Database.Part;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class acts as the controller for the checked out items part of the inventory tab
 */
public class ControllerCheckedOutTab  extends ControllerInventoryPage implements Initializable {

    @FXML
    private AnchorPane checkedOutPage;

    @FXML
    private TableView checkedOutTable;

    @FXML
    private TableColumn<CheckedOutItems, Integer> quantityCol;

    @FXML
    private TableColumn<CheckedOutItems, String> partNameCol, dueDateCol, sNameCol, checkOutAtCol;

    private CheckedOutParts checkedOutParts;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label emptytableLabel = new Label("No parts found.");
        emptytableLabel.setFont(new Font(18));
        checkedOutTable.setPlaceholder(emptytableLabel);
        checkedOutTable.setRowFactory( tv -> {
            TableRow<CheckedOutItems> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    CheckedOutItems rowData = row.getItem();
                    showInfoPage(rowData);
                    System.out.println("Hi, " + rowData.toString());
                }
            });
            return row ;
        });
    }

    /**
     * This sets each column table to the corresponding field in the CheckedOutItems class, and then populates it.
     */
    public void populateTable(){
        checkedOutParts = new CheckedOutParts();
        ObservableList<CheckedOutItems> list = checkedOutParts.getCheckedOutItems(); //Queries database, populating the Observable Arraylist in that class
        checkedOutTable.getItems().clear();
        checkedOutTable.getColumns().clear();

        // SET COLUMN WIDTH HERE (TOTAL = 800)
        checkedOutTable.getColumns().add(createColumn(0, "Student"));
        checkedOutTable.getColumns().add(createColumn(1, "Part Name"));
        checkedOutTable.getColumns().add(createColumn(2, "Quantity"));
        checkedOutTable.getColumns().add(createColumn(3, "CheckedOutAt"));
        checkedOutTable.getColumns().add(createColumn(4, "Date"));

        for (int i = 0; i < list.size(); i++) {
            for (int columnIndex = checkedOutTable.getColumns().size(); columnIndex < list.size(); columnIndex++) {
                checkedOutTable.getColumns().add(createColumn(columnIndex, ""));
            }
            ObservableList<StringProperty> data = FXCollections.observableArrayList();
            data.add(new SimpleStringProperty(list.get(i).getStudentName().get()));
            data.add(new SimpleStringProperty(list.get(i).getPartName().get()));
            data.add(new SimpleStringProperty("" + list.get(i).getQuantity()));
            data.add(new SimpleStringProperty(list.get(i).getCheckedOutAt().get()));
            data.add(new SimpleStringProperty(list.get(i).getDueDate().get()));
            checkedOutTable.getItems().add(data);
        }

//        ObservableList<CheckedOutItems> asd = checkedOutParts.data;
//        sNameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
//        partNameCol.setCellValueFactory(new PropertyValueFactory<>("partName"));
//        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
//        checkOutAtCol.setCellValueFactory(new PropertyValueFactory<>("checkedOutAt"));
//        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
//        checkedOutTable.setItems(checkedOutParts.data);

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
        column.setPrefWidth(800 / 6);
        return column;
    }

    /**
     * This method brings up the FXML page for showing the info about the selected part
     *
     * @author Matthew Karcz
     */
    public void showInfoPage(CheckedOutItems part){
        Stage stage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowPart.fxml"));
            Parent root = loader.load();
            ((ControllerShowPart) loader.getController()).initPart(part, "checkedOut");
            Scene scene = new Scene(root, 400, 400);
            stage.setTitle("Part Information");
            stage.initOwner(checkedOutPage.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("msoe.png"));
            stage.showAndWait();
            populateTable();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}