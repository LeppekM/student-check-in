package Database;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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

        try {
            String test = "insert into vendors (vendorID, vendor) values (1, 'bob')";
            Statement preparedStatement = connection.createStatement();
            preparedStatement.executeUpdate(test.toString());
            preparedStatement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
