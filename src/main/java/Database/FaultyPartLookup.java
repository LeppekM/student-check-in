package Database;



import InventoryController.FaultyPartTabTableRow;
import InventoryController.StudentCheckIn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class FaultyPartLookup {
    private final String url = Database.host + "/student_check_in";
    private Statement statement;
    private final String selectFaulty = "\n" +
            "select parts.partName, parts.location, parts.barcode, fault.partID, fault.description, parts.partID\n" +
            "from fault\n" +
            "inner join parts on fault.partID = parts.partID";
    private String studentName, studentEmail, partName, barcode, description, price, location, date;

    /**
     * Method to return detailed faulty part info
     * @return Return detailed faulty part info
     */
    public ObservableList<FaultyPartTabTableRow> getDetailedFaultyInfo(){
        ObservableList<FaultyPartTabTableRow> data = FXCollections.observableArrayList();
        String query = " select students.studentName, students.email,  parts.partName, parts.barcode, fault.description, parts.price, parts.location, checkout.checkinAt\n" +
                " from fault\n" +
                " inner join parts on fault.partID = parts.partID\n" +
                " inner join checkout on fault.checkoutID = checkout.checkoutID\n" +
                " inner join students on checkout.studentID = students.studentID;";

        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                setVariables(resultSet);
                FaultyPartTabTableRow faultyItems = new FaultyPartTabTableRow(studentName, studentEmail, partName, barcode, description, price, location, date);
                data.add(faultyItems);
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
            studentName = resultSet.getString("studentName");
            studentEmail = resultSet.getString("email");
            partName = resultSet.getString("partName");
            barcode = resultSet.getString("barcode");
            description = resultSet.getString("description");
            price = resultSet.getString("price");
            location = resultSet.getString("location");
            date = resultSet.getString("checkinAt");
        } catch (SQLException e){
            StudentCheckIn.logger.error("Cannot connect to the database while populating CheckedOutParts");
            throw new IllegalStateException("Cannot connect to the database");
        }
    }

}
