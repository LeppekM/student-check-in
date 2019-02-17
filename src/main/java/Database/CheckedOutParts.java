package Database;

import HelperClasses.StageWrapper;
import InventoryController.CheckedOutItems;
import InventoryController.StudentCheckIn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.sql.*;
import java.time.ZoneId;
import java.util.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class queries the database for checked out items, and returns a student name, part name, quantity checked out, date checked out, and due date
 */
public class CheckedOutParts {
    private final String url = Database.host + "/student_check_in";
    private final String SELECTQUERY = "SELECT students.studentName, parts.partName, parts.barcode, checkout.checkoutAt, checkout.dueAt, checkout.checkoutID, parts.partID\n" +
            "FROM checkout\n" +
            "INNER JOIN parts on checkout.partID = parts.partID\n" +
            "INNER JOIN students on checkout.studentID = students.studentID\n" +
            "WHERE checkout.checkinAt IS NULL";

    private Statement statement;
    private int barcode, checkoutID, partID;
    private String checkedOutAt;
    private String dueDate;
    private String partName;
    private String studentName;

    public ObservableList<CheckedOutItems> data = FXCollections.observableArrayList();

    /**
     * Queries the database for items that are checked out.
     */
    public ObservableList<CheckedOutItems> getCheckedOutItems(){

        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECTQUERY);
            while(resultSet.next()){
                setVariables(resultSet);
                CheckedOutItems checkedOutItems = new CheckedOutItems(studentName, partName, barcode, checkedOutAt, dueDate, checkoutID, partID);
                data.add(checkedOutItems);
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
            partName = resultSet.getString("partName");
            barcode = resultSet.getInt("barcode");
            checkedOutAt = resultSet.getString("checkoutAt");
            dueDate = resultSet.getString("dueAt");
            checkoutID = resultSet.getInt("checkoutID");
            partID = resultSet.getInt("partID");
        } catch (SQLException e){
            StudentCheckIn.logger.error("Cannot connect to the database while populating CheckedOutParts");
            throw new IllegalStateException("Cannot connect to the database");
        }
    }

}