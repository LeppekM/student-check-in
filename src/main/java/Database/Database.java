package Database;

import CheckItemsController.CheckoutObject;
import Database.ObjectClasses.Part;
import Database.ObjectClasses.SavedPart;
import Database.ObjectClasses.Student;
import Database.ObjectClasses.Worker;
import HelperClasses.DatabaseHelper;
import InventoryController.CheckedOutItems;
import InventoryController.IController;
import InventoryController.StudentCheckIn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Database implements IController {
    //DB root pass: Userpassword123
    public static final String username = "root";
    public static final String password = "Userpassword123";
    static String host = "jdbc:mysql://localhost:3306";
    static final String dbdriver = "com.mysql.jdbc.Driver";
    static final String dbname = "student_check_in";
    static Connection connection;
    private DatabaseHelper databaseHelper = new DatabaseHelper();
    private Worker worker;

    /**
     * This creates a connection to the database
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
            StudentCheckIn.logger.error("SQLError: Can't connect to the database! Connection could not be established.");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, could not connect to the database.");
            alert.showAndWait();
//            System.exit(0);
        }
    }

    /**
     * Returns the connection to the database created by constructor method
     * @return the database connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * This method uses an SQL query to get all items in the databse with a due date less than today's date
     * @return a list of overdue items
     */
    public ObservableList<OverdueItem> getOverdue() {
        ObservableList<OverdueItem> data = FXCollections.observableArrayList();

            databaseHelper.getCurrentDateTimeStamp();
            String overdue = "select checkout.partID, checkout.studentID, students.studentName, students.email, parts.partName," +
                    " parts.serialNumber, checkout.dueAt, parts.price/100, checkout.checkoutID from checkout " +
                    "left join parts on checkout.partID = parts.partID " +
                    "left join students on checkout.studentID = students.studentID " +
                    "where checkout.checkinAt is null";
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(overdue);
                while(resultSet.next()){
                    String dueAt = resultSet.getString("checkout.dueAt");
                    if (isOverdue(dueAt)){
                        data.add(new OverdueItem(resultSet.getInt("checkout.studentID"), resultSet.getString("students.studentName"),
                                resultSet.getString("students.email"), resultSet.getString("parts.partName"),
                                resultSet.getString("parts.serialNumber"), dueAt,
                                resultSet.getString("parts.price/100"), resultSet.getString("checkout.checkoutID")));
                    }
                }
                resultSet.close();
                statement.close();
            } catch(SQLException e){
                StudentCheckIn.logger.error("SQL Error: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        return data;
    }

    /**
     * Helper method to determine if item is overdue
     * @param date Due date of item
     * @return true if item is overdue; false otherwise
     */
    public boolean isOverdue(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
        if(!date.isEmpty()) {
            try {
                Date current = dateFormat.parse(databaseHelper.getCurrentDateTimeStamp());
                Date dueDate = dateFormat.parse(date);
                return current.after(dueDate);
            } catch (ParseException e) {
                StudentCheckIn.logger.error("Parse Error: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Helper method to get the current date
     * @return today's date
     */
    private static Date gettoday() {
        long date = System.currentTimeMillis();
        return new java.sql.Date(date);
    }

    /**
     * Calculates the date that was 2 years ago from today
     * @return the date that was 2 years ago from today
     */
    private static Date getTwoYearsAgo() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -2);
        return cal.getTime();
    }

    /**
     * This uses an SQL query to soft delete an item from the database
     * @param partID a unique part id
     */
    public void deleteItem(int partID) {
        try {
            String delete = "update parts p set p.deletedBy = '" + this.worker.getName().replace("'", "\\'") + "', p.isDeleted = 1, p.deletedAt = date('"
                    + gettoday() + "') where p.partID = " + partID + ";";
            Statement statement = connection.createStatement();
            statement.executeUpdate(delete);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Notifications.create().title("Successful!").text("Part with ID = " + partID + " has been successfully deleted").hideAfter(new Duration(5000)).show();//.showWarning();
    }

    /**
     * For each part that's name equals partName, this sets the "isDeleted" value to 1, which is
     * true. This is called a soft delete, because the part is not actually removed from the
     * database, but will not show up in anything, since it is marked as deleted.
     * @param partName the name of the parts that are deleted
     */
    public void deleteParts(String partName) {
        try {
            String deleteQuery = "UPDATE parts p set p.deletedBy = '" + this.worker.getName().replace("'", "\\'") + "', p.isDeleted = 1, " +
                    "p.deletedAt = date('" + gettoday() + "') WHERE p.partName = '" + partName + "';";
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
        String query = "select * from parts where partID = " + partID;
        Part part = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                part = new Part(resultSet.getString("partName"), resultSet.getString("serialNumber"),
                        resultSet.getString("manufacturer"), resultSet.getDouble("price"), resultSet.getString("vendorID"),
                        resultSet.getString("location"), resultSet.getLong("barcode"), resultSet.getBoolean("isFaulty"),
                        resultSet.getInt("partID"), resultSet.getInt("isDeleted"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return part;
    }

    /**
     * Gets info about the student who most recently checked a part in or out
     * @param partID the part ID of the part being checked
     * @return a Student object that represents the student who last checked the part in or out
     */
    public Student getStudentToLastCheckout(int partID) {
        String query = "SELECT * FROM checkout INNER JOIN students ON checkout.studentID = students.studentID WHERE partID = " + partID + ";";
        int studentID = -1;
        Student student = null;
        String studentName = "";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                studentID = resultSet.getInt("studentID");
                studentName = resultSet.getString("studentName");
            }
            student = selectStudent(studentID, null);
            if (student.getName().equals("")) {
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
        String query = "SELECT * FROM checkout WHERE partID = " + partID + ";";
        CheckoutObject checkoutObject = null;
        String studentID = "", barcode= "", dueAt = "";
        String checkoutAt = null, checkinAt = null;

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
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
     * This method clears the checkout data that is over 2 years old and does not involve a part
     * that is faulty.
     */
    public void clearOldHistory() {
        String query =
                "DELETE checkout " +
                "FROM checkout " +
                        "INNER JOIN parts " +
                        "ON checkout.partID = parts.partID " +
                        "AND checkout.checkinAt < ? " +
                        "AND parts.isFaulty = 0;";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(query);
            DateFormat target = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
            String formattedDate = target.format(getTwoYearsAgo());
            System.out.println(formattedDate);
            preparedStatement.setString(1, formattedDate);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method to remove a fault
     * @param barcode barcode of faulty part
     * @param name name of faulty part
     * @return partID
     */
    private int getPartID(int barcode, String name){
        String query = "select * from parts where partName = '" + name + "' and barcode = " + barcode + ";";
        int ID = 0;
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                ID = resultSet.getInt("partID");
            }
            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return ID;
    }

    /**
     * Removes faulty part from table
     * @param barcode barcode of faulty part
     * @param name name of faulty part
     */
    public void resolveFault(int barcode, String name){
        int partID = getPartID(barcode, name);
        String query = "delete from fault where partID = " + partID + ";";
        String pquery = "update parts set isFaulty = 0, updatedAt = date('" + gettoday() + "'), updatedBy = '" +
                this.worker.getName().replace("'", "\\'") + "' where partID = " + partID + ";";
        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            statement.executeUpdate(pquery);
            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Gets the fault description for the part with the matching part ID
     * @param partID the part ID for the part being checked
     * @return the fault description of the part
     */
    public String getFaultDescription(int partID) {
        String query = "SELECT * FROM fault WHERE partID = " + partID + ";";
        String description = "";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                description = resultSet.getString("description");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return description;
    }

    /**
     * Gets a part that has the matching part name
     * @param partName the name of the part being checked
     * @return the part with the matching part name
     */
    public Part selectPartByPartName(String partName) {
        String query = "select * from parts where partName = '" + partName + "';";
        Part part = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                part = new Part(resultSet.getString("partName"), resultSet.getString("serialNumber"),
                        resultSet.getString("manufacturer"), resultSet.getDouble("price"), resultSet.getString("vendorID"),
                        resultSet.getString("location"), resultSet.getLong("barcode"), false,
                        resultSet.getInt("partID"), resultSet.getInt("isDeleted"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return part;
    }

    /**
     * Gets the name of the part that the barcode corresponds to
     * @param barcode the barcode for the part being checked
     * @return the name of the part
     */
    public String getPartNameFromBarcode(int barcode) {
        String query = "SELECT partName from parts where barcode = " + barcode + ";";
        String partName = "";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                partName = resultSet.getString("partName");
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return partName;
    }

    /**
     * Checks whether the part with the given part ID is currently checked out
     * @param partID the part ID of the part being checked
     * @return true if the matching part is checked out; false otherwise
     */
    public boolean getIsCheckedOut(String partID) {
        String query = "SELECT COUNT(*) FROM checkout WHERE checkinAt is NULL AND partID = " + partID + ";";
        ResultSet resultSet;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            resultSet.next();
//            statement.close();
            if (resultSet.getInt(1) > 0) {
                resultSet.close();
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
     * @param partID the part ID of the part exempt from the search
     * @return the list of serial numbers
     */
    public ArrayList<String> getOtherSerialNumbersForPartName(String partName, String partID) {
        String query = "SELECT serialNumber FROM parts WHERE parts.isDeleted = 0 AND partName = '" + partName + "' AND partID != " + partID + ";";
        ArrayList<String> serialNumbers = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                serialNumbers.add(resultSet.getString("serialNumber"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return serialNumbers;
    }

    /**
     * Gets a list of barcodes used by a part with a given name, except for the part with the given part ID
     * @param partName the name of parts being checked
     * @param partID the part ID of the part exempt from the search
     * @return the list of barcodes
     */
    public ArrayList<String> getOtherBarcodesForPartName(String partName, String partID) {
        String query = "SELECT barcode FROM parts WHERE parts.isDeleted = 0 AND partName = '" + partName + "' AND partID != " + partID + ";";
        ArrayList<String> barcodes = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                barcodes.add(resultSet.getString("barcode"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return barcodes;
    }

    /**
     * Gets a list of all barcodes used by parts with the given name
     * @param partName name of the part being checked
     * @return the list of barcodes
     */
    public ArrayList<String> getAllBarcodesForPartName(String partName) {
        String query = "SELECT barcode FROM parts WHERE parts.isDeleted = 0 AND partName = '" + partName + "';";
        ArrayList<String> barcodes = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                barcodes.add(resultSet.getString("barcode"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return barcodes;
    }

    /**
     * Gets a list of all part IDs used by parts with the given name
     * @param partName name of the part being checked
     * @return the list of part IDs
     */
    public ArrayList<String> getAllPartIDsForPartName(String partName) {
        String query = "SELECT partID FROM parts WHERE parts.isDeleted = 0 AND partName = '" + partName + "';";
        ArrayList<String> partIDs = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                partIDs.add(resultSet.getString("partID"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return partIDs;
    }

    /**
     * Gets a list of all serial numbers used by parts with the given name
     * @param partName name of the part being checked
     * @return the list of serial numbers
     */
    public ArrayList<String> getAllSerialNumbersForPartName(String partName) {
        String query = "SELECT serialNumber FROM parts WHERE parts.isDeleted = 0 AND partName = '" + partName + "';";
        ArrayList<String> serialNumbers = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                serialNumbers.add(resultSet.getString("serialNumber"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return serialNumbers;
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
     * Checks whether the parts with the given part name have unique serial numbers
     * @param partName the name of the parts being checked
     * @return true if the part has unique serial numbers; false otherwise
     */
    public boolean hasUniqueSerialNumbers(String partName) {
        ArrayList<String> serialNumbers = getAllSerialNumbersForPartName(partName);
        if (countPartsOfType(partName) > 1) {
            for (int i = 0; i < serialNumbers.size(); i++) {
                for (int j = 0; j < serialNumbers.size(); j++) {
                    if (i != j && serialNumbers.get(i).equals(serialNumbers.get(j))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Gets the number of parts that's part name matches the given part name
     * @param partName the part name being checked
     * @return the number of parts
     */
    public int countPartsOfType(String partName) {
        String query = "SELECT COUNT(*) FROM parts WHERE isDeleted = 0 AND partName = '" + partName + "';";
        ResultSet resultSet;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Gets a list of different part names for all parts. This is used for ensuring that when
     * a part name is added, it is not already used.
     * @return the list of distinct part names
     */
    public ArrayList<String> getUniquePartNames() {
        String query = "SELECT distinct partName FROM parts;";
        ArrayList<String> partNames = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                partNames.add(resultSet.getString("partName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return partNames;
    }

    /**
     * Gets a list of different barcodes for all parts. This is used for ensuring that when
     * a barcode is added, it is not already used.
     * @return the list of distinct barcodes
     */
    public ArrayList<String> getUniqueBarcodes() {
        String query = "SELECT DISTINCT barcode FROM parts";
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

    /**
     * This method returns a list of all distinct barcodes in the database, except for the ones
     * that belong to a part with the passed in part name. This is used for making sure not to
     * use an already existing barcode when adding parts.
     * @param partName the part name that is an exception
     * @return the list of barcodes
     */
    public ArrayList<String> getUniqueBarcodesBesidesPart(String partName) {
        String query = "SELECT DISTINCT barcode FROM parts WHERE partName != '" + partName + "';";
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

    /**
     * This method checks to see whether the database contains a part with a passed in part name
     * @param partName the name of the part being checked
     * @return true if the database contains a part with part name that equals partName; false otherwise
     */
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
                studentsList.add(new Student(name, id, email));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not retrieve the list of students");
            StudentCheckIn.logger.error("Could not retrieve the list of students");
            alert.showAndWait();
            e.printStackTrace();
        }
        return studentsList;
    }

    /**
     * This method returns a list of all of the student emails in the system
     * @return the list of all student rfids
     */
    public ObservableList<String> getStudentRFIDs() {
        ObservableList<String> rfids = FXCollections.observableArrayList();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT studentID FROM students");
            String rfid;
            while (resultSet.next()) {
                rfid = resultSet.getString("studentID");
                rfids.add(rfid);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not retrieve the list of rfids");
            StudentCheckIn.logger.error("Could not retrieve the list of rfids");
            alert.showAndWait();
            e.printStackTrace();
        }
        return rfids;
    }

    /**
     * This method returns a list of all of the student emails in the system
     * @return the list of all student emails
     */
    public ObservableList<String> getStudentEmails() {
        ObservableList<String> emails = FXCollections.observableArrayList();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT email FROM students");
            String email;
            while (resultSet.next()) {
                email = resultSet.getString("email");
                emails.add(email);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not retrieve the list of students");
            StudentCheckIn.logger.error("Could not retrieve the list of students");
            alert.showAndWait();
            e.printStackTrace();
        }
        return emails;
    }

    /**
     * Gets the list of workers from the database
     * @return observable list of workers
     */
    public ObservableList<Worker> getWorkers(){
        ObservableList<Worker> workerList = FXCollections.observableArrayList();
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM workers");
            String name;
            String email;
            String pass;
            int ID;
            int pin;
            boolean admin;
            boolean parts;
            boolean over;
            boolean workers;
            boolean students;
            while (resultSet.next()){
                name = resultSet.getString("workerName");
                ID = resultSet.getInt("workerID");
                pass = resultSet.getString("pass");
                email = resultSet.getString("email");
                pin = resultSet.getInt("pin");
                admin = resultSet.getByte("isAdmin") == 1;
                parts = resultSet.getByte("editParts") == 1;
                over = resultSet.getByte("overdue") == 1;
                workers = resultSet.getByte("workers") == 1;
                students = resultSet.getByte("removeParts") == 1;
                workerList.add(new Worker(name, ID, email, pass, pin, admin, parts, workers, students, over));
            }
            resultSet.close();
            statement.close();
        }catch (SQLException e){
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

            ResultSet resultSet = statement.executeQuery("SELECT * FROM workers WHERE email = '" + email.replace("'", "\\'") + "';");
            String name;
            String password;
            int ID;
            int pin;
            boolean isAdmin;
            boolean parts;
            boolean over;
            boolean workers;
            boolean students;
            if (resultSet.next()) {
                name = resultSet.getString("workerName");
                ID = resultSet.getInt("workerID");
                password = resultSet.getString("pass");
                isAdmin = resultSet.getByte("isAdmin") == 1;
                parts = resultSet.getByte("editParts") == 1;
                over = resultSet.getByte("overdue") == 1;
                workers = resultSet.getByte("workers") == 1;
                students = resultSet.getByte("removeParts") == 1;
                pin = resultSet.getInt("pin");
                worker = new Worker(name, ID, email, password, pin, isAdmin, parts, workers, students, over);
            }
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

    /**
     * This method checks whether pin matches one of the administrators' pins
     * @param pin the inputted pin that is being checked
     * @return true if pin is one of the administrators' pins; false otherwise
     */
    public boolean isValidPin(int pin) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT pin FROM workers;");
            int adminPin;
            while (resultSet.next()) {
                adminPin = resultSet.getInt("pin");
                if (adminPin == pin) {
                    return true;
                }
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not retrieve the list of admin pins");
            StudentCheckIn.logger.error("Could not retrieve the list of admin pins");
            alert.showAndWait();
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Gets a student from the database based on their RFID
     * @param ID RFID to search for
     * @return a student
     */
    public Student selectStudent(int ID, String studentEmail){
        String query = null;
        String coList = null;
        String pList = null;
        String oList = null;
        if (studentEmail == null) {
            query = "select * from students where studentID = " + ID;
            coList = "select students.studentID, students.studentName, students.email, parts.partName, checkout.checkoutAt, checkout.dueAt, checkout.checkoutID, parts.barcode, parts.serialNumber, parts.price, parts.partID " +
                    "from students " +
                    "left join checkout on students.studentID = checkout.studentID " +
                    "left join parts on checkout.partID = parts.partID" +
                    " where students.studentID = " + ID +
                    " AND checkout.checkinAt is null;";
            pList = "select students.studentName, parts.partName, checkout.checkoutAt, checkout.reservedAt, checkout.dueAt, checkout.checkoutID, checkout.returnDate, checkout.course " +
                    "from students " +
                    "left join checkout on students.studentID = checkout.studentID " +
                    "left join parts on checkout.partID = parts.partID where students.studentID = " + ID + " and checkout.reservedAt != '';";
            oList = "select checkout.partID, checkout.studentID, students.studentName, students.email, parts.partName, " +
                    "parts.serialNumber, checkout.dueAt, parts.price/100, checkout.checkoutID, checkout.checkinAt from checkout " +
                    "inner join parts on checkout.partID = parts.partID " +
                    "inner join students on checkout.studentID = students.studentID " +
                    "where checkout.checkinAt is null";
//                "where checkout.dueAt < date('" + todaysDate + "') and students.studentID = " + ID + ";";
        }else if (ID == -1){
            studentEmail = studentEmail.replace("'", "\\'");
            query = "select * from students where email = '" + studentEmail + "';";
            coList = "select students.studentName, students.email, students.studentID, parts.partName, checkout.checkoutAt, checkout.dueAt, checkout.checkoutID, parts.barcode, parts.serialNumber, parts.price, parts.partID " +
                    "from students " +
                    "left join checkout on students.studentID = checkout.studentID " +
                    "left join parts on checkout.partID = parts.partID" +
                    " where students.email = '" + studentEmail +
                    "' AND checkout.checkinAt is null;";
            pList = "select students.studentName, students.email, students.studentID, parts.partName, checkout.checkoutAt, checkout.reservedAt, checkout.dueAt, checkout.checkoutID, checkout.returnDate, checkout.course " +
                    "from students " +
                    "left join checkout on students.studentID = checkout.studentID " +
                    "left join parts on checkout.partID = parts.partID where students.email = '" + studentEmail + "' and checkout.reservedAt != '';";
            oList = "select checkout.partID, checkout.studentID, students.studentName, students.email, parts.partName, " +
                    "parts.serialNumber, checkout.dueAt, parts.price/100, checkout.checkoutID, checkout.checkinAt from checkout " +
                    "inner join parts on checkout.partID = parts.partID " +
                    "inner join students on checkout.studentID = students.studentID " +
                    "where checkout.checkinAt is null";
        }
        Student student = null;
        String name = "";
        String email = "";
        String date = "";
        int id = 0;
        int uniqueID = 0;
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
                uniqueID = resultSet.getInt("uniqueID");
            }
            resultSet.close();
            statement.close();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(coList);
            resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()){
                checkedOutItems.add(new CheckedOutItems(
                        resultSet.getInt("checkout.checkoutID"),
                        resultSet.getString("students.studentName"),
                        resultSet.getString("students.email"),
                        resultSet.getInt("students.studentID"),
                        resultSet.getString("parts.partName"),
                        resultSet.getString("parts.barcode"),
                        resultSet.getString("parts.serialNumber"),
                        resultSet.getInt("parts.partID"),
                        resultSet.getString("checkout.checkoutAt"),
                        resultSet.getString("checkout.dueAt"),
                        resultSet.getString("parts.price")));
            }
            statement.close();
            resultSet.close();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(oList);
            resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()){
                String dueAt = resultSet.getString("checkout.dueAt");
                int studentID = resultSet.getInt("checkout.studentID");
                if (isOverdue(dueAt) && (studentID==ID || email.equals(studentEmail))) {
                    overdueItems.add(new OverdueItem(resultSet.getInt("checkout.studentID"),
                            resultSet.getString("students.studentName"),
                            resultSet.getString("students.email"),
                            resultSet.getString("parts.partName"),
                            resultSet.getString("parts.serialNumber"),
                            dueAt,
                            resultSet.getString("parts.price/100"),
                            resultSet.getString("checkout.checkoutID")));
                }
            }
            statement.close();
            resultSet.close();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(pList);
            resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()){
                savedParts.add(new SavedPart(resultSet.getString("students.studentName"),
                        resultSet.getString("parts.partName"),
                        resultSet.getString("checkout.checkoutAt"),
                        1,
                        resultSet.getString("checkout.reservedAt"),
                        resultSet.getString("checkout.dueAt"),
                        resultSet.getString("checkout.checkoutID"),
                        resultSet.getString("checkout.returnDate"),
                        resultSet.getString("checkout.course")));
            }
            statement.close();
            resultSet.close();
            if (checkedOutItems.size() > 0) {
                date = checkedOutItems.get(0).getCheckedOutDate().get();
            }
            // date null if no checkouts
            for (int i = 0; i < checkedOutItems.size() && date != null; i++){
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                    Date d = sdf.parse(date);
                    Date d1 = sdf.parse(checkedOutItems.get(i).getCheckedOutDate().get());
                    if (d1.after(d)){
                        date = checkedOutItems.get(i).getCheckedOutDate().get();
                    }
                }catch (ParseException e){
                    e.printStackTrace();
                }
            }
            student = new Student(name, uniqueID, id, email, date, checkedOutItems, overdueItems, savedParts);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return student;
    }

    /**
     * Adds a new student to the database
     * @param s student to be added
     */
    public void addStudent(Student s){
        String email = s.getEmail().replace("'", "\\'");
        String name = s.getName().replace("'", "\\'");
        String query = "insert into students (studentID, email, studentName, createdAt, createdBy) values (" + s.getRFID()
                + ", '" + email + "', '" + name + "', date('" + gettoday() + "'), '" + this.worker.getName().replace("'", "\\'") + "');";
        try {
            Statement statement = connection.createStatement();
            statement.execute(query);
            statement.close();
        }catch (SQLException e){
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
        // note: the controllermanagestudents class replaces "'" with "\\'" for this method,
        // but other methods in this class need to replace "'" with "\\'" so that it does not
        // mess up the database queries.
        String query = "insert into students (email, studentName, createdAt, createdBy) values ('" +
                s.getEmail() + "', '" + s.getName() + "', date('" + gettoday() + "'), '" + this.worker.getName().replace("'", "\\'") + "');";
        try {
            Statement statement = connection.createStatement();
            statement.execute(query);
            statement.close();
        }catch (SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not add student");
            StudentCheckIn.logger.error("Could not add student " + s.getName().replace("'", "\\'") + ", SQL Exception");
            alert.showAndWait();
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not add student");
            StudentCheckIn.logger.error("Could not add student " + s.getName().replace("'", "\\'") + ", SQL Exception");
            alert.showAndWait();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void updateStudent(Student s){
        String query = "update students set students.studentID = " + s.getRFID() + ", students.studentName = '" +
                s.getName().replace("'", "\\'") + "', students.email = '" + s.getEmail().replace("'", "\\'") + "', students.updatedAt = date('" +
                gettoday().toString() + "'), students.updatedBy = '" + this.worker.getName().replace("'", "\\'") + "' where students.uniqueID = " + s.getUniqueID() +";";
        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            statement.close();
        }catch (SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not update student");
            StudentCheckIn.logger.error("Could not update student, SQL Exception");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Deletes a student from the database
     * @param name students name
     */
    public void deleteStudent(String name){
        String query = "delete from students where students.studentName = '" + name.replace("'", "\\'") + "';";
        try{
            Statement statement = connection.createStatement();
            statement.execute(query);
            statement.close();
        }catch (SQLException e){
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
    public void addWorker(Worker w){
        int bit = w.isAdmin()? 1 : 0;
        String query = "insert into workers (email, workerName, pin, pass, isAdmin, createdAt, createdBy) values ('" + w.getEmail().replace("'", "\\'") +
                "', '" + w.getName().replace("'", "\\'") + "', " + w.getPin() + ", '" + w.getPass() + "', " + bit + ", date('" + gettoday() + "'), '" + this.worker.getName().replace("'", "\\'") + "');";
        try {
            Statement statement = connection.createStatement();
            statement.execute(query);
            statement.close();
        }catch (SQLException e){
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
    public void deleteWorker(String name){
        String query = "delete from workers where workers.workerName = '" + name.replace("'", "\\'") + "';";
        try{
            Statement statement = connection.createStatement();
            statement.execute(query);
            statement.close();
        }catch (SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not delete worker.");
            StudentCheckIn.logger.error("SQL Exception: Could not delete worker.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void updateWorker(Worker w){
        int admin = w.isAdmin() ? 1 : 0;
        int over = w.isOver() ? 1: 0;
        int edit = w.isEdit() ? 1: 0;
        int remove = w.isRemove() ? 1 : 0;
        int work = w.isWorker() ? 1 : 0;
        String query = "update workers set workers.workerName = '" + w.getName().replace("'", "\\'") + "', workers.pin = " +
                w.getPin() + ", workers.pass = '" + w.getPass() + "', workers.isAdmin = " + admin + "," +
                " workers.email = '" + w.getEmail().replace("'", "\\'") + "', workers.overdue = " + over + ", workers.editParts = " + edit +
                ", workers.workers = " + work + ", workers.removeParts = " + remove + ", workers.updatedAt = date('" +
                gettoday().toString() + "'), workers.updatedBy = '" + this.worker.getName().replace("'", "\\'") + "' where workers.workerID = " +
                w.getID() + ";";
        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            statement.close();
        }catch (SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not update worker");
            StudentCheckIn.logger.error("Could not update worker, SQL Exception");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Used to keep track of which worker is currently logged in by passing the worker into
     * each class.
     * @param worker the currently logged in worker
     */
    @Override
    public void initWorker(Worker worker) {
        if (this.worker == null){
            this.worker = worker;
        }
    }
}