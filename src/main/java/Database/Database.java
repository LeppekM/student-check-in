package Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.sql.*;

public class Database {

    public static final String username = "root";
    public static final String password = "Rootpass123";
    static String host = "jdbc:mysql://localhost:3306";
    static final String dbdriver = "com.mysql.jdbc.Driver";
    static final String dbname = "student_check_in";
    static Connection connection;

//    private String circuitDesigners = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
//            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy, isDeleted)" +
//            " values (?, 'Circuit Designers', ?, 'MSOE', 9800, 1, 'S350 A1', NULL, 1, 0, 1, date('" + gettoday() + "')," +
//            "NULL, 0);";//1-103, 1-103
//    private String analog = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
//            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy, isDeleted)" +
//            " values (?, 'Analog Discovery 2', ?, 'Digilent', 27900, 2, 'S350 B1', NULL, 1, 0, 1, date('" + gettoday() + "')," +
//            "NULL, 0);";//494-579, 1-85

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

//        LoadRealDatabase loadRealDatabase = new LoadRealDatabase(connection);
//        String[] tables = {"parts", "fault", "checkout_parts", "checkouts", "students", "vendors"};
//        for (String s: tables){
//            loadRealDatabase.clearDatabase(s);
//        }
//        int j = 0;
//        for (int i = 1; i < 188; i++) {
//            if (i < 104) {
//                j = i;
//                loadRealDatabase.loadDatabase(circuitDesigners, i, j);
//            }else {
//                j = i - 103;
//                loadRealDatabase.loadDatabase(analog, i, j);
//            }
//        }
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
            String delete = "update parts p set p.deletedBy = 'root', p.isDeleted = 1, p.deletedAt = date('" + gettoday() +"') where p.partID = " + partID + ";";
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

    public Part selectPart(int partID){
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
        }catch (SQLException e){
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