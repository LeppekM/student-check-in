package Database;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Database {

    static String host = "jdbc:mysql://127.0.0.1:3306/?user=root";
    static final String dbdriver = "com.mysql.jdbc.Driver";
    static final String dbname = "local_student_check_in";
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
            //String test = "update vendor v set v.vendorID = 1, v.vendor = 'bob'";
            Statement preparedStatement = connection.createStatement();
            //preparedStatement.executeUpdate(test.toString());

            preparedStatement.execute("use local_student_check_in;");

            String testQuery = "desc parts;";
            ArrayList<String> result = new ArrayList<>();
            result = executeQuery(connection, testQuery);
            for (String s : result) {
                System.out.println(s);
            }
            preparedStatement.close();
        }catch (SQLException e){
            e.printStackTrace();
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