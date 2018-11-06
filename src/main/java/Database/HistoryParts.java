package Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.sql.*;

public class HistoryParts {

    private static String host = "jdbc:mysql://localhost:3306";
    private static final String dbdriver = "com.mysql.jdbc.Driver";
    private static final String dbname = "student_check_in";
    private static Connection connection;

    private static final String HISTORY_QUERY = "SELECT studentName, partName, serialNumber, location, " +
            "checkoutQuantity - checkInQuantity AS 'quantity', CASE " +
            "WHEN checkouts.checkoutAt < checkout_parts.checkedInAt " +
            "THEN checkout_parts.checkedInAt ELSE checkouts.checkoutAt END AS 'date' " +
            "FROM parts " +
            "INNER JOIN checkout_parts ON parts.partID = checkout_parts.partID " +
            "INNER JOIN checkouts ON checkout_parts.checkoutID = checkouts.checkoutID " +
            "INNER JOIN students ON checkouts.studentID = students.studentID " +
            "WHERE parts.isDeleted = 0 " +
            "ORDER BY CASE " +
            "WHEN checkouts.checkoutAt < checkout_parts.checkedInAt " +
            "THEN checkout_parts.checkedInAt ELSE checkouts.checkoutAt END DESC;";
    private Statement statement;
    private int quantity;
    private String studentName, partName, serialNumber, location, date;

    public ObservableList<HistoryItems> data = FXCollections.observableArrayList();

    /**
     * Queries the database for items that are checked out.
     */
    public ObservableList<HistoryItems> getHistoryItems(String username, String password){
//        String login = JOptionPane.showInputDialog("Enter login name: ");
//
//// Note: password will be echoed to console;
////        String password = JOptionPane.showInputDialog("Enter password: ");
//        JPanel panel = new JPanel();
//        JLabel label = new JLabel("Enter a password: ");
//        JPasswordField pass = new JPasswordField(20);
//        panel.add(label);
//        panel.add(pass);
//        String[] options = new String[]{"OK", "Cancel"};
//        int option = JOptionPane.showOptionDialog(null, panel, "Input",
//                JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
//                null, options, options[1]);
//        String password = new String(pass.getPassword());
//
//        JOptionPane.showMessageDialog(null, "Connecting as user '" + login + "' . . .");

        // Load the JDBC driver.
        // Library (.jar file) must be added to project build path.
        try {
            Class.forName(dbdriver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }

        try (Connection connection = DriverManager.getConnection((host + "/" + dbname), username, password)) {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(HISTORY_QUERY);
            while(resultSet.next()){
                setVariables(resultSet);
                HistoryItems historyItems = new HistoryItems(studentName, partName, serialNumber, location, quantity, date);
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
            partName = resultSet.getString("partName");
            serialNumber = resultSet.getString("serialNumber");
            location = resultSet.getString("location");
            quantity = resultSet.getInt("quantity");
            date = resultSet.getString("date");

        } catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database");
        }
    }

}
