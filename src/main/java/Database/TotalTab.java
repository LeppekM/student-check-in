package Database;

import Database.ObjectClasses.Part;
import InventoryController.CheckedOutItems;
import InventoryController.StudentCheckIn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class TotalTab {

    private final String url = Database.host + "/student_check_in";

    private Statement statement;

    public ObservableList<Part> data = FXCollections.observableArrayList();

    private String partName, location, serialNumber;
    private long barcode;
    private int  partID;
    private boolean fault;



    public ObservableList<Part> getTotalTabParts(){
        String query ="select partName, serialNumber, barcode, location, isFaulty, partID from parts;";
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                setVariables(resultSet);
                Part part = new Part(partName, serialNumber, location, barcode, fault, partID);
                data.add(part);
            }
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQL Error: Can't connect to the database.");
            throw new IllegalStateException("Cannot connect the database", e);

        }
        return data;
    }


    /**
     * Sets variables to the results of the query
     * @param resultSet The results of the query
     */
    private void setVariables(ResultSet resultSet){
        try {

            partName = resultSet.getString("partName");
            barcode = resultSet.getLong("barcode");
            serialNumber = resultSet.getString("serialNumber");
            location = resultSet.getString("location");
            fault = resultSet.getBoolean("isFaulty");
            partID = resultSet.getInt("parts.partID");

        } catch (SQLException e){
            StudentCheckIn.logger.error("Cannot connect to the database while populating CheckedOutParts");
            throw new IllegalStateException("Cannot connect to the database");
        }
    }
}
