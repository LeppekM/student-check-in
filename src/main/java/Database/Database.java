package Database;

import InventoryController.CheckedOutItems;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import java.util.Date;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Database {
    //DB root pass: Userpassword123
    public static final String username = "root";
    public static final String password = "Userpassword123";
    static String host = "jdbc:mysql://192.168.2.4:3306";
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
    public ObservableList<OverdueItem> getOverdue() {
        ObservableList<OverdueItem> data = FXCollections.observableArrayList();
        try {
            Date date = gettoday();
            String overdue = "select checkout_parts.partID, checkouts.studentID, students.studentName, students.email, parts.partName," +
                    " parts.serialNumber, checkout_parts.dueAt, parts.price/100, checkouts.checkoutID from checkout_parts " +
                    "left join parts on checkout_parts.partID = parts.partID " +
                    "left join checkouts on checkout_parts.checkoutID = checkouts.checkoutID " +
                    "left join students on checkouts.studentID = students.studentID " +
                    "where checkout_parts.dueAt < date('" + date.toString() + "');";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(overdue);
            while (resultSet.next()) {
                data.add(new OverdueItem(resultSet.getInt("checkouts.studentID"), resultSet.getString("students.studentName"),
                        resultSet.getString("students.email"), resultSet.getString("parts.partName"),
                        resultSet.getString("parts.serialNumber"), resultSet.getString("checkout_parts.dueAt"),
                        resultSet.getString("parts.price/100"), resultSet.getString("checkouts.checkoutID")));
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
        return new java.sql.Date(date);
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
        Notifications.create().title("Successful!").text("Part with ID = " + partID + " has been successfully deleted").hideAfter(new Duration(5000)).show();//.showWarning();
    }

    public void deleteParts(String partName) {
        try {
            String deleteQuery = "UPDATE parts p set p.deletedBy = 'root', p.isDeleted = 1, " +
                    "p.deletedAt = date('" + gettoday() + "') WHERE p.partName = '" + partName + "';";
            Statement statement = connection.createStatement();
            statement.executeUpdate(deleteQuery);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static ObservableList getHistory() {
        ObservableList<HistoryItems> data = FXCollections.observableArrayList();
        try {
            String historyQuery = "SELECT studentName, partName, serialNumber, " +
                    "CASE WHEN checkouts.checkoutAt < checkout_parts.checkedInAt " +
                    "THEN 'In' ELSE 'Out' END AS 'Status', " +
                    "CASE WHEN checkouts.checkoutAt < checkout_parts.checkedInAt " +
                    "THEN checkout_parts.checkedInAt ELSE checkouts.checkoutAt END AS 'date' " +
                    "FROM parts " +
                    "INNER JOIN checkout_parts ON parts.partID = checkout_parts.partID " +
                    "INNER JOIN checkouts ON checkout_parts.checkoutID = checkouts.checkoutID " +
                    "INNER JOIN students ON checkouts.studentID = students.studentID " +
                    "WHERE parts.isDeleted = 0 " +
                    "ORDER BY CASE " +
                    "WHEN checkouts.checkoutAt < checkout_parts.checkedInAt " +
                    "THEN checkout_parts.checkedInAt ELSE checkouts.checkoutAt END DESC;";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(historyQuery);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()) {
                data.add(new HistoryItems(resultSet.getString("studentName"), resultSet.getString("partName"),
                        resultSet.getString("serialNumber"),
                        resultSet.getString("location"), resultSet.getString("date")));
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
            while (resultSet.next()) {
                part = new Part(resultSet.getString("partName"), resultSet.getString("serialNumber"),
                        resultSet.getString("manufacturer"), resultSet.getDouble("price"), resultSet.getString("vendorID"),
                        resultSet.getString("location"), resultSet.getString("barcode"), false,
                        resultSet.getInt("partID"), resultSet.getInt("isDeleted"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return part;
    }

    public ArrayList<String> getSerialNumbersForBarcode(String barcode, String partID) {
        String query = "SELECT serialNumber FROM parts WHERE parts.isDeleted = 0 AND barcode = " + barcode + " AND partID != " + partID + ";";
        ArrayList<String> serialNumbers = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                serialNumbers.add(resultSet.getString("serialNumber"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return serialNumbers;
    }

    public ArrayList<String> getUniqueBarcodes() {
        String query = "SELECT distinct barcode FROM parts;";
        ArrayList<String> barcodes = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                barcodes.add(resultSet.getString("barcode"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return barcodes;
    }

    public boolean hasPartName(String partName) {
        String query = "SELECT * from parts where partName = '" + partName + "';";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Gets a student from the database based on their RFID
     *
     * @param ID RFID to search for
     * @return a student
     * @author Bailey Terry
     */
    public Student selectStudent(int ID){
        String todaysDate = gettoday().toString();
        String query = "select * from students where studentID = " + ID;
        String coList = "select students.studentName, parts.partName, checkouts.checkoutAt, checkout_parts.checkoutQuantity, checkout_parts.dueAt, checkouts.checkoutID \n" +
                "from students\n" +
                "left join checkouts on students.studentID = checkouts.studentID\n" +
                "left join checkout_parts on checkouts.checkoutID = checkout_parts.checkoutID\n" +
                "left join parts on checkout_parts.partID = parts.partID where students.studentID = " + ID  + ";";
        String pList = "select students.studentName, parts.partName, checkouts.checkoutAt, checkout_parts.checkoutQuantity, checkouts.reservedAt, checkout_parts.dueAt," +
                " checkouts.checkoutID, checkouts.prof, checkouts.course, checkouts.reason\n" +
                "from students\n" +
                "left join checkouts on students.studentID = checkouts.studentID\n" +
                "left join checkout_parts on checkouts.checkoutID = checkout_parts.checkoutID\n" +
                "left join parts on checkout_parts.partID = parts.partID where students.studentID = " + ID + " and checkouts.reservedAt != '';";
        String oList = "select checkout_parts.partID, checkouts.studentID, students.studentName, students.email, parts.partName," +
                " parts.serialNumber, checkout_parts.dueAt, parts.price/100, checkouts.checkoutID from checkout_parts " +
                "left join parts on checkout_parts.partID = parts.partID " +
                "left join checkouts on checkout_parts.checkoutID = checkouts.checkoutID " +
                "left join students on checkouts.studentID = students.studentID " +
                "where checkout_parts.dueAt < date('" + todaysDate + "') and students.studentID = " + ID + ";";
        Student student = null;
        String name = "";
        String email = "";
        String date = "";
        int id = 0;
        ObservableList<CheckedOutItems> checkedOutItems = FXCollections.observableArrayList();
        ObservableList<OverdueItem> overdueItems = FXCollections.observableArrayList();
        ObservableList<SavedPart> savedParts = FXCollections.observableArrayList();
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()){
                name = resultSet.getString("studentName");
                email = resultSet.getString("email");
                id = resultSet.getInt("studentID");
            }
            resultSet.close();
            statement.close();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(coList);
            resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()){
                checkedOutItems.add(new CheckedOutItems(resultSet.getString("students.studentName"),
                        resultSet.getString("parts.partName"), resultSet.getInt("checkout_parts.checkoutQuantity"),
                        resultSet.getString("checkouts.checkoutAt"), resultSet.getString("checkout_parts.dueAt"),
                        resultSet.getInt("checkouts.checkoutID")));
            }
            statement.close();
            resultSet.close();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(oList);
            resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()){
                overdueItems.add(new OverdueItem(resultSet.getInt("checkouts.studentID"),
                        resultSet.getString("students.studentName"), resultSet.getString("students.email"),
                        resultSet.getString("parts.partName"), resultSet.getString("parts.serialNumber"),
                        resultSet.getString("checkout_parts.dueAt"), resultSet.getString("parts.price/100"),
                        resultSet.getString("checkouts.checkoutID")));
            }
            statement.close();
            resultSet.close();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(pList);
            resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()){
                savedParts.add(new SavedPart(resultSet.getString("students.studentName"),
                        resultSet.getString("parts.partName"), resultSet.getString("checkouts.checkoutAt"),
                        resultSet.getInt("checkout_parts.checkoutQuantity"), resultSet.getString("checkouts.reservedAt"),
                        resultSet.getString("checkout_parts.dueAt"), resultSet.getString("checkouts.checkoutID"),
                        resultSet.getString("checkouts.prof"), resultSet.getString("checkouts.course"), resultSet.getString("checkouts.reason")));
            }
            statement.close();
            resultSet.close();
            if (checkedOutItems.size() > 0) {
                date = checkedOutItems.get(0).getCheckedOutAt().get();
            }
            for (int i = 0; i < checkedOutItems.size(); i++){
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date d = sdf.parse(date);
                    Date d1 = sdf.parse(checkedOutItems.get(i).getCheckedOutAt().get());
                    if (d1.after(d)){
                        date = checkedOutItems.get(i).getCheckedOutAt().get();
                    }
                }catch (ParseException e){
                    e.printStackTrace();
                }
            }
            student = new Student(name,id,email, date, checkedOutItems,overdueItems,savedParts);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return student;
    }

}