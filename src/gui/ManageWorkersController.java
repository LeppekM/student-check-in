package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ManageWorkersController implements Initializable {

    @FXML
    private VBox scene;

    @FXML
    private TableView workersTableManageWorkersPage;

    @FXML
    CheckBox isAdminCheckBoxAddWorkerPage;

    @FXML
    private Button addWorkerButtonManageWorkersPage,
            viewWorkerButtonManageWrokersPage,
            deleteWorkerButtonManageWorkersPage,
            backToHomeButtonManageWorkersPage;

    @FXML
    private TableColumn workerNameColumn, workerEmailColumn, workerStatusColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTable();
    }

    public void backToHome() {
        try {
            Pane pane = FXMLLoader.load(getClass().getResource("Menu.fxml"));
            scene.getScene().setRoot(pane);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
        }
    }

    public void addWorker() {
        newStage("AddWorker.fxml", "Add Worker");
    }

    public void viewWorker() {
        newStage("EditWorker.fxml", "Edit Worker");

    }

    private void newStage(String fxml, String title) {
        try {
            Stage diffStage = new Stage();
            Pane pane = FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = new Scene(pane);
            diffStage.setScene(scene);
            diffStage.initModality(Modality.APPLICATION_MODAL);
            diffStage.setTitle(title);
            diffStage.showAndWait();
            populateTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteWorker() {
        if (workersTableManageWorkersPage.getSelectionModel().getSelectedItems() != null) {
            String col;
            String pattern = "String Property value: [value: (*)]";
            for (int i = 0; i < workersTableManageWorkersPage.getSelectionModel().getSelectedItems().size(); i++) {
                // get the value of the third column
                col = workersTableManageWorkersPage.getSelectionModel().getSelectedItems().get(i).toString().split(", ")[2];
                col = col.substring(col.indexOf(": ") + 2, col.indexOf("]]"));
                //col = col.matches("String Property value: (*)");
                removeFromTextFile(col);
            }
            workersTableManageWorkersPage.getItems().removeAll(workersTableManageWorkersPage.getSelectionModel().getSelectedItems());
        }
    }

    public void removeFromTextFile(String email) {
        try {
            File inputFile = new File("src/workers.txt");
            BufferedReader r = new BufferedReader(new FileReader(inputFile));
            String line;
            String lines = "";
            while ((line = r.readLine()) != null) {
                if (!line.contains(email)) {
                    lines += line + "\r\n";
                }
            }
            BufferedWriter w = new BufferedWriter(new FileWriter(inputFile, false));
            w.write(lines);
            w.close();
            r.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void populateTable() {
        try {
            FileReader fr = new FileReader("src/workers.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            workersTableManageWorkersPage.getItems().clear();
            workersTableManageWorkersPage.getColumns().clear();
            workersTableManageWorkersPage.getColumns().add(createColumn(0, "Worker Name"));
            workersTableManageWorkersPage.getColumns().add(createColumn(1, "Worker Email"));
            workersTableManageWorkersPage.getColumns().add(createColumn(2, "Worker Status"));
            while ((line = br.readLine()) != null) {
                final String[] items = line.split(",");
                ObservableList<StringProperty> data = FXCollections.observableArrayList();
                for (int i = 0; i < 2; i++) {
                    data.add(new SimpleStringProperty(items[i]));
                }
                if (items[2].equals("true")) {
                    data.add(new SimpleStringProperty("Administrator"));
                } else {
                    data.add(new SimpleStringProperty("Student Worker"));
                }
                workersTableManageWorkersPage.getItems().add(data);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

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
        return column;
    }



}