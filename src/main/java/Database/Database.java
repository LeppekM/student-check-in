package Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import javax.swing.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Database {

    static String host = "jdbc:mysql://localhost:3306";
    static final String dbdriver = "com.mysql.jdbc.Driver";
    static final String dbname = "student_check_in";
    static Connection connection;

    public Database(String username, String password) {
        // scanner.useDelimiter("\n");

//        String login = JOptionPane.showInputDialog("Enter login name: ");
//
//        // Note: password will be echoed to console;
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
//        JOptionPane.showMessageDialog(null,"Connecting as user '" + login + "' . . .");

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

    public ObservableList getOverdue(){
        ObservableList<OverdueItems> data = FXCollections.observableArrayList();
        try {
            Date date = gettoday();
            String overdue = "select checkout_parts.partID, checkouts.studentID, parts.partName, parts.serialNumber, checkout_parts.dueAt, parts.price" +
                    " from checkout_parts left join parts on checkout_parts.partID = parts.partID left join checkouts on checkout_parts.checkoutID = checkouts.checkoutID" +
                    " where checkout_parts.dueAt < date('" + date.toString() + "');";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(overdue);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()){
                data.add(new OverdueItems(resultSet.getInt("checkouts.studentID"), resultSet.getString("parts.partName"),
                        resultSet.getString("parts.serialNumber"), resultSet.getString("checkout_parts.dueAt"),
                        resultSet.getInt("parts.price")));
            }
            resultSet.close();
            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return data;
    }

    private static Date gettoday(){
        long date = System.currentTimeMillis();
        return new Date(date);
    }

    public void deleteItem(int partID){
        try{
            String delete = "update parts p set p.deletedBy = 'root' where p.partID = " + partID + ";";
            Statement statement = connection.createStatement();
            statement.executeUpdate(delete);
            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(null, "Part with ID = " + partID + " has been successfully deleted");
    }

    public Connection getConnection(){
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