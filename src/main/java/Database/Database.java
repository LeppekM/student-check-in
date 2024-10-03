package Database;

import Controllers.IController;
import Database.ObjectClasses.Checkout;
import Database.ObjectClasses.Part;
import Database.ObjectClasses.Student;
import Database.ObjectClasses.Worker;
import HelperClasses.TimeUtils;
import HelperClasses.StageUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * This is the Object that works as the data access layer, and is the only object in the project which touches the
 * database. This Object is a singleton to prevent too many connections at a time, and acts as a utility/helper class
 */
public class Database implements IController {
    //DB root pass: Userpassword123
    public static final String USERNAME = "root";
    public static final String PASSWORD = "Userpassword123";
    private static final String HOST = "jdbc:mysql://localhost:3306";
    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_NAME = "/student_check_in";
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 2000; // 2 seconds
    private static final int CONNECTION_TIMEOUT = 10; // 10 seconds
    private static Connection connection;
    private final TimeUtils timeUtils = new TimeUtils();
    private Worker worker;
    private final StageUtils stageUtils = StageUtils.getInstance();

    private static final Database database = new Database();
    /**
     * This creates a connection to the database
     */
    private Database() {
        // Load the JDBC driver.
        // Library (.jar file) must be added to project build path.
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
        connection = null;
        DriverManager.setLoginTimeout(3);
        try {
            connection = DriverManager.getConnection((HOST + DB_NAME),
                    USERNAME, PASSWORD);
            connection.setClientInfo("autoReconnect", "true");
        } catch (SQLException e) {
            stageUtils.errorAlert("Error, could not connect to the database.");
        }
    }

    public static Database getInstance() {
        return database;
    }

    private Connection reconnect() {
        Connection conn = null;
        int retries = MAX_RETRIES;

        DriverManager.setLoginTimeout(CONNECTION_TIMEOUT); // Set timeout for connection attempts

        while (retries > 0) {
            try {
                conn = DriverManager.getConnection(HOST + DB_NAME, USERNAME, PASSWORD);
                break; // Break the loop if connection is successful
            } catch (SQLException e) {
                retries--;
                if (retries == 0) {
                    // Handle max retries exceeded
                    stageUtils.errorAlert("Error, could not reconnect to the database after multiple attempts.");
                } else {
                    try {
                        System.out.println("Retrying database connection... " + retries + " attempts left.");
                        Thread.sleep(RETRY_DELAY_MS); // Wait before retrying
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt(); // Restore interrupt flag if interrupted
                    }
                }
            }
        }
        return conn;
    }

