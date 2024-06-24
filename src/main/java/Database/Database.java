package Database;

import CheckItemsController.CheckoutObject;
import Database.ObjectClasses.Part;
import Database.ObjectClasses.Student;
import Database.ObjectClasses.Worker;
import HelperClasses.TimeUtils;
import HelperClasses.StageUtils;
import InventoryController.CheckedOutItems;
import InventoryController.IController;
import InventoryController.StudentCheckIn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class Database implements IController {
    //DB root pass: Userpassword123
    public static final String username = "root";
    public static final String password = "3l3ctr1c_B00gloo";
    static String host = "jdbc:mysql://localhost:3306";
    static final String dbDriver = "com.mysql.jdbc.Driver";
    static final String dbname = "/student_check_in";
    static Connection connection;
    private final TimeUtils timeUtils = new TimeUtils();
    private Worker worker;
    private final StageUtils stageUtils = StageUtils.getInstance();

    /**
     * This creates a connection to the database
     */
    public Database() {
        // Load the JDBC driver.
        // Library (.jar file) must be added to project build path.
        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
        connection = null;
        try {
            connection = DriverManager.getConnection((host + dbname),
                    username, password);
            connection.setClientInfo("autoReconnect", "true");
        } catch (SQLException e) {
            e.printStackTrace();
            StudentCheckIn.logger.error("SQLError: Can't connect to the database! Connection could not be established.");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, could not connect to the database.");
            alert.showAndWait();
        }
    }

    /**
     * Returns the connection to the database created by constructor method
     * @return the database connection
     */
    public Connection getConnection() {
        try{
            if (connection.isClosed()) {
                connection = DriverManager.getConnection((host + dbname), username, password);
            }
        } catch (SQLException ignored) {
            StudentCheckIn.logger.error("SQLError: Can't connect to the database! Problem establishing a new " +
                    "connection after previous was closed.");
        }

        return connection;
    }

    /**
     * This method uses an SQL query to get all items in the database with a due date less than today's date
     * @return a list of overdue items
     */
    public ObservableList<OverdueItem> getOverdue() {
        ObservableList<OverdueItem> data = FXCollections.observableArrayList();

        timeUtils.getCurrentDateTimeStamp();
        String overdue = "SELECT checkout.partID, checkout.studentID, students.studentName, students.email, parts.partName," +
                " parts.serialNumber, parts.barcode, checkout.dueAt, checkout.checkoutID FROM checkout " +
                "LEFT JOIN parts ON checkout.partID = parts.partID " +
                "LEFT JOIN students ON checkout.studentID = students.studentID " +
                "WHERE checkout.checkinAt IS NULL;";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(overdue);
            while (resultSet.next()) {
                String dueAt = resultSet.getString("checkout.dueAt");
                if (isOverdue(dueAt)) {
                    data.add(new OverdueItem(resultSet.getInt("checkout.studentID"), resultSet.getString("students.studentName"),
                            resultSet.getString("students.email"), resultSet.getString("parts.partName"),
                            resultSet.getString("parts.serialNumber"),
                            resultSet.getLong("parts.barcode"), timeUtils.convertStringtoDate(dueAt),
                            resultSet.getString("checkout.checkoutID")));
                }
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQL Error: {}", e.getLocalizedMessage());
            e.printStackTrace();
        }
        return data;
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
                StudentCheckIn.logger.error("Parse Error: {}", e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * This uses an SQL query to delete a specific part from the database
     * @param partID a unique part id
     */
    public void deleteItem(int partID) {
        try {
            String delete = "delete from parts where partID = " + partID + ";";
            Statement statement = connection.createStatement();
            statement.executeUpdate(delete);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Notifications.create().title("Successful!").text("Part with ID = " + partID + " has been successfully deleted").hideAfter(new Duration(5000)).show();
    }

    /**
     * Removes all parts of the same name from the database
     * @param partName the name of the parts that are deleted
     */
    public void deleteParts(String partName) {
        try {
            String deleteQuery = "DELETE FROM parts WHERE partName = '" + cleanString(partName) + "';";
            Statement statement = connection.createStatement();
            statement.executeUpdate(deleteQuery);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                part = new Part(resultSet.getString("partName"), resultSet.getString("serialNumber"),
                        resultSet.getString("manufacturer"), Double.parseDouble(resultSet.getString("price")), resultSet.getString("vendorID"),
                        resultSet.getString("location"), resultSet.getLong("barcode"), resultSet.getInt("partID"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
        final String getAllBarcodes = "SELECT barcode FROM parts WHERE barcode = " + barcode + ";";
        try {
            PreparedStatement statement = connection.prepareStatement(getAllBarcodes);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                bc = rs.getLong("barcode");
            }
            statement.close();
            rs.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database when checking if barcode exists.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return bc == 0;
    }

    public boolean partNameExists(String partName) {
        String name = "";
        String query = "SELECT parts.partName from parts WHERE partName = " + cleanString(partName) + ";";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                name = rs.getString("partName");
            }
            statement.close();
            rs.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database when checking if part name exists.");
            stageUtils.errorAlert("SQLException: Can't connect to the database when checking if part name exists.");
        }
        return !name.isEmpty();
    }

    public int getCheckoutIDFromBarcodeAndRFID(int RFID, long barcode) {
        int checkoutID = 0;
        String query = "select checkoutID from checkout where studentID =? and barcode = ? and checkinAt IS NULL limit 1";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, RFID);
            statement.setLong(2, barcode);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                checkoutID = rs.getInt("checkoutID");
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database when getting checkout from part ID.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return checkoutID;
    }

    /**
     * Returns the student ID associated with
     * @param email the email
     * @return the ID associated with student's email, 0 if the student isn't in the db (might be 0 for imported students)
     */
    public int getStudentIDFromEmail(String email) {
        int sID = 0;
        String query = "SELECT studentID FROM students WHERE email = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, cleanString(email));
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                sID = rs.getInt("studentID");
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("IllegalStateException: Can't connect to the database to look for student.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return sID;
    }

    public boolean hasCheckedOutItemsFromID(int studentID) {
        String query = "SELECT studentID FROM checkout WHERE studentID = ? AND checkinAt IS NULL;";
        List<Integer> checkouts = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, studentID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                checkouts.add(rs.getInt("studentID"));
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("IllegalStateException: Can't connect to the database when looking for student.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return !checkouts.isEmpty();
    }

    public boolean hasCheckedOutItemsFromEmail(String email) {
        return hasCheckedOutItemsFromID(getStudentIDFromEmail(email));
    }

    /**
     * Inserts new checkout entity into the database, and changes the associated part.isCheckedOut to 1
     */
    public boolean checkOutPart(long barcode, int RFID, String course, String prof, String dueDate) {
        try {
            String addToCheckouts = "INSERT INTO checkout (partID, studentID, barcode, checkoutAt, dueAt, prof, course) " +
                    "VALUES(?,?,?,?,?,?,?);";
            int partID = getPartIDFromBarcode(barcode, false);
            if (partID == 0) {
                stageUtils.errorAlert("Unable to find a valid partID for barcode");
                return false;
            }
            PreparedStatement statement = connection.prepareStatement(addToCheckouts);
            statement.setInt(1, partID);
            statement.setInt(2, RFID);
            statement.setLong(3, barcode);
            statement.setString(4, timeUtils.getCurrentDateTimeStamp());
            if (dueDate != null){
                statement.setString(5, cleanString(dueDate));
                statement.setString(6, cleanString(prof));
                statement.setString(7, cleanString(course));
            } else {
                statement.setString(5, timeUtils.setDueDate());
                statement.setString(6, "");
                statement.setString(7, "");
            }
            setPartStatus(partID, false); //This will set the partID found above to a checked out status
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database when adding new checkout item.");
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
    public boolean checkInPart(long barcode, int RFID) {
        int partID = getPartIDFromBarcode(barcode, true);
        try {
            String setDate = "UPDATE checkout SET checkinAt = ? WHERE checkoutID = ?;";
            PreparedStatement statement = connection.prepareStatement(setDate);
            statement.setString(1, timeUtils.getCurrentDateTimeStamp());
            statement.setInt(2, getCheckoutIDFromBarcodeAndRFID(RFID, barcode));
            statement.execute();
            statement.close();
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
            status = "UPDATE parts SET isCheckedOut = 0 WHERE partID = ?;";
        } else {
            status = "UPDATE parts SET isCheckedOut = 1 WHERE partID = ?;";
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(status);
            preparedStatement.setInt(1,partID);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database when setting part status.");
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
        String status = "SELECT partID FROM parts WHERE barcode = ? AND isCheckedOut = ? LIMIT 1;";
        try {
            PreparedStatement statement = connection.prepareStatement(status);
            statement.setLong(1, barcode);
            statement.setBoolean(2, isCheckIn);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                partID = rs.getInt("partID");
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database when getting part ID from barcode.");
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
        int studentID = -1;
        Student student = null;
        String studentName = "";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                studentID = resultSet.getInt("studentID");
                studentName = resultSet.getString("studentName");
            }
            student = selectStudent(studentID, null);
            if (student.getName().isEmpty()) {
                student.setName(studentName);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    /**
     * Gets info about the most recent checkin/out transaction for the part with the matching part ID
     * @param partID the part ID of the part being checked
     * @return a CheckoutObject object that represents info about the part's last checkout
     */
    public CheckoutObject getLastCheckoutOf(int partID) {
        String query = "SELECT c.* FROM checkout c\n" +
                "INNER JOIN (SELECT MAX(checkoutID) AS max_checkoutID FROM checkout WHERE partID = " + partID +
                ") max_c on c.checkoutID = max_c.max_checkoutID;";
        CheckoutObject checkoutObject = null;
        String studentID = "", barcode = "", dueAt = "";
        String checkoutAt = null, checkinAt = null;

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                studentID = "" + resultSet.getInt("studentID");
                barcode = "" + resultSet.getLong("barcode");
                checkoutAt = resultSet.getString("checkoutAt");
                checkinAt = resultSet.getString("checkinAt");
                dueAt = resultSet.getString("dueAt");
            }
            checkoutObject = new CheckoutObject(studentID, barcode, checkoutAt, checkinAt, dueAt);
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return checkoutObject;
    }

    /**
     * todo: Make this smarter, don't want to delete checkout(s) on parts that are out, also delete students that have no transaction history for 4+ years
     * This method clears the checkout data that is over 2 years old
     */
    public void clearOldHistory() {
        String query =
                "DELETE checkout " +
                        "FROM checkout " +
                        "INNER JOIN parts " +
                        "ON checkout.partID = parts.partID " +
                        "AND checkout.checkinAt < ? ";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(query);
            DateFormat target = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
            String formattedDate = target.format(timeUtils.getTwoYearsAgo());
            preparedStatement.setString(1, formattedDate);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Gets a part that has the matching part name
     * @param barcode of the part being checked
     * @return a part with the matching barcode, if it exists
     */
    public Part selectPartByBarcode(long barcode) {
        String query = "select * from parts where barcode = ?;";
        Part part = null;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, barcode);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                part = new Part(resultSet.getString("partName"), resultSet.getString("serialNumber"),
                        resultSet.getString("manufacturer"), Double.parseDouble(resultSet.getString("price")), resultSet.getString("vendorID"),
                        resultSet.getString("location"), resultSet.getLong("barcode"), resultSet.getInt("partID"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return part;
    }

    /**
     * Checks whether the part with the given part ID is currently checked out
     * It is checking this via checkout table, not parts table
     * @param partID the part ID of the part being checked
     * @return true if the matching part is checked out; false otherwise
     */
    public boolean getIsCheckedOut(String partID) {
        String query = "SELECT COUNT(*) FROM checkout WHERE checkinAt is NULL AND partID = " + cleanString(partID) + ";";
        ResultSet resultSet;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            resultSet.next();
            int result = resultSet.getInt(1);
            statement.close();
            resultSet.close();
            if (result > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Gets a list of serial numbers used by a part with a given name, except for the part with the given part ID
     * @param partName the name of parts being checked
     * @param partID   the part ID of the part exempt from the search
     * @return the list of serial numbers
     */
    public ArrayList<String> getOtherSerialNumbersForPartName(String partName, String partID) {
        String query = "SELECT serialNumber FROM parts WHERE partName = '" + cleanString(partName) + "' AND partID != " + cleanString(partID) + ";";
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
     * Gets a list of barcodes used by a part with a given name, except for the part with the given part ID
     *
     * @param partName the name of parts being checked
     * @param partID   the part ID of the part exempt from the search
     * @return the list of barcodes
     */
    public ArrayList<String> getOtherBarcodesForPartName(String partName, String partID) {
        String query = "SELECT barcode FROM parts WHERE partName = '" + cleanString(partName) + "' AND partID != " + cleanString(partID) + ";";
        return collectFromOneCol(query, "barcode");
    }

    /**
     * Gets a list of all barcodes used by parts with the given name
     * @param partName name of the part being checked
     * @return the list of barcodes
     */
    public ArrayList<String> getAllBarcodesForPartName(String partName) {
        String query = "SELECT barcode FROM parts WHERE partName = " + cleanString(partName) + ";";
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
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                list.add(resultSet.getString(column));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Checks whether the parts with the given part name have unique barcodes
     * @param partName the name of the parts being checked
     * @return true if the part has unique barcodes; false otherwise
     */
    public boolean hasUniqueBarcodes(String partName) {
        ArrayList<String> barcodes = getAllBarcodesForPartName(partName);
        if (countPartsOfType(partName) > 1) {
            for (int i = 0; i < barcodes.size(); i++) {
                for (int j = 0; j < barcodes.size(); j++) {
                    if (i != j && barcodes.get(i).equals(barcodes.get(j))) {
                        return false;
                    }
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
        String query = "SELECT COUNT(*) FROM parts WHERE partName = ?;";
        ResultSet resultSet;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, cleanString(partName));
            resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * This method returns a list of all distinct barcodes in the database, except for the ones
     * that belong to a part with the passed in part name. This is used for making sure not to
     * use an already existing barcode when adding parts.
     * @param partName the part name that is an exception
     * @return the list of barcodes
     */
    public ArrayList<String> getUniqueBarcodesBesidesPart(String partName) {
        String query = "SELECT DISTINCT barcode FROM parts WHERE partName != " + cleanString(partName) + ";";
        return collectFromOneCol(query, "barcode");
    }

    /**
     * This method checks to see whether the database contains a part with a passed in part name
     * @param partName the name of the part being checked
     * @return true if the database contains a part with part name that equals partName; false otherwise
     */
    public boolean hasPartName(String partName) {
        String query = "SELECT * from parts where partName = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, cleanString(partName));
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This method queries the database and finds the max part ID in the database
     * @return The max part ID already in the database
     */
    public int getMaxPartID(){
        int partID = 0;
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT partID FROM parts ORDER BY partID DESC LIMIT 1");
            while(rs.next()){
                partID = rs.getInt("partID");
            }
            statement.close();
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
                "VALUES(?,?,?,?,?,?,?,?,?,?)";
        VendorInformation vendorInformation = new VendorInformation();  //todo: remove this
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cleanString(p.getPartName()));
            preparedStatement.setString(2, p.getSerialNumber());
            preparedStatement.setString(3, cleanString(p.getManufacturer()));
            preparedStatement.setDouble(4, p.getPrice());
            preparedStatement.setInt(5, vendorInformation.getVendorIDFromVendor(p.getVendor()));
            preparedStatement.setString(6, cleanString(p.getLocation()));
            preparedStatement.setLong(7, p.getBarcode());
            preparedStatement.setInt(8, 0);
            preparedStatement.setString(9, timeUtils.getCurrentDate());
            preparedStatement.setString(10, cleanString(this.worker.getName()));
            preparedStatement.execute();
            preparedStatement.close();
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
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM students");
            String name;
            int id;
            String email;
            while (resultSet.next()) {
                name = resultSet.getString("studentName");
                id = resultSet.getInt("studentID");
                email = resultSet.getString("email");
                String[] names = name.split(" ");
                studentsList.add(new Student(names[0], names[1], id, email));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            stageUtils.errorAlert("Could not retrieve the list of students");
            e.printStackTrace();
        }
        return studentsList;
    }

    /**
     * @return true if a student has RFID, false otherwise
     */
    public boolean studentRFIDExists(int rfid) {
        int studentRFID = 0;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT studentID FROM students where studentID = " + rfid + ";");
            studentRFID = resultSet.getInt("studentID");
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not retrieve the list of rfids");
            StudentCheckIn.logger.error("Could not retrieve the list of rfids");
            alert.showAndWait();
            e.printStackTrace();
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
     * Gets the list of workers from the database
     * @return observable list of workers
     */
    public ObservableList<Worker> getWorkers() {
        ObservableList<Worker> workerList = FXCollections.observableArrayList();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM workers");
            while (resultSet.next()) {
                workerList.add(buildWorker(resultSet));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not retrieve the list of workers");
            StudentCheckIn.logger.error("Could not retrieve the list of students");
            alert.showAndWait();
            e.printStackTrace();
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
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM workers WHERE email = '" + cleanString(email) + "';");
            resultSet.next();
            worker = buildWorker(resultSet);
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not retrieve the list of workers");
            StudentCheckIn.logger.error("Could not retrieve the list of workers");
            alert.showAndWait();
            e.printStackTrace();
        }
        return worker;
    }

    public Worker getWorker(int RFID) {
        Worker worker = null;
        String query = "Select * from workers where ID = " + RFID + ";";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            worker = buildWorker(resultSet);
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not retrieve the list of workers");
            StudentCheckIn.logger.error("Could not retrieve the list of workers");
            alert.showAndWait();
            e.printStackTrace();
        }
        return worker;
    }

    private Worker buildWorker(ResultSet resultSet) {
        Worker worker = null;
        try {
            String name;
            String password;
            int ID;
            int RFID;
            String email;
            int pin;
            boolean isAdmin;
            boolean parts;
            boolean workers;
            boolean students;
            name = resultSet.getString("workerName");
            ID = resultSet.getInt("workerID");
            RFID = resultSet.getInt("ID");
            email = resultSet.getString("email");
            password = resultSet.getString("pass");
            isAdmin = resultSet.getByte("isAdmin") == 1;
            parts = resultSet.getByte("editParts") == 1;
            workers = resultSet.getByte("workers") == 1;
            students = resultSet.getByte("removeParts") == 1;
            pin = resultSet.getInt("pin");
            if (!email.isEmpty() && RFID != 0) {
                worker = new Worker(name, ID, email, password, pin, RFID, isAdmin, parts, workers, students);
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not retrieve the worker from database");
            StudentCheckIn.logger.error("Could not retrieve workers");
            alert.showAndWait();
            e.printStackTrace();
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
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT pin FROM workers WHERE pin = " + pin + ";");
            adminPin = resultSet.getInt("adminPin");
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not retrieve the list of admin pins");
            StudentCheckIn.logger.error("Could not retrieve the list of admin pins");
            alert.showAndWait();
            e.printStackTrace();
        }
        return adminPin == pin;
    }

    /**
     * Gets a student from the database based on their RFID or email
     * todo: simplify, if possible
     * @param ID RFID to search for, -1 if no RFID being searched
     * @param studentEmail the email being searched, null if no email being searched
     * @return a student matching inputs if one exists in the db, null otherwise
     */
    public Student selectStudent(int ID, String studentEmail) {
        String query;
        String coList = "select students.studentID, students.studentName, students.email, parts.partName, checkout.checkoutAt, checkout.dueAt, checkout.checkoutID, checkout.studentID, parts.barcode, parts.serialNumber, parts.price, parts.partID " +
                "from students " +
                "left join checkout on students.studentID = checkout.studentID " +
                "left join parts on checkout.partID = parts.partID";
        if (ID == -1 && studentEmail != null) {
            studentEmail = cleanString(studentEmail);
            query = "select * from students where email = '" + studentEmail + "';";
            coList += " where students.email = '" + studentEmail +
                    "' AND checkout.checkinAt is null;";
        } else if (ID != -1) {
            query = "select * from students where studentID = " + ID + ";";
            coList += " where students.studentID = " + ID +
                    " AND checkout.checkinAt is null;";
        } else {
            return null;  // if there's no ID or email
        }
        Student student = null;
        String name = "", email = "";
        Date date = null;
        int id = 0, uniqueID = 0;
        ObservableList<CheckedOutItems> checkedOutItems = FXCollections.observableArrayList();
        ObservableList<OverdueItem> overdueItems = FXCollections.observableArrayList();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()) {
                name = resultSet.getString("studentName");
                email = resultSet.getString("email");
                id = resultSet.getInt("studentID");
                uniqueID = resultSet.getInt("uniqueID");
            }
            resultSet.close();
            statement.close();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(coList);
            resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()) {
                if (resultSet.getInt("checkout.checkoutID") != 0) {
                    checkedOutItems.add(new CheckedOutItems(
                            resultSet.getInt("checkout.checkoutID"),
                            resultSet.getString("students.studentName"),
                            resultSet.getString("students.email"),
                            resultSet.getInt("students.studentID"),
                            resultSet.getString("parts.partName"),
                            resultSet.getString("parts.barcode"),
                            resultSet.getString("parts.serialNumber"),
                            resultSet.getInt("parts.partID"),
                            timeUtils.convertStringtoDate(resultSet.getString("checkout.checkoutAt")),
                            timeUtils.convertStringtoDate(resultSet.getString("checkout.dueAt")),
                            resultSet.getString("parts.price")));

                    String dueAt = resultSet.getString("checkout.dueAt");
                    if (isOverdue(dueAt)) {
                        overdueItems.add(new OverdueItem(
                                resultSet.getInt("students.studentID"),
                                resultSet.getString("students.studentName"),
                                resultSet.getString("students.email"),
                                resultSet.getString("parts.partName"),
                                resultSet.getLong("parts.barcode"),
                                timeUtils.convertStringtoDate(dueAt),
                                resultSet.getString("checkout.checkoutID"),
                                resultSet.getDouble("parts.price")));
                    }
                }
            }
            statement.close();
            resultSet.close();
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
            e.printStackTrace();
        }
        return student;
    }

    public Student selectStudentWithoutLists(String email) {
        String query = "Select studentName, email, studentID from students where email = ?;";
        String studentEmail = "";
        String name = "";
        int id = 0;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, cleanString(email));
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                studentEmail = rs.getString("email");
                name = rs.getString("studentName");
                id = rs.getInt("studentID");
            }
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database.");
            throw new IllegalStateException("Cannot connect the database", e);
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
        try {
            PreparedStatement statement;
            if(isRFID){
                String getStudentNameFromIDQuery = "\n" +
                        "select studentName from students\n" +
                        "where studentID = ?";
                statement = connection.prepareStatement(getStudentNameFromIDQuery);
            } else {
                String getStudentNameFromEmailQuery = "select studentName from students where email = ?";
                statement = connection.prepareStatement(getStudentNameFromEmailQuery);
                input = cleanString(input);
            }
            statement.setString(1, input);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                sName = rs.getString("studentName");
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("IllegalStateException: Can't connect to the database when looking for student.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return sName;
    }

    /**
     * Adds a new student to the database
     * @param s student to be added
     */
    public void addStudent(Student s) {
        String query = "insert into students (studentID, email, studentName, createdAt, createdBy) values (" + s.getRFID()
                + ", '" + cleanString(s.getEmail()) + "', '" + cleanString(s.getName()) + "', date('" + timeUtils.getToday() + "'), '" + cleanString(this.worker.getName()) + "');";
        try {
            Statement statement = connection.createStatement();
            statement.execute(query);
            statement.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not add student");
            StudentCheckIn.logger.error("Could not add student, SQL Exception");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Adds a student to the database without the student's rfid. This is used for
     * importing a bunch of students when the rfid is unknown.
     * @param s the student to be added
     */
    public boolean importStudent(Student s) {
        // note: the controllerManageStudents class replaces "'" with "\\'" for this method,
        // but other methods in this class need to replace "'" with "\\'" so that it does not
        // mess up the database queries.
        String query = "insert into students (email, studentName, createdAt, createdBy) values ('" +
                cleanString(s.getEmail()) + "', '" + cleanString(s.getName()) + "', date('" + TimeUtils.getToday() + "'), '" + cleanString(this.worker.getName()) + "');";
        try {
            Statement statement = connection.createStatement();
            statement.execute(query);
            statement.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not add student");
            StudentCheckIn.logger.error("Could not add student {}, SQL Exception", s.getName());
            alert.showAndWait();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void updateStudent(Student s, int oldRFID) {
        if(studentHasCheckedOutItems(s.getEmail())){
            updateCheckedOutPartsRFID(s, oldRFID);
        }
        String query = "update students set students.studentID = " + s.getRFID() + ", students.studentName = '" +
                cleanString(s.getName()) + "', students.email = '" + cleanString(s.getEmail()) + "', students.updatedAt = date('" +
                TimeUtils.getToday() + "'), students.updatedBy = '" + cleanString(this.worker.getName()) + "' where students.uniqueID = " + s.getUniqueID() + ";";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not update student");
            StudentCheckIn.logger.error("Could not update student, SQL Exception");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Helper method for updateStudent that alters the RFID associated with checkouts if it is being changed
     * @param s the student being updated, with all the information changed
     * @param oldRFID the RFID the checkouts are associated with
     */
    public void updateCheckedOutPartsRFID(Student s, int oldRFID){
        if (s.getRFID() != oldRFID) {  // won't update if no change is made with the RFID
            String query = "UPDATE checkout SET checkout.studentID = " + s.getRFID() + " WHERE checkout.studentID = " + oldRFID + ";";
            try {
                Statement statement = connection.createStatement();
                statement.executeUpdate(query);
                statement.close();
            } catch (SQLException e) {
                stageUtils.errorAlert("Issue updating checked out parts for student that was being updated, SQL exception");
                e.printStackTrace();
            }
        }
    }

    public boolean studentHasCheckedOutItems(String email) {
        Student s = selectStudent(-1, email);
        if(s.getRFID() == 0 || s.getCheckedOut().isEmpty()){
            return false;
        }
        return !s.getCheckedOut().isEmpty();
    }

    /**
     * @return the number of parts with the same barcode that the selected student does not have returned
     */
    public int amountOutByStudent(long barcode, Student s) {
        String query = "SELECT COUNT(*) FROM checkout WHERE checkinAt is NULL AND barcode = " + barcode + " AND studentID = " + s.getRFID() + ";";
        ResultSet resultSet;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            resultSet.next();
            int result = resultSet.getInt(1);
            statement.close();
            resultSet.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @return the number of parts associated with one barcode that is not currently checked out
     */
    public int getNumPartsAvailableByBarcode(long barcode) {
        String query1 = "SELECT partID FROM parts WHERE barcode = " + barcode + " AND isCheckedOut = 0;";
        ResultSet resultSet;
        int result = 0;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query1);
            while(resultSet.next()) {
                boolean checkedOut = getIsCheckedOut(resultSet.getString(1));
                if (!checkedOut) {
                    result++;
                }
            }
            statement.close();
            resultSet.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Deletes a student from the database
     * @param email students email
     */
    public void deleteStudent(String email) {
        if (studentHasCheckedOutItems(email)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Student could not be deleted because they have parts checked out");
            alert.showAndWait();
            return;
        }
        String query = "delete from students where email = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, cleanString(email));
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not delete student.");
            StudentCheckIn.logger.error("SQL Exception: Could not delete student.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Adds a new worker to the database
     * @param w worker to be added
     */
    public void addWorker(Worker w) {
        int bit = w.isAdmin() ? 1 : 0;
        String query = "insert into workers (email, workerName, pin, pass, ID, isAdmin, createdAt, createdBy) values ('" + cleanString(w.getEmail()) +
                "', '" + cleanString(w.getName()) + "', " + w.getPin() + ", '" + cleanString(w.getPass()) + "', " + w.getRIFD() + "," + bit + ", date('" + TimeUtils.getToday() + "'), '" + cleanString(this.worker.getName()) + "');";
        try {
            Statement statement = connection.createStatement();
            statement.execute(query);
            statement.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not add worker");
            StudentCheckIn.logger.error("Could not add worker, SQL Exception");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Deletes a worker from the database
     * @param name workers name
     */
    public void deleteWorker(String name) {
        String query = "delete from workers where workers.workerName = '" + cleanString(name) + "';";
        try {
            Statement statement = connection.createStatement();
            statement.execute(query);
            statement.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not delete worker.");
            StudentCheckIn.logger.error("SQL Exception: Could not delete worker.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void updateWorker(Worker w) {
        int admin = w.isAdmin() ? 1 : 0;
        int edit = w.canEditParts() ? 1 : 0;
        int remove = w.canRemoveParts() ? 1 : 0;
        int work = w.canEditWorkers() ? 1 : 0;
        String query = "update workers set workers.workerName = '" + cleanString(w.getName()) + "', workers.pin = " +
                w.getPin() + ", workers.pass = '" + cleanString(w.getPass()) + "', workers.ID = " + w.getRIFD() + ", workers.isAdmin = " + admin + "," +
                " workers.email = '" + cleanString(w.getEmail()) + "', workers.editParts = " + edit +
                ", workers.workers = " + work + ", workers.removeParts = " + remove + ", workers.updatedAt = date('" +
                TimeUtils.getToday() + "'), workers.updatedBy = '" + cleanString(this.worker.getName()) + "' where workers.workerID = " +
                w.getID() + ";";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not update worker");
            StudentCheckIn.logger.error("Could not update worker, SQL Exception");
            alert.showAndWait();
            e.printStackTrace();
        }
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