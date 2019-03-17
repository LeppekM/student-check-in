package Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.sql.*;

/**
 * This class queries the database for the transaction history, and returns a student name, part name, serial number, status, quantity, date
 */
public class HistoryParts {

    private static String host = Database.host;
    private static final String dbdriver = "com.mysql.jdbc.Driver";
    private static final String dbname = "student_check_in";
    private static Connection connection;

    private static final String HISTORY_QUERY = "SELECT studentName, email, partName, serialNumber, " +
            "CASE WHEN checkout.checkoutAt < checkout.checkinAt " +
            "THEN 'In' ELSE 'Out' END AS 'Status', " +
            "CASE WHEN checkout.checkoutAt < checkout.checkinAt " +
            "THEN checkout.checkinAt ELSE checkout.checkoutAt END AS 'date' " +
            "FROM parts " +
            "INNER JOIN checkout ON parts.partID = checkout.partID " +
//            "INNER JOIN checkout ON checkout.checkoutID = checkout.checkoutID " +
            "INNER JOIN students ON checkout.studentID = students.studentID " +
            "WHERE parts.isDeleted = 0 " +
            "ORDER BY CASE " +
            "WHEN checkout.checkoutAt < checkout.checkinAt " +
            "THEN checkout.checkinAt ELSE checkout.checkoutAt END DESC;";

    private Statement statement;
    private String studentName, studentEmail, partName, serialNumber, status, date;

    public ObservableList<HistoryItems> data = FXCollections.observableArrayList();

    /**
     * Queries the database for the transaction history.
     */
    public ObservableList<HistoryItems> getHistoryItems(){
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
                HistoryItems historyItems = new HistoryItems(studentName, studentEmail, partName, serialNumber, status, date);
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
            serialNumber = resultSet.getString("serialNumber");
            status = resultSet.getString("status");
            date = resultSet.getString("date");

        } catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database");
        }
    }

}
