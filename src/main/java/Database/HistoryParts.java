package Database;

import HelperClasses.DatabaseHelper;
import InventoryController.HistoryTabTableRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * This class queries the database for the transaction history, and returns a student name, part name, serial number, action, quantity, date
 */
public class HistoryParts {

    private static String host = Database.host;
    private static final String dbdriver = "com.mysql.jdbc.Driver";
    private static final String dbname = "student_check_in";
    private static Connection connection;

    // This query is used to get the data for the transaction history table in the inventory
    private static final String HISTORY_QUERY = "SELECT studentName, email, partName, parts.barcode, " +
            "CASE WHEN checkout.checkoutAt < checkout.checkinAt " +
            "THEN 'Checked In' ELSE 'Checked Out' END AS 'Action', " +
            "CASE WHEN checkout.checkoutAt < checkout.checkinAt " +
            "THEN checkout.checkinAt ELSE checkout.checkoutAt END AS 'Date' " +
            "FROM parts " +
            "INNER JOIN checkout ON parts.partID = checkout.partID " +
            "INNER JOIN students ON checkout.studentID = students.studentID " +
            "ORDER BY CASE " +
            "WHEN checkout.checkoutAt < checkout.checkinAt " +
            "THEN checkout.checkinAt ELSE checkout.checkoutAt END DESC;";

    private Statement statement;
    private String studentName, studentEmail, partName, action ,date;

    private long barcode;

    private DatabaseHelper helper = new DatabaseHelper();

    public ObservableList<HistoryTabTableRow> data = FXCollections.observableArrayList();

    /**
     * Queries the database for the transaction history.
     */
    public ObservableList<HistoryTabTableRow> getHistoryItems(){
        try {
            Class.forName(dbdriver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }

        try (Connection connection = DriverManager.getConnection((host + "/" + dbname), Database.username, Database.password)) {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(HISTORY_QUERY);
            while(resultSet.next()){
                setVariables(resultSet);
                HistoryTabTableRow historyItems = new HistoryTabTableRow(studentName, studentEmail, partName, barcode, action, helper.convertStringtoDate(date));
                data.add(historyItems);
            }
        } catch (SQLException e) {
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
            barcode = resultSet.getLong("barcode");
            action = resultSet.getString("Action");
            date = resultSet.getString("Date");

        } catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database");
        }
    }

}
