package Database;

import Database.ObjectClasses.Part;
import InventoryController.StudentCheckIn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class TotalTab {

    private final String url = Database.host + "/student_check_in";

    public ObservableList<Part> data = FXCollections.observableArrayList();

    private String partName, location, serialNumber;
    private long barcode;
    private int  partID, price;


    public ObservableList<Part> getTotalTabParts(){
        String query ="select partName, serialNumber, barcode, location, partID, price from parts;";
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                setVariables(resultSet);
                Part part = new Part(partName, serialNumber, location, barcode, partID, price);
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
            partID = resultSet.getInt("parts.partID");
            price = resultSet.getInt("price");

        } catch (SQLException e){
            StudentCheckIn.logger.error("Cannot connect to the database while populating CheckedOutParts");
            throw new IllegalStateException("Cannot connect to the database");
        }
    }
}
