package Database;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Database {

    static String host = "jdbc:mysql://127.0.0.1:3306";
    static final String dbdriver = "com.mysql.jdbc.Driver";
    static final String dbname = "local_student_check_in";
    static Connection connection;

    public void test() {
        // scanner.useDelimiter("\n");

        String login = JOptionPane.showInputDialog("Enter login name: ");

        // Note: password will be echoed to console;
//        String password = JOptionPane.showInputDialog("Enter password: ");
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enter a password: ");
        JPasswordField pass = new JPasswordField(20);
        panel.add(label);
        panel.add(pass);
        String[] options = new String[]{"OK", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, panel, "Input",
                                                JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                                            null, options, options[1]);
        String password = new String(pass.getPassword());

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

        //String test = "update vendor v set v.vendorID = 1, v.vendor = 'bob'";

        String testQuery = "desc parts;";
        ArrayList<String> result = new ArrayList<>();
        result = executeQuery(connection, testQuery);
        for (String s : result) {
            System.out.print(s + " ");
        }
    }

    private static ArrayList<String> executeQuery(Connection connection, String query) {
        ResultSet results = null;
        ArrayList<String> resultList = new ArrayList<>();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            results = statement.executeQuery(query);
            while (results.next()) {
                resultList.add(results.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        } finally {
            try {
                results.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }
}