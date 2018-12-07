package InventoryController;

import Database.Database;
import Database.OverdueItems;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class OverduePopUp implements Initializable {

    @FXML
    private JFXTextField name, email, serialNumber, partName, dueDate, fee;

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
    }

    public void populate(OverdueItems overdueItems){
        try {
            Connection connection = database.getConnection();
            PreparedStatement statement1;
            PreparedStatement statement2;
            ResultSet resultSet;
            ResultSetMetaData resultSetMetaData;
            String query1 = "select studentName, email from students where studentID = ?";
            String query2 = "select p.part, p.serialNumber, cp.dueAt, p.price \n" +
                    "from parts p\n" +
                    "left join checkout_parts cp on cp.partID = p.partID where p.partName = ?;";
            statement1 = connection.prepareStatement(query1);
            statement1.setInt(1, overdueItems.getID());
            resultSet = statement1.executeQuery(query1);
            resultSetMetaData = resultSet.getMetaData();
            while(resultSet.next()){
                name.setText(resultSet.getString("studentName"));
                email.setText(resultSet.getString("email"));
            }
            resultSet.close();
            statement1.close();
            statement2 = connection.prepareStatement(query2);
            statement2.setString(1, overdueItems.getPart());
            resultSet = statement2.executeQuery(query2);
            resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()){
                partName.setText(resultSet.getString("p.part"));
                serialNumber.setText(resultSet.getString("p.serialNumber"));
                dueDate.setText(resultSet.getString("cp.dueAt"));
                fee.setText(resultSet.getString("p.price"));
            }
            resultSet.close();
            statement2.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
