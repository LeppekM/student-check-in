package Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.sql.*;

public class Database {

    static String host = "jdbc:mysql://localhost:3306";
    static final String dbdriver = "com.mysql.jdbc.Driver";
    static final String dbname = "student_check_in";
    static Connection connection;

    private String circuitDesigners = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Circuit Designers', ?, 'MSOE', 9800, 0, 'S350 A1', NULL, 1, 0, 1, date('" + gettoday() + "')," +
            "NULL);";//1-103, 1-103
    private String wireKits = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Wire Kits', ?, 'RSR', 1195, 0, 'S350 B1', NULL, 1, 0, 1, date('" + gettoday() + "')," +
            "NULL);";//104-207, 1-103
    private String partsBoxes = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Parts Boxes', ?, 'MSOE', 5000, 0, 'S350 C1', NULL, 1, 0, 1, date('" + gettoday() + "')," +
            "NULL);";//208-297, 1-89
    private String cypressPSO = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Cypress PSOC5LP Dev. Bds.', ?, 'Cypress', 9343, 0, 'S350 D1', NULL, 1, 0, 1, date('" + gettoday() + "')," +
            "NULL);";//298-378, 1-80
    private String cypressFM = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Cypress FM4 Kit', ?, 'Cypress', 6945, 0, 'S350 E1', NULL, 1, 0, 1, date('" + gettoday() + "')," +
            "NULL);";//379-439, 1-60
    private String stampKit = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Basic Stamp Kit', ?, 'MSOE', 16700, 0, 'S350 F1', NULL, 1, 0, 1, date('" + gettoday() + "')," +
            "NULL);";//440-466, 1-26
    private String olimex = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Olimex ECG/EMG', ?, 'MSOE', 2453, 0, 'S350 G1', NULL, 1, 0, 1, date('" + gettoday() + "')," +
            "NULL);";//467-479, 1-12
    private String tens = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'TENS Unit', ?, 'TENS', 4999, 0, 'S350 H1', NULL, 1, 0, 1, date('" + gettoday() + "')," +
            "NULL);";//480-493, 1-13
    private String analog = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Analog Discovery 2', ?, 'Digilent', 27900, 0, 'S350 I1', NULL, 1, 0, 1, date('" + gettoday() + "')," +
            "NULL);";//494-579, 1-85
    private String fluke123 = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Fluke 123 Scopemeter', ?, 'Fluke', 189900, 0, 'S350 J1', NULL, 1, 0, 1, date('" + gettoday() + "')," +
            "NULL);";//580-591, 1-11
    private String fluke43 = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Fluke 43 & 43B Power Quality Analyzer', ?, 'Fluke', 346900, 0, 'S350 K1', NULL, 1, 0, 1, date('" + gettoday() + "')," +
            "NULL);";//592-610, 1-18
    private String nexus = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'SE1021 Nexus Kits', ?, 'Nexus', 8200, 0, 'S350 L1', NULL, 1, 0, 1, date('" + gettoday() + "')," +
            "NULL);";//611-631, 1-20
    private String mke = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Milwaukee Tool DMM', ?, 'Milwaukee Tool', 15900, 0, 'S350 M1', NULL, 1, 0, 1, date('" + gettoday() + "')," +
            "NULL);";//632-682, 1-50

    public Database(String username, String password) {
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
        String[] tables = {"parts", "fault", "checkout_parts", "checkouts", "students", "vendors"};
        for (String s: tables){
            clearDatabase(s);
        }
        int j = 0;
        for (int i = 1; i < 683; i++) {
            if (i < 104) {
                j = i;
                loadDatabase(circuitDesigners, i, j);
            }else if (i < 208) {
                j = i - 103;
                loadDatabase(wireKits, i, j);
            }else if (i < 298) {
                j = i - 207;
                loadDatabase(partsBoxes, i, j);
            }else if (i < 379) {
                j = i - 297;
                loadDatabase(cypressPSO, i, j);
            }else if (i < 440) {
                j = i - 378;
                loadDatabase(cypressFM, i, j);
            }else if (i < 467) {
                j = i - 439;
                loadDatabase(stampKit, i, j);
            }else if (i < 480) {
                j = i - 466;
                loadDatabase(olimex, i, j);
            }else if (i < 494) {
                j = i - 479;
                loadDatabase(tens, i, j);
            }else if (i < 580) {
                j = i - 493;
                loadDatabase(analog, i, j);
            }else if (i < 592) {
                j = i - 579;
                loadDatabase(fluke123, i, j);
            }else if (i < 611) {
                j = i - 591;
                loadDatabase(fluke43, i, j);
            }else if (i < 632) {
                j = i - 610;
                loadDatabase(nexus, i, j);
            }else {
                j = i - 631;
                loadDatabase(mke, i, j);
            }
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

    private void loadDatabase(String partQuery, int partIDValue, int serialValue){
//        String[] tables = {"parts", "fault", "checkout_parts", "checkouts", "students", "vendors"};
//        for (String s: tables){
//            clearDatabase(s);
//        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(partQuery);
//            for (int i = partIDMinValue; i < partIDMaxValue; i++){
            preparedStatement.setInt(1, partIDValue);
//            }
//            for (int i = 1; i < serialMaxValue; i++){
            preparedStatement.setInt(2, serialValue);
//            }
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void clearDatabase(String table){
        String clearTable =  "delete from " + table + ";";
        try{
            Statement statement = connection.createStatement();
            statement.execute(clearTable);
        }catch (SQLException e){

        }
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