    /**
     * Returns the connection to the database created by constructor method
     * @return the database connection
     */
    private Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = reconnect();
            }
        } catch (SQLException ignored) { }

        return connection;
    }

    public ResultSet executeQueryWithRetry(String query) throws SQLException {
        int retries = MAX_RETRIES;
        ResultSet resultSet = null;
        while (retries > 0) {
            try {
                Connection conn = getConnection();
                Statement statement = conn.createStatement();
                statement.setQueryTimeout(CONNECTION_TIMEOUT); // Set timeout for the query execution
                resultSet = statement.executeQuery(query); // Execute the query
                break; // Successful query execution, break the loop
            } catch (SQLException e) {
                retries--;
                if (retries == 0) {
                    throw new SQLException("Error, could not execute query after multiple attempts.", e);
                } else {
                    System.out.println("Retrying query execution... " + retries + " attempts left.");
                    try {
                        Thread.sleep(RETRY_DELAY_MS); // Wait before retrying
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt(); // Restore the interrupt flag
                    }
                }
            }
        }
        return resultSet; // Note: ResultSet must be closed later (via try-with-resources block)
    }

    public int executeUpdateWithRetry(String query) throws SQLException {
        int retries = MAX_RETRIES;
        int result = 0;
        while (retries > 0) {
            try {
                Connection conn = getConnection();
                Statement statement = conn.createStatement();
                statement.setQueryTimeout(CONNECTION_TIMEOUT); // Set timeout for the update execution
                result = statement.executeUpdate(query); // Execute the update (INSERT, UPDATE, or DELETE)
                break; // Successful execution, break the loop
            } catch (SQLException e) {
                retries--;
                if (retries == 0) {
                    throw new SQLException("Error, could not execute update after multiple attempts.", e);
                } else {
                    System.out.println("Retrying update execution... " + retries + " attempts left.");
                    try {
                        Thread.sleep(RETRY_DELAY_MS); // Wait before retrying
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt(); // Restore the interrupt flag
                    }
                }
            }
        }
        return result;
    }

    /**
     * This method uses an SQL query to get all items in the database with a due date less than today's date
     * @return a list of overdue items
     */
    public ObservableList<OverdueItem> getOverdue() {
        ObservableList<OverdueItem> data = FXCollections.observableArrayList();

        String overdue = "SELECT checkout.partID, checkout.studentID, students.studentName, students.email, " +
                "parts.partName, parts.serialNumber, parts.barcode, checkout.dueAt, checkout.checkoutID " +
                "FROM checkout LEFT JOIN parts ON checkout.partID = parts.partID " +
                "LEFT JOIN students ON checkout.studentID = students.studentID " +
                "WHERE checkout.checkinAt IS NULL;";
        try (ResultSet resultSet = executeQueryWithRetry(overdue)) {
            while (resultSet.next()) {
                String dueAt = resultSet.getString("checkout.dueAt");
                if (isOverdue(dueAt)) {
                    data.add(new OverdueItem(resultSet.getLong("checkout.studentID"),
                            resultSet.getString("students.studentName"), resultSet.getString("students.email"),
                            resultSet.getString("parts.partName"), resultSet.getString("parts.serialNumber"),
                            resultSet.getLong("parts.barcode"), timeUtils.convertStringtoDate(dueAt),
                            resultSet.getString("checkout.checkoutID")));
                }
            }
        } catch (SQLException e) {
            stageUtils.errorAlert("Error, could not retrieve all overdue parts");
        }
        return data;
    }

    /**
     * This uses an SQL query to delete a specific part from the database
     * @param partID a unique part id
     */
    public void deletePart(int partID) {
        String delete = "delete from parts where partID = " + partID + ";";
        try {
            executeUpdateWithRetry(delete);
        } catch (SQLException e) {
            stageUtils.errorAlert("Error, could not delete part with ID " + partID);
        }
        Notifications.create().title("Successful!").text("Part with ID = " + partID + " has been successfully deleted")
                .hideAfter(new Duration(5000)).show();
    }

    /**
     * Removes all parts of the same name from the database
     * @param partName the name of the parts that are deleted
     */
    public void deleteParts(String partName) {
        String deleteQuery = "DELETE FROM parts WHERE partName = '" + cleanString(partName) + "';";
        try {
            executeUpdateWithRetry(deleteQuery);
        } catch (SQLException e) {
            stageUtils.errorAlert("Error, could not delete part with name = " + partName);
        }
    }

    /**
     * Gets a specific part from the database
     * @param partID unique id of the part
     * @return a Part
     */
    public Part selectPart(int partID) {
        String query = "SELECT * FROM parts WHERE partID = " + partID + ";";
        Part part = null;
        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            if (resultSet.next()) {
                part = new Part(resultSet.getString("partName"), resultSet.getString("serialNumber"),
                        resultSet.getString("manufacturer"), Double.parseDouble(resultSet.getString("price")),
                        resultSet.getString("vendorID"), resultSet.getString("location"),
                        resultSet.getLong("barcode"), resultSet.getInt("partID"));
            }
        } catch (SQLException e) {
            stageUtils.errorAlert("Error retrieving part with ID = " + partID);
        }
        return part;
    }

    /**
     * Checks if barcode exists
     * @param barcode Barcode to be checked
     * @return True if barcode exists
     */
    public boolean barcodeExists(long barcode) {
        long bc = 0;
        String getAllBarcodes = "SELECT barcode FROM parts WHERE barcode = " + barcode + ";";
        try (ResultSet rs = executeQueryWithRetry(getAllBarcodes)) {
            if (rs.next()) {
                bc = rs.getLong("barcode");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return !(bc == 0);
    }

    public boolean partNameExists(String partName) {
        String name = "";
        String query = "SELECT parts.partName from parts WHERE partName = '" + cleanString(partName) + "';";
        try (ResultSet rs = executeQueryWithRetry(query)) {
            if (rs.next()) {
                name = rs.getString("partName");
            }
        } catch (SQLException e) {
            stageUtils.errorAlert("SQLException: Can't connect to the database when checking if part name exists.");
        }
        return !name.isEmpty();
    }

    public int getCheckoutIDFromBarcodeAndRFID(long rfid, long barcode) {
        int checkoutID = 0;
        String query = "select checkoutID from checkout where studentID = " + rfid + " and barcode = " + barcode +
                "and checkinAt IS NULL limit 1";
        try (ResultSet rs = executeQueryWithRetry(query)) {
            if(rs.next()){
                checkoutID = rs.getInt("checkoutID");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return checkoutID;
    }

    /**
     * Returns the student ID associated with
     * @param email the email
     * @return the ID associated with student's email, 0 if the student isn't in the db
     * (might be 0 for imported students)
     */
    public long getStudentIDFromEmail(String email) {
        long sID = 0;
        String query = "SELECT studentID FROM students WHERE email = " + email + ";";
        try (ResultSet rs = executeQueryWithRetry(query)) {
            if (rs.next()) {
                sID = rs.getLong("studentID");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return sID;
    }

    /**
     * Inserts new checkout entity into the database, and changes the associated part.isCheckedOut to 1
     */
    public boolean checkOutPart(long barcode, long rfid, String course, String prof, String dueDate) {
        int partID = getPartIDFromBarcode(barcode, false);
        if (partID == 0) {
            stageUtils.errorAlert("Unable to find a valid partID for barcode");
            return false;
        }
        String addToCheckouts = "INSERT INTO checkout (partID, studentID, barcode, checkoutAt, dueAt, " +
                "prof, course) VALUES(" + partID + "," + rfid + "," + barcode + ",'" +
                timeUtils.getCurrentDateTime().toString() + "','";
        if (dueDate != null) {
            addToCheckouts += cleanString(dueDate) + "','" + cleanString(prof) + "','" + cleanString(course) + "');";
        } else {
            addToCheckouts += timeUtils.setDueDate() + "','','');";
        }
        try {
            executeUpdateWithRetry(addToCheckouts);
            setPartStatus(partID, false); //This will set the partID found above to a checked out status
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        } catch (NullPointerException e){
            stageUtils.errorAlert("Part with barcode of " + barcode + " is already checked out");
            return false;
        }
        return true;
    }

    /**
     * Updates the checkout entity checkinAt to current time, and changes the associated part.isCheckedOut to 0
     */
    public boolean checkInPart(long barcode, long rfid) {
        int partID = getPartIDFromBarcode(barcode, true);
        String setDate = "UPDATE checkout SET checkinAt = '" + timeUtils.getCurrentDateTime().toString()
                + "' WHERE checkoutID = " + getCheckoutIDFromBarcodeAndRFID(rfid, barcode) + ";";
        try {
            executeUpdateWithRetry(setDate);
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        setPartStatus(partID, true); //Sets part to checked in
        return true;
    }

    /**
     * Sets part isCheckedOut status to 1 to signify the part is checked out or 0 to show that it is in
     * @param partID Part ID of part as an int
     * @param isCheckIn boolean true if status is being set to checked in, false if checked out
     */
    private void setPartStatus(int partID, boolean isCheckIn){
        String status;
        if (isCheckIn) {
            status = "UPDATE parts SET isCheckedOut = 0 WHERE partID = " + partID + ";";
        } else {
            status = "UPDATE parts SET isCheckedOut = 1 WHERE partID = " + partID + ";";
        }
        try {
            executeUpdateWithRetry(status);
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    /**
     * This method takes a barcode as parameter and returns the corresponding partID to be added to check out table.
     * @param barcode barcode of part
     * @param isCheckIn whether the desired part is checked in
     * @return Part ID as int
     */
    private int getPartIDFromBarcode(long barcode, boolean isCheckIn){
        int partID = 0;
        String status = "SELECT partID FROM parts WHERE barcode = " + barcode + " AND isCheckedOut = " +
                (isCheckIn? 1 : 0) + " LIMIT 1;";
        try (ResultSet rs = executeQueryWithRetry(status)) {
            if(rs.next()) {
                partID = rs.getInt("partID");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return partID;
    }

    /**
     * Gets info about the student who most recently checked a part in or out
     * @param partID the part ID of the part being checked
     * @return a Student object that represents the student who last checked the part in or out
     */
    public Student getStudentToLastCheckout(int partID) {
        String query = "SELECT c.*, s.* FROM checkout c\n" +
                "INNER JOIN students s \n" +
                "ON c.studentID = s.studentID \n" +
                "INNER JOIN (SELECT MAX(checkoutID) AS max_checkoutID FROM checkout WHERE partID = " + partID +
                ") max_c on c.checkoutID = max_c.max_checkoutID;";
        long studentID = -1;
        Student student = null;
        String studentName = "";
        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            if (resultSet.next()) {
                studentID = resultSet.getLong("studentID");
                studentName = resultSet.getString("studentName");
            }
            student = selectStudent(studentID, null);
            if (student == null) {
                return null;
            }
            if (student.getName().isEmpty()) {
                student.setName(studentName);
            }
        } catch (SQLException e) {
            stageUtils.errorAlert("Error retrieving last student to checkout part");
        }
        return student;
    }

    /**
     * Gets info about the most recent checkin/out transaction for the part with the matching part ID
     * @param partID the part ID of the part being checked
     * @return a CheckoutObject object that represents info about the part's last checkout
     */
    public Checkout getLastCheckoutOf(int partID) {
        String query = "SELECT c.* FROM checkout c\n" +
                "INNER JOIN (SELECT MAX(checkoutID) AS max_checkoutID FROM checkout WHERE partID = " + partID +
                ") max_c on c.checkoutID = max_c.max_checkoutID;";
        Checkout checkoutObject = null;
        long studentID = 0;
        long barcode = 0;
        String dueAt = null;
        String professor = null;
        String course = null;
        Date checkoutAt  = null;
        Date checkinAt = null;

        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            if (resultSet.next()) {
                studentID = resultSet.getLong("studentID");
                barcode = resultSet.getLong("barcode");
                checkoutAt = TimeUtils.parseTimestamp(resultSet.getTimestamp("checkoutAt"));
                checkinAt = TimeUtils.parseTimestamp(resultSet.getTimestamp("checkinAt"));
                dueAt = resultSet.getString("dueAt");
                professor = resultSet.getString("prof");
                course = resultSet.getString("course");
            }
            checkoutObject = new Checkout(studentID, barcode, checkoutAt, checkinAt,
                    timeUtils.convertStringtoDate(dueAt), professor, course);
        } catch (SQLException e) {
            stageUtils.errorAlert("Error while retrieving last checkout of part");
        }
        return checkoutObject;
    }

    /**
     * This method clears the checkout data that is over 2 years old
     */
    public void clearOldHistory() {
        String query =
                "DELETE checkout FROM checkout WHERE checkout.checkinAt < '" + TimeUtils.getTwoYearsAgo() + "';";
        try {
            executeUpdateWithRetry(query);
        } catch (SQLException e) {
            stageUtils.errorAlert("Error clearing old history");
        }
    }

    /**
     * Removes students who have no transaction history associated with them. This should only
     * affect students with no parts checked in/out in the past two years (after history is cleared)
     * or students created via the manage students screen, either through importing them or adding a student
     */
    public void clearUnusedStudents() {
        String query =
                "DELETE FROM students WHERE studentID NOT IN (SELECT studentID FROM checkout) OR studentID is null";
        try {
            executeUpdateWithRetry(query);
        } catch (SQLException e) {
            stageUtils.errorAlert("Error clearing unused students");
        }
    }

    /**
     * Checks whether the part with the given part ID is currently checked out
     * It is checking this via checkout table, not parts table
     * @param partID the part ID of the part being checked
     * @return true if the matching part is checked out; false otherwise
     */
    public boolean getIsCheckedOut(String partID) {
        String query = "SELECT COUNT(*) FROM checkout WHERE checkinAt is NULL AND partID = " + cleanString(partID)
                + ";";
        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            resultSet.next();
            int result = resultSet.getInt(1);
            if (result > 0) {
                return true;
            }
        } catch (SQLException e) {
            stageUtils.errorAlert("Could not determine if specific part is checked out");
        }
        return false;
    }

    public ObservableList<Part> getAllParts() {
        String query = "select partName, serialNumber, barcode, location, partID, price from parts;";
        ObservableList<Part> data = FXCollections.observableArrayList();
        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            while(resultSet.next()){
                String partName = resultSet.getString("partName");
                long barcode = resultSet.getLong("barcode");
                String serialNumber = resultSet.getString("serialNumber");
                String location = resultSet.getString("location");
                int partID = resultSet.getInt("parts.partID");
                int price = resultSet.getInt("price");
                Part part = new Part(partName, serialNumber, location, barcode, partID, price);
                data.add(part);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database", e);
        }
        return data;
    }

    public ObservableList<Checkout> getAllCheckoutHistory() {
        ObservableList<Checkout> data = FXCollections.observableArrayList();
        String query = "WITH ActionHistory AS ( " +
                "SELECT studentName, email, partName, parts.barcode, checkout.checkinAt, checkout.checkoutAt, " +
                "1 as val FROM parts " +
                "INNER JOIN checkout ON parts.partID = checkout.partID " +
                "INNER JOIN students ON checkout.studentID = students.studentID " +
                "UNION ALL " +
                "SELECT studentName, email, partName, parts.barcode, checkout.checkinAt, checkout.checkoutAt, " +
                "2 as val FROM parts " +
                "INNER JOIN checkout ON parts.partID = checkout.partID " +
                "INNER JOIN students ON checkout.studentID = students.studentID " +
                "WHERE checkout.checkinAt IS NOT NULL " +
                ")" +
                "SELECT studentName, email, partName, barcode, " +
                "CASE WHEN val = 1 THEN 'Checked Out' ELSE 'Checked In' END AS 'Action', " +
                "CASE WHEN val = 1 THEN checkoutAt ELSE checkinAt END AS 'Date' " +
                "FROM ActionHistory " +
                "ORDER BY Date DESC;";
        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            while (resultSet.next()) {
                String studentName = resultSet.getString("studentName");
                String studentEmail = resultSet.getString("email");
                String partName = resultSet.getString("partName");
                long barcode = resultSet.getLong("barcode");
                String action = resultSet.getString("Action");
                Date date = TimeUtils.parseTimestamp(resultSet.getTimestamp("Date"));
                Checkout historyItems = new Checkout(studentName, studentEmail, partName, barcode, action, date);
                data.add(historyItems);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database", e);
        }
        return data;
    }

    public ObservableList<Checkout> getAllCurrentlyCheckedOut() {
        ObservableList<Checkout> data = FXCollections.observableArrayList();
        String query = "SELECT students.studentName, students.email, " +
                "students.studentID, parts.partName, parts.barcode, parts.serialNumber, checkout.checkoutAt, " +
                "checkout.dueAt, checkout.checkoutID, parts.partID, parts.price\n" +
                "FROM checkout\n" +
                "INNER JOIN parts on checkout.partID = parts.partID\n" +
                "INNER JOIN students on checkout.studentID = students.studentID\n" +
                "WHERE checkout.checkinAt IS NULL";
        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            while(resultSet.next()){
                int checkoutID = resultSet.getInt("checkoutID");
                String studentName = resultSet.getString("studentName");
                String studentEmail = resultSet.getString("email");
                long studentID = resultSet.getLong("studentID");
                String partName = resultSet.getString("partName");
                long barcode = resultSet.getLong("barcode");
                String serialNumber = resultSet.getString("serialNumber");
                Date checkedOutAt = TimeUtils.parseTimestamp(resultSet.getTimestamp("checkoutAt"));
                String dueDate = resultSet.getString("dueAt");
                int partID = resultSet.getInt("parts.partID");
                String fee = resultSet.getString("price");
                Checkout checkedOut = new Checkout(checkoutID, studentName, studentEmail, studentID, partName, barcode,
                        serialNumber, partID, checkedOutAt,
                        timeUtils.convertStringtoDate(dueDate), fee);
                data.add(checkedOut);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database", e);
        }
        return data;
    }

    /**
     * Gets a list of serial numbers used by a part with a given name, except for the part with the given part ID
     * @param partName the name of parts being checked
     * @param partID   the part ID of the part exempt from the search
     * @return the list of serial numbers
     */
    public ArrayList<String> getOtherSerialNumbersForPartName(String partName, String partID) {
        String query = "SELECT serialNumber FROM parts WHERE partName = '" + cleanString(partName) +
                "' AND partID != " + cleanString(partID) + ";";
        return collectFromOneCol(query, "serialNumber");
    }

    /**
     * Gets a list of all serial numbers used by parts with the given name
     * @param partName name of the part being checked
     * @return the list of serial numbers
     */
    public ArrayList<String> getAllSerialNumbersForPartName(String partName) {
        String query = "SELECT serialNumber FROM parts WHERE partName = '" + cleanString(partName) + "';";
        return collectFromOneCol(query, "serialNumber");
    }

    /**
     * Gets a list of all barcodes used by parts with the given name
     * @param partName name of the part being checked
     * @return the list of barcodes
     */
    public ArrayList<String> getAllBarcodesForPartName(String partName) {
        String query = "SELECT barcode FROM parts WHERE partName = '" + cleanString(partName) + "';";
        return collectFromOneCol(query, "barcode");
    }

    /**
     * Gets a list of all part IDs used by parts with the given name
     * @param partName name of the part being checked
     * @return the list of part IDs
     */
    public ArrayList<String> getAllPartIDsForPartName(String partName) {
        String query = "SELECT partID FROM parts WHERE partName = '" + cleanString(partName) + "';";
        return collectFromOneCol(query, "partID");
    }

    /**
     * Helper method to collect many of one column from a specific table
     */
    private ArrayList<String> collectFromOneCol(String query, String column) {
        ArrayList<String> list = new ArrayList<>();
        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            while (resultSet.next()) {
                list.add(resultSet.getString(column));
            }
        } catch (SQLException e) {
            stageUtils.errorAlert("Could not collect " + column + "(s) from database");
        }
        return list;
    }

    /**
     * Checks whether the parts with the given part name have unique barcodes
     * @param partName the name of the parts being checked
     * @return true if the part has unique barcodes; false otherwise
     */
    public boolean hasUniqueBarcodes(String partName) {
        if (countPartsOfType(partName) > 1) {
            ArrayList<String> barcodes = getAllBarcodesForPartName(partName);
            for (String barcode : barcodes) {
                if (Collections.frequency(barcodes, barcode) > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Gets the number of parts which have the given part name
     * @param partName the part name being checked
     * @return the number of parts
     */
    public int countPartsOfType(String partName) {
        String query = "SELECT COUNT(*) FROM parts WHERE partName = '" + cleanString(partName) + "';";
        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            stageUtils.errorAlert("Unable to count number of parts with the same name");
        }
        return -1;
    }

    /**
     * This method checks to see whether the database contains a part with a passed in part name
     * @param partName the name of the part being checked
     * @return true if the database contains a part with part name that equals partName; false otherwise
     */
    public boolean hasPartName(String partName) {
        String query = "SELECT * from parts where partName = '" + cleanString(partName) + "';";
        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            return resultSet.next();
        } catch (SQLException e) {
            stageUtils.errorAlert("Unable to determine whether part name exists in database");
        }
        return false;
    }

    /**
     * This method queries the database and finds the max part ID in the database
     * @return The max part ID already in the database
     */
    public int getMaxPartID(){
        int partID = 0;
        String query = "SELECT partID FROM parts ORDER BY partID DESC LIMIT 1";
        try (ResultSet rs = executeQueryWithRetry(query)) {
            while(rs.next()){
                partID = rs.getInt("partID");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return partID;
    }

    /**
     * Add part to database
     */
    public void addPart(Part p){
        String query = "INSERT INTO parts(partName, serialnumber, manufacturer, price, vendorID," +
                " location, barcode, isCheckedOut, createdAt, createdBy)"+
                "VALUES('" + cleanString(p.getPartName()) + "','" + p.getSerialNumber() + "','" +
                cleanString(p.getManufacturer()) + "'," + p.getPrice() + "," + getVendorIDFromVendor(p.getVendor()) +
                ",'" + cleanString(p.getLocation()) + "'," + p.getBarcode() + "," + 0 + // 0 denotes it's checked in
                ",'" + timeUtils.getCurrentDate() + "','" + cleanString(this.worker.getName()) + "');";
        try {
            executeUpdateWithRetry(query);
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    /**
     * This method edits an item in the database
     * @param part The part to be edited
     */
    public void editPart(Part part){
        String editQuery = "UPDATE parts SET serialNumber = '" + part.getSerialNumber() + "', barcode = " +
                part.getBarcode() + ", price = " + part.getPrice() + ", location = '" + part.getLocation() + "', " +
                "updatedAt = '" + timeUtils.getCurrentDate() + "' WHERE partID = " + part.getPartID() + ";";
        try {
            executeUpdateWithRetry(editQuery);
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    public void editAllOfPartName(String originalPartName, Part updatedPart) {
        if (!originalPartName.equals(updatedPart.getPartName())) {
            String editAllQuery = "UPDATE parts SET partName = '" + updatedPart.getPartName() + "', price = " +
                    updatedPart.getPrice() + ", location = '" + updatedPart.getLocation() + "', manufacturer = '" +
                    updatedPart.getManufacturer() + "', vendorID = " + getVendorIDFromVendor(updatedPart.getVendor()) +
                    ", updatedAt = '" + timeUtils.getCurrentDate() + "' WHERE partName = '" + originalPartName + "';";
            try {
                executeUpdateWithRetry(editAllQuery);
            } catch (SQLException e) {
                throw new IllegalStateException("Cannot connect to the database", e);
            }
        }
    }

    public void editAllOfPartNameCommonBarcode(String originalPartName, Part updatedPart) {
        ArrayList<String> partIDsForPart = database.getAllPartIDsForPartName(originalPartName);
        try {
            for (String partID : partIDsForPart) {
                String editAllCommonBarcodeQuery = "UPDATE parts SET partName = '" + updatedPart.getPartName() +
                        "', price = " + updatedPart.getPrice() + ", location = '" + updatedPart.getLocation() +
                        "', barcode = " + updatedPart.getBarcode() + ", manufacturer = '" + updatedPart.getManufacturer()
                        + "', vendorID = " + getVendorFromID(updatedPart.getVendor())+ ", updatedAt = '" +
                        timeUtils.getCurrentDate() + "' WHERE partID = " + partID + ";";
                executeUpdateWithRetry(editAllCommonBarcodeQuery);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    /**
     * Gets the list of students from the database
     * @return observable list of students
     */
    public ObservableList<Student> getStudents() {
        ObservableList<Student> studentsList = FXCollections.observableArrayList();
        String query = "SELECT * FROM students";
        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            String name;
            long id;
            String email;
            while (resultSet.next()) {
                name = resultSet.getString("studentName");
                id = resultSet.getLong("studentID");
                email = resultSet.getString("email");
                String[] names = name.split(" ", 2);
                studentsList.add(new Student(names[0], names[1], id, email));
            }
        } catch (SQLException e) {
            stageUtils.errorAlert("Could not retrieve the list of students");
        }
        return studentsList;
    }

    /**
     * @return true if a student has RFID, false otherwise
     */
    public boolean studentRFIDExists(long rfid) {
        long studentRFID = 0;
        String query = "SELECT studentID FROM students where studentID = " + rfid + ";";
        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            if (resultSet.next()) {
                studentRFID = resultSet.getLong("studentID");
            }
        } catch (SQLException e) {
            stageUtils.errorAlert("Could not retrieve the list of rfids");
        }
        return studentRFID != 0;
    }

    /**
     * This method returns a list of all student emails in the system
     * @return the list of all student emails
     */
    public ObservableList<String> getStudentEmails() {
        String query = "SELECT email FROM students;";
        return FXCollections.observableArrayList(collectFromOneCol(query, "email"));
    }

    /**
     * This method returns a list of all Unique part names in the database
     * @return the list of all unique part names
     */
    public ObservableList<String> getAllPartNames() {
        String query = "SELECT DISTINCT partName FROM parts;";
        return FXCollections.observableArrayList(collectFromOneCol(query, "partName"));
    }

    /**
     * Gets the list of workers from the database
     * @return observable list of workers
     */
    public ObservableList<Worker> getWorkers() {
        ObservableList<Worker> workerList = FXCollections.observableArrayList();
        String query = "SELECT * FROM workers";
        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            while (resultSet.next()) {
                workerList.add(buildWorker(resultSet));
            }
        } catch (SQLException e) {
            stageUtils.errorAlert("Could not retrieve the list of workers");
        }
        return workerList;
    }

    /**
     * This method gets a Worker object for the worker that has the passed in email
     * @param email the email of the worker to be retrieved
     * @return the Worker with the matching email
     */
    public Worker getWorker(String email) {
        Worker worker = null;
        String query = "SELECT * FROM workers where email = '" + cleanString(email) + "';";
        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            resultSet.next();
            worker = buildWorker(resultSet);
        } catch (SQLException e) {
            stageUtils.errorAlert("Could not retrieve the list of workers");
        }
        return worker;
    }

    public Worker getWorker(long rfid) {
        Worker worker = null;
        String query = "Select * from workers where ID = " + rfid + ";";
        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            if (resultSet.next()) {
                worker = buildWorker(resultSet);
            }
        } catch (SQLException e) {
            stageUtils.errorAlert("Could not retrieve the list of workers");
        }
        return worker;
    }

    private Worker buildWorker(ResultSet resultSet) {
        Worker worker = null;
        try {
            String name = resultSet.getString("workerName");
            String password = resultSet.getString("pass");
            int workerID = resultSet.getInt("workerID");
            long rfid = resultSet.getLong("ID");
            String email = resultSet.getString("email");
            int pin = resultSet.getInt("pin");
            boolean isAdmin = resultSet.getByte("isAdmin") == 1;
            boolean parts = resultSet.getByte("editParts") == 1;
            boolean workers = resultSet.getByte("workers") == 1;
            boolean students = resultSet.getByte("removeParts") == 1;
            if (!email.isEmpty() && rfid != 0) {
                worker = new Worker(name, workerID, email, password, pin, rfid, isAdmin, parts, workers, students);
            }
        } catch (SQLException e) {
            stageUtils.errorAlert("Could not retrieve the worker from database");
        }
        return worker;
    }

    /**
     * This method checks whether pin matches one of the administrators' pins
     * @param pin the inputted pin that is being checked
     * @return true if pin is one of the administrators' pins; false otherwise
     */
    public boolean isValidPin(int pin) {
        int adminPin = 0;
        String query = "SELECT pin FROM workers WHERE pin = " + pin + ";";
        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            if (resultSet.next()) {
                adminPin = resultSet.getInt("pin");
            }
        } catch (SQLException e) {
            stageUtils.errorAlert("Could not retrieve the list of admin pins");
        }
        return adminPin == pin && pin != 0;
    }

    public int getNumAdmins() {
        String query = "SELECT COUNT(*) FROM workers WHERE isAdmin = 1;";
        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            stageUtils.errorAlert("Could not count the number of admins in database");
        }
        return -1;
    }

    /**
     * Gets a student from the database based on their RFID or email
     * @param rfid RFID to search for, -1 if no RFID being searched
     * @param studentEmail the email being searched, null if no email being searched
     * @return a student matching inputs if one exists in the db, null otherwise
     */
    public Student selectStudent(long rfid, String studentEmail) {
        String query;
        String coList = "select students.studentID, students.studentName, students.email, parts.partName, " +
                "checkout.checkoutAt, checkout.dueAt, checkout.checkoutID, checkout.studentID, parts.barcode, " +
                "parts.serialNumber, parts.price, parts.partID from students " +
                "left join checkout on students.studentID = checkout.studentID " +
                "left join parts on checkout.partID = parts.partID";
        if (rfid == -1 && studentEmail != null) {
            studentEmail = cleanString(studentEmail);
            query = "select * from students where email = '" + studentEmail + "';";
            coList += " where students.email = '" + studentEmail +
                    "' AND checkout.checkinAt is null;";
        } else if (rfid != -1) {
            query = "select * from students where studentID = " + rfid + ";";
            coList += " where students.studentID = " + rfid +
                    " AND checkout.checkinAt is null;";
        } else {
            return null;  // if there's no ID or email
        }
        Student student = null;
        String name = "", email = "";
        Date date = null;
        int uniqueID = 0;
        long id = 0;
        ObservableList<Checkout> checkedOutItems = FXCollections.observableArrayList();
        ObservableList<OverdueItem> overdueItems = FXCollections.observableArrayList();
        try (ResultSet resultSet = executeQueryWithRetry(query)) {  // gets student information
            while (resultSet.next()) {
                name = resultSet.getString("studentName");
                email = resultSet.getString("email");
                id = resultSet.getLong("studentID");
                uniqueID = resultSet.getInt("uniqueID");
            }
            // gets all currently checked out items associated with student
            try (ResultSet rs = executeQueryWithRetry(coList)) {
                while (rs.next()) {
                    if (rs.getInt("checkout.checkoutID") != 0) {
                        checkedOutItems.add(new Checkout(
                                rs.getInt("checkout.checkoutID"),
                                rs.getString("students.studentName"),
                                rs.getString("students.email"),
                                rs.getLong("students.studentID"),
                                rs.getString("parts.partName"),
                                rs.getLong("parts.barcode"),
                                rs.getString("parts.serialNumber"),
                                rs.getInt("parts.partID"),
                                TimeUtils.parseTimestamp(rs.getTimestamp("checkout.checkoutAt")),
                                timeUtils.convertStringtoDate(rs.getString("checkout.dueAt")),
                                rs.getString("parts.price")));

                        String dueAt = rs.getString("checkout.dueAt");
                        if (isOverdue(dueAt)) {
                            overdueItems.add(new OverdueItem(
                                    rs.getLong("students.studentID"),
                                    rs.getString("students.studentName"),
                                    rs.getString("students.email"),
                                    rs.getString("parts.partName"),
                                    rs.getLong("parts.barcode"),
                                    timeUtils.convertStringtoDate(dueAt),
                                    rs.getString("checkout.checkoutID")));
                        }
                    }
                }
            }
            if (!checkedOutItems.isEmpty()) {
                date = checkedOutItems.get(0).getCheckedOutDate().get();
            }
            // date null if no checkouts
            for (int i = 0; i < checkedOutItems.size() && date != null; i++) {
                Date d1 = checkedOutItems.get(i).getCheckedOutDate().get();
                if (d1.after(date)) {
                    date = checkedOutItems.get(i).getCheckedOutDate().get();
                }
            }
            if (date != null) {
                student = new Student(name, uniqueID, id, email, date.toString(), checkedOutItems, overdueItems);
            } else {
                student = new Student(name, uniqueID, id, email, "", checkedOutItems, overdueItems);
            }
        } catch (SQLException e) {
            stageUtils.errorAlert("Could not retrieve student from database");
        }
        return student;
    }

    public Student selectStudentWithoutLists(String email) {
        String query = "Select studentName, email, studentID from students where email = '" + cleanString(email) + "';";
        String studentEmail = "";
        String name = "";
        long id = 0;
        try (ResultSet rs = executeQueryWithRetry(query)) {
            while (rs.next()) {
                studentEmail = rs.getString("email");
                name = rs.getString("studentName");
                id = rs.getLong("studentID");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot retrieve student information from the database", e);
        }
        return new Student(name, id, studentEmail);
    }

    /**
     * @param email the email that is being checked against all students in the database
     * @return The student's name if there is a match, empty String otherwise
     */
    public String getStudentNameFromEmail(String email) {
        return getStudentName(email, false);
    }

    /**
     * @param studentID the RFID that is being checked against all students in the database
     * @return The student's name if there is a match, empty String otherwise
     */
    public String getStudentNameFromID(String studentID) {
        return getStudentName(studentID, true);
    }

    private String getStudentName(String input, boolean isRFID) {
        String sName = "";
        String query;
        if (isRFID) {
            query = "select studentName from students where studentID = " + input + ";";
        } else {
            query = "select studentName from students where email = '" + cleanString(input) + "';";
        }
        try (ResultSet rs = executeQueryWithRetry(query)) {
            if (rs.next()) {
                sName = rs.getString("studentName");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot get student name from the database", e);
        }
        return sName;
    }

    /**
     * Adds a new student to the database
     * @param s student to be added
     */
    public void addStudent(Student s) {
        String query = "insert into students (studentID, email, studentName, createdAt, createdBy) values (" +
                s.getRFID() + ", '" + cleanString(s.getEmail()) + "', '" + cleanString(s.getName()) +
                "', date('" + TimeUtils.getToday() + "'), '" + cleanString(this.worker.getName()) + "');";
        try {
            executeUpdateWithRetry(query);
        } catch (SQLException e) {
            stageUtils.errorAlert("Could not add student");
        }
    }

    /**
     * Adds a student to the database without the student's rfid. This is used for
     * importing a bunch of students when the rfid is unknown.
     * @param s the student to be added
     */
    public boolean importStudent(Student s) {
        String query = "insert into students (email, studentName, createdAt, createdBy) values ('" +
                cleanString(s.getEmail()) + "', '" + cleanString(s.getName()) +
                "', date('" + TimeUtils.getToday() + "'), '" + cleanString(this.worker.getName()) + "');";
        try {
            executeUpdateWithRetry(query);
        } catch (Exception e) {
            stageUtils.errorAlert("Could not add student");
            return false;
        }
        return true;
    }

    public void updateStudent(Student s, long oldRFID) {
        if(studentHasCheckedOutItems(oldRFID)){
            updateCheckedOutPartsRFID(s, oldRFID);
        }
        String query = "update students set students.studentID = " + s.getRFID() + ", students.studentName = '" +
                cleanString(s.getName()) + "', students.email = '" + cleanString(s.getEmail()) +
                "', students.updatedAt = date('" + TimeUtils.getToday() + "'), students.updatedBy = '" +
                cleanString(this.worker.getName()) + "' where students.uniqueID = " + s.getUniqueID() + ";";
        try {
            executeUpdateWithRetry(query);
        } catch (SQLException e) {
            stageUtils.errorAlert("Could not update student");
        }
    }

    /**
     * Helper method for updateStudent that alters the RFID associated with checkouts if it is being changed
     * @param s the student being updated, with all the information changed
     * @param oldRFID the RFID the checkouts are associated with
     */
    public void updateCheckedOutPartsRFID(Student s, long oldRFID){
        if (s.getRFID() != oldRFID) {  // won't update if no change is made with the RFID
            String query = "UPDATE checkout SET checkout.studentID = " + s.getRFID() +
                    " WHERE checkout.studentID = " + oldRFID + ";";
            try {
                executeUpdateWithRetry(query);
            } catch (SQLException e) {
                stageUtils.errorAlert("Issue updating checked out parts for student that was " +
                        "being updated, SQL exception");
            }
        }
    }

    /**
     * Confirms whether the RFID has associated checkout data, either currently checked out or history
     */
    public boolean studentHasCheckedOutItems(long RFID) {
        String query = "SELECT COUNT(*) FROM checkout WHERE checkout.studentID = " + RFID + ";";
        return checkCheckedOutFromRFID(RFID, query);
    }

    /**
     * Confirms whether the student CURRENTLY has any items checked out
     */
    public boolean studentHasCurrentCheckedOutItems(long RFID) {
        String query = "SELECT COUNT(*) FROM checkout WHERE checkout.studentID = " + RFID + " AND checkinAt is NULL;";
        return checkCheckedOutFromRFID(RFID, query);
    }

    private boolean checkCheckedOutFromRFID(long RFID, String query) {
        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            resultSet.next();
            if(RFID != 0 && resultSet.getInt(1) > 0){
                return true;
            }
        } catch (SQLException e) {
            stageUtils.errorAlert("Issue counting parts checked out by specific student");
        }
        return false;
    }

    /**
     * @return the number of parts with the same barcode that the selected student does not have returned
     */
    public int amountOutByStudent(long barcode, Student s) {
        String query = "SELECT COUNT(*) FROM checkout WHERE checkinAt is NULL AND barcode = " + barcode +
                " AND studentID = " + s.getRFID() + ";";
        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            stageUtils.errorAlert("Issue counting parts checked out by specific student");
        }
        return 0;
    }

    /**
     * @return the number of parts associated with one barcode that is not currently checked out
     */
    public int getNumPartsAvailableByBarcode(long barcode) {
        String query = "SELECT partID FROM parts WHERE barcode = " + barcode + " AND isCheckedOut = 0;";
        int result = 0;
        try (ResultSet resultSet = executeQueryWithRetry(query)) {
            while(resultSet.next()) {
                boolean checkedOut = getIsCheckedOut(resultSet.getString(1));
                if (!checkedOut) {
                    result++;
                }
            }
            return result;
        } catch (SQLException e) {
            stageUtils.errorAlert("Issue counting parts checked out by barcode");
        }
        return 0;
    }

    /**
     * Deletes a student from the database
     * @param email students email
     */
    public void deleteStudent(String email) {
        Student s = selectStudent(-1, email);
        if (studentHasCurrentCheckedOutItems(s.getRFID())) {
            stageUtils.errorAlert("Student could not be deleted because they have parts checked out");
            return;
        }
        String query = "delete from students where email = '" + cleanString(email) + "';";
        try {
            executeUpdateWithRetry(query);
        } catch (SQLException e) {
            stageUtils.errorAlert("Could not delete student.");
        }
    }

    /**
     * Adds a new worker to the database
     * @param w worker to be added
     */
    public void addWorker(Worker w) {
        int bit = w.isAdmin() ? 1 : 0;
        String query = "insert into workers (email, workerName, pin, pass, ID, isAdmin, createdAt, createdBy) " +
                "values ('" + cleanString(w.getEmail()) + "', '" + cleanString(w.getName()) + "', " + w.getPin() +
                ", '" + cleanString(w.getPass()) + "', " + w.getWorkerRFID() + "," + bit + ", date('" +
                TimeUtils.getToday() + "'), '" + cleanString(this.worker.getName()) + "');";
        try {
            executeUpdateWithRetry(query);
        } catch (SQLException e) {
            stageUtils.errorAlert("Could not add worker");
        }
    }

    /**
     * Deletes a worker from the database
     * @param name workers name
     */
    public void deleteWorker(String name) {
        String query = "delete from workers where workers.workerName = '" + cleanString(name) + "';";
        try {
            executeUpdateWithRetry(query);
        } catch (SQLException e) {
            stageUtils.errorAlert("Could not delete worker.");
        }
    }

    public void updateWorker(Worker w) {
        int admin = w.isAdmin() ? 1 : 0;
        int edit = w.canEditParts() ? 1 : 0;
        int remove = w.canRemoveParts() ? 1 : 0;
        int work = w.canEditWorkers() ? 1 : 0;
        String query = "update workers set workers.workerName = '" + cleanString(w.getName()) + "', workers.pin = " +
                w.getPin() + ", workers.pass = '" + cleanString(w.getPass()) + "', workers.ID = " + w.getWorkerRFID()
                + ", workers.isAdmin = " + admin + "," + " workers.email = '" + cleanString(w.getEmail()) + "', " +
                "workers.editParts = " + edit + ", workers.workers = " + work + ", workers.removeParts = " + remove +
                ", workers.updatedAt = date('" + TimeUtils.getToday() + "'), workers.updatedBy = '" +
                cleanString(this.worker.getName()) + "' where workers.workerID = " + w.getWorkerID() + ";";
        try {
            executeUpdateWithRetry(query);
        } catch (SQLException e) {
            stageUtils.errorAlert("Could not update worker");
        }
    }

    /**
     * This method queries the database to get the vendor corresponding to a particular vendorID.
     * @param vendorID the vendorID of the vendor to be returned
     * @return the vendor corresponding to the vendorID
     */
    public String getVendorFromID(String vendorID){
        String result = null;
        String getVendorFromIDQuery = "SELECT vendor FROM vendors WHERE vendorID = " + vendorID + ";";
        try (ResultSet resultSet = executeQueryWithRetry(getVendorFromIDQuery)) {
            if (resultSet.next()) {
                result = resultSet.getString(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return result;
    }

    /**
     * This method queries the database to get the vendorID corresponding to a particular vendor.
     * @param vendor the vendorID of the vendor to be returned
     * @return the vendorID corresponding to the vendor
     */
    public int getVendorIDFromVendor(String vendor) {
        int result = -1;
        String getVendorIDFromVendorQuery = "SELECT vendorID FROM vendors WHERE vendor = '" + vendor + "';";
        try (ResultSet resultSet = executeQueryWithRetry(getVendorIDFromVendorQuery)) {
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return result;
    }

    /**
     * Creates a new vendor
     * @param vendorName Name of vendor
     */
    public void createNewVendor(String vendorName, String description){
        String createNewVendorQuery = "INSERT INTO vendors(vendorID, vendor, description) values ("
                + getNewVendorID() + ", '" + cleanString(vendorName) + "', '" + cleanString(description) + "')";
        try {
            executeUpdateWithRetry(createNewVendorQuery);
        } catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    /**
     * Gets max vendor ID, adds one to it to generate a new vendor id
     * @return Vendor ID
     */
    private int getNewVendorID() {
        int vendorID = 0; // this is probably the least efficient way to do this but :/
        String getVendorIDQuery = "SELECT vendorID\n" +
                "FROM vendors\n" +
                "ORDER BY vendorID DESC\n" +
                "LIMIT 1";
        try (ResultSet rs  = executeQueryWithRetry(getVendorIDQuery)) {
            while(rs.next()){
                vendorID = rs.getInt("vendorID");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        //Gets max id from table, then increments to create new partID
        return vendorID + 1;
    }

    /**
     * This method queries the database to get a list of all vendors
     * @return the list of vendors
     */
    public ArrayList<String> getVendorList() {
        ArrayList<String> vendors;
        String getVendorListQuery = "SELECT vendor FROM vendors;";
        try (ResultSet resultSet = executeQueryWithRetry(getVendorListQuery)) {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            vendors = new ArrayList<>();
            while (resultSet.next()) {
                for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
                    vendors.add(resultSet.getString(i));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return vendors;
    }

    /**
     * Helper method to determine if item is overdue
     * @param date Due date of item
     * @return true if item is overdue; false otherwise
     */
    public boolean isOverdue(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
        if (date != null && !date.isEmpty()) {
            try {
                Date current = dateFormat.parse(timeUtils.getCurrentDateTimeStamp());
                Date dueDate = dateFormat.parse(date);
                return current.after(dueDate);
            } catch (ParseException e) {
                stageUtils.errorAlert("Error, could not parse date");
            }
        }
        return false;
    }

    /**
     * Helper method meant to prevent errors with strings being injected into sql
     */
    private String cleanString(String s) {
        // check that string isn't trying to escape
        if (s.endsWith("\\") && !s.endsWith("\\\\")) {
            s += "\\";
        }
        return s.replace("'", "\\'");
    }

    /**
     * Used to keep track of which worker is currently logged in by passing the worker into
     * each class.
     * @param worker the currently logged in worker
     */
    @Override
    public void initWorker(Worker worker) {
        if (this.worker == null) {
            this.worker = worker;
        }
    }
}