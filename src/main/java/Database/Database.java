package Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.sql.*;

public class Database {
    //DB root pass: Userpassword123
    public static final String username = "root";
    public static final String password = "Userpassword123";
    static String host = "jdbc:mysql://localhost:3306";
    static final String dbdriver = "com.mysql.jdbc.Driver";
    static final String dbname = "student_check_in";
    static Connection connection;

    /**
     * This creates a connection to the database
     *
     * @author Bailey Terry
     */
    public Database() {
        // Load the JDBC driver.
        // Library (.jar file) must be added to project build path.
        try {
            Class.forName(dbdriver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }

        connection = null;
        try {
            connection = DriverManager.getConnection((host + "/" + dbname),
                    username, password);
            connection.setClientInfo("autoReconnect", "true");
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * This method uses an SQL query to get all items in the databse with a due date less than todays date
     *
     * @return a list of overdue items
     * @author Bailey Terry
     */
    public ObservableList getOverdue() {
        ObservableList<OverdueItems> data = FXCollections.observableArrayList();
        try {
            Date date = gettoday();
            String overdue = "select checkout_parts.partID, checkouts.studentID, parts.partName, parts.serialNumber, checkout_parts.dueAt, parts.price/100" +
                    " from checkout_parts left join parts on checkout_parts.partID = parts.partID left join checkouts on checkout_parts.checkoutID = checkouts.checkoutID" +
                    " where checkout_parts.dueAt < date('" + date.toString() + "');";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(overdue);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()) {
                data.add(new OverdueItems(resultSet.getInt("checkouts.studentID"), resultSet.getString("parts.partName"),
                        resultSet.getString("parts.serialNumber"), resultSet.getString("checkout_parts.dueAt"),
                        resultSet.getInt("parts.price/100")));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Helper method to get the current date
     *
     * @return todays date
     * @author Bailey Terry
     */
    private static Date gettoday() {
        long date = System.currentTimeMillis();
        return new Date(date);
    }

    /**
     * This uses an SQL query to soft delete an item from the database
     *
     * @param partID a unique part id
     * @author Bailey Terry
     */
    public void deleteItem(int partID) {
        try {
            String delete = "update parts p set p.deletedBy = 'root', p.isDeleted = 1, p.deletedAt = date('" + gettoday() + "') where p.partID = " + partID + ";";
            Statement statement = connection.createStatement();
            statement.executeUpdate(delete);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //JOptionPane.showMessageDialog(null, "Part with ID = " + partID + " has been successfully deleted");
    }

    public Connection getConnection() {
        return connection;
    }

    public static ObservableList getHistory() {
        ObservableList<HistoryItems> data = FXCollections.observableArrayList();
        try {
            String historyQuery = "SELECT studentName, partName, serialNumber, location, " +
                    "checkoutQuantity - checkInQuantity AS 'quantity', CASE " +
                    "WHEN checkouts.checkoutAt < checkout_parts.checkedInAt " +
                    "THEN checkout_parts.checkedInAt ELSE checkouts.checkoutAt END AS 'date' " +
                    "FROM parts " +
                    "INNER JOIN checkout_parts ON parts.partID = checkout_parts.partID " +
                    "INNER JOIN checkouts ON checkout_parts.checkoutID = checkouts.checkoutID " +
                    "INNER JOIN students ON checkouts.studentID = students.studentID " +
                    "WHERE parts.deleted = 0 " +
                    "ORDER BY CASE " +
                    "WHEN checkouts.checkoutAt < checkout_parts.checkedInAt " +
                    "THEN checkout_parts.checkedInAt ELSE checkouts.checkoutAt END DESC;";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(historyQuery);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()) {
                data.add(new HistoryItems(resultSet.getString("studentName"), resultSet.getString("partName"),
                        resultSet.getString("serialNumber"), resultSet.getString("location"),
                        resultSet.getInt("quantity"), resultSet.getString("date")));
                resultSet.close();
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Helper method to get a specific part from the database
     *
     * @param partID unique id of the part
     * @return a Part
     * @author Bailey Terry
     */
    public Part selectPart(int partID) {
        String query = "select * from parts where partID = " + partID;
        Part part = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()) {
                part = new Part(resultSet.getString("partName"), resultSet.getString("serialNumber"),
                        resultSet.getString("manufacturer"), resultSet.getDouble("price"), resultSet.getString("vendorID"),
                        resultSet.getString("location"), resultSet.getString("barcode"), false,
                        resultSet.getInt("partID"), resultSet.getBoolean("isDeleted"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return part;
    }

    //    private static ObservableList<HistoryItems> executeQuery(Connection connection, String query) {
//        ResultSet results = null;
//        ArrayList<String> resultList = new ArrayList<>();
//        Statement statement = null;
//        try {
//            statement = connection.createStatement();
//            results = statement.executeQuery(query);
//            while (results.next()) {
//                resultList.add(results.getString(1));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            System.exit(0);
//        } finally {
//            try {
//                results.close();
//                statement.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        return resultList;
//    }

}