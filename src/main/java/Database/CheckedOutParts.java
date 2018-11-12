package Database;

import InventoryController.CheckedOutItems;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * This class queries the database for checked out items, and returns a student name, part name, quantity checked out, date checked out, and due date
 */
public class CheckedOutParts {
    private final String url = "jdbc:mysql://localhost:3306/student_check_in";
//    private final String username = "langdk";
//    private final String password = "password";
    private final String SELECTQUERY = "SELECT students.studentName, parts.partName, checkout_parts.checkoutQuantity, checkouts.checkoutAt, checkout_parts.dueAt\n" +
            "FROM checkout_parts\n" +
            "INNER JOIN parts \n" +
            "ON checkout_parts.partID = parts.partID\n" +
            "INNER JOIN checkouts\n" +
            "ON checkout_parts.checkoutID = checkouts.checkoutID\n" +
            "INNER JOIN students\n" +
            "ON checkouts.studentID = students.studentID";
    private Statement statement;
    private int checkoutQuantity;
    private String checkedOutAt;
    private String dueDate;
    private String partName;
    private String studentName;

    public ObservableList<CheckedOutItems> data = FXCollections.observableArrayList();

    /**
     * Queries the database for items that are checked out.
     */
    public void getCheckedOutItems(){

        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECTQUERY);
            while(resultSet.next()){
                setVariables(resultSet);
                CheckedOutItems checkedOutItems = new CheckedOutItems(studentName, partName, checkoutQuantity, checkedOutAt, dueDate);
                data.add(checkedOutItems);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database", e);
        }
    }

    /**
     * Sets variables to the results of the query
     * @param resultSet The results of the query
     */
    private void setVariables(ResultSet resultSet){
        try {
            studentName = resultSet.getString("studentName");
            partName = resultSet.getString("partName");
            checkoutQuantity = resultSet.getInt("checkoutQuantity");
            checkedOutAt = resultSet.getString("checkoutAt");
            dueDate = resultSet.getString("dueAt");

        } catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database");
        }
    }
}