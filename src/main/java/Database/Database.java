package Database;

import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    static String host = "jdbc:mysql://localhost:3306";
    static final String dbdriver = "com.mysql.jdbc.Driver";
    static final String dbname = "parts";
    static Connection connection;

    public void connect() {
        // scanner.useDelimiter("\n");

        String login = JOptionPane.showInputDialog("Enter login name: ");

        // Note: password will be echoed to console;
        String password = JOptionPane.showInputDialog("Enter password: ");

        // String password = PasswordField.readPassword("Enter password: ");

        JOptionPane.showMessageDialog(null, "Connecting as user '" + login + "' . . .");

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

    public void deleteItem(int partID){
        try{
            String delete = "update parts p set p.isDeleted = 1 where p.partID = " + partID + ";";
            Statement statement = connection.createStatement();
            statement.executeUpdate(delete);
        }catch (SQLException e){
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(null, "Part with ID = " + partID + " has been successfully deleted");
    }
}
