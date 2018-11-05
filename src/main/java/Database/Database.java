package Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Database {

    static String host = "jdbc:mysql://192.168.56.1:3306";
    static final String dbdriver = "com.mysql.jdbc.Driver";
    static final String dbname = "parts";
    static Connection connection;

    public Database() {
        // scanner.useDelimiter("\n");

        String login = JOptionPane.showInputDialog("Enter login name: ");

        // Note: password will be echoed to console;
        String password = JOptionPane.showInputDialog("Enter password: ");

        // String password = PasswordField.readPassword("Enter password: ");

        JOptionPane.showMessageDialog(null,"Connecting as user '" + login + "' . . .");

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
                    login, password);
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
            String overdue = "select checkout_parts.partID, parts.partName, parts.serialNumber, checkout_parts.dueAt, parts.price" +
                    " from checkout_parts left join parts on checkout_parts.partID = parts.partID" +
                    " where checkout_parts.dueAt < date('" + date.toString() + "');";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(overdue);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()){
                data.add(new OverdueItems(resultSet.getInt("checkout_parts.partID"), resultSet.getString("parts.partName"),
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd");
        long date = System.currentTimeMillis();
        return new Date(date);
    }
}
