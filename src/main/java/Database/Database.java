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

    public void test() {
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

    public static ObservableList getOverdue(){
        ObservableList<OverdueItems> data = FXCollections.observableArrayList();
        try {
            Date date = gettoday();
            String overdue = "select cp.partID, p.partName, p.serialNumber, cp.dueAt, p.price" +
                    "from checkout_parts cp left join parts p on cp.partID = p.partID" +
                    "where cp.dueAt < " + date.toString() + ";";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(overdue);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()){
                data.add(new OverdueItems(resultSet.getInt("cp.partID"), resultSet.getString("p.partName"),
                        resultSet.getString("p.serialNumber"), resultSet.getDate("cp.dueAt").toString(),
                        resultSet.getInt("p.price")));
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
