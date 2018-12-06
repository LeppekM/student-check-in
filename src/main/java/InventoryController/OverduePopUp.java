package InventoryController;

import Database.Database;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class OverduePopUp implements Initializable {

    @FXML
    private JFXTextField name, email, serialNumber, partName, dueDate, fee;

    @FXML
    private AnchorPane overduePopUp;

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
        String sName = "";
        String sEmail = "";
        String part = "";
        String serial = "";
        String due = "";
        String fee = "";
        try {
            Connection connection = database.getConnection();
            PreparedStatement statement;
            String query1 = "select studentName, email from students where studentID = ?";
            statement = connection.prepareStatement(query1);
            statement.setInt(1, overduePopUp.getParent().);
        }catch (SQLException e){

        }
    }
}
