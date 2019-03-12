package Database;

import Database.Objects.Worker;
import HelperClasses.DatabaseHelper;
import InventoryController.CheckedOutItems;
import InventoryController.StudentCheckIn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
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
    static String host = "jdbc:mysql://localhost:3306";
    static final String dbdriver = "com.mysql.jdbc.Driver";
    static final String dbname = "student_check_in";
    static Connection connection;
    private DatabaseHelper databaseHelper = new DatabaseHelper();

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
            StudentCheckIn.logger.error("SQLError: Can't connect to the database! Connection could not be established.");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, could not connect to the database.");
            alert.showAndWait();
//            System.exit(0);
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

            databaseHelper.getCurrentDateTimeStamp();
            String overdue = "select checkout.partID, checkout.studentID, students.studentName, students.email, parts.partName," +
                    " parts.serialNumber, checkout.dueAt, parts.price/100, checkout.checkoutID from checkout " +
                    "left join parts on checkout.partID = parts.partID " +
                    "left join students on checkout.studentID = students.studentID ";
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

    public void removeOverdue(int barcode){
        String query = "update checkout set checkout.dueAt = null where checkout.barcode = " + barcode + ";";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        }catch (SQLException e){
            StudentCheckIn.logger.error("SQL error: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to determine if item is overdue
     * @param date Due date of item
     * @return True if item is overdue
     */
    boolean isOverdue(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm a");
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
                        resultSet.getString("location"), resultSet.getString("barcode"), resultSet.getBoolean("isFaulty"),
                        resultSet.getInt("partID"), resultSet.getInt("isDeleted"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return part;
    }

    public Part selectPartByPartName(String partName) {
        String query = "select * from parts where partName = '" + partName + "';";
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
            int pin;
            boolean admin;
            while (resultSet.next()){
                name = resultSet.getString("workerName");
                pass = resultSet.getString("pass");
                email = resultSet.getString("email");
                pin = resultSet.getInt("pin");
                admin = resultSet.getByte("isAdmin") == 1;
                workerList.add(new Worker(name, email, pass, pin, admin));
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
     * Gets a student from the database based on their RFID
     *
     * @param ID RFID to search for
     * @return a student
     * @author Bailey Terry
     */
    public Student selectStudent(int ID){
        String todaysDate = gettoday().toString();
        String query = "select * from students where studentID = " + ID;
        String coList = "select students.studentName, parts.partName, checkout.checkoutAt, checkout.dueAt, checkout.checkoutID, parts.barcode, parts.partID " +
                "from students " +
                "left join checkout on students.studentID = checkout.studentID " +
                "left join parts on checkout.partID = parts.partID where students.studentID = " + ID  + ";";
        String pList = "select students.studentName, parts.partName, checkout.checkoutAt, checkout.reservedAt, checkout.dueAt, checkout.checkoutID, checkout.returnDate, checkout.course " +
                "from students " +
                "left join checkout on students.studentID = checkout.studentID " +
                "left join parts on checkout.partID = parts.partID where students.studentID = " + ID + " and checkout.reservedAt != '';";
        String oList = "select checkout.partID, checkout.studentID, students.studentName, students.email, parts.partName, " +
                "parts.serialNumber, checkout.dueAt, parts.price/100, checkout.checkoutID, checkout.checkinAt from checkout " +
                "inner join parts on checkout.partID = parts.partID " +
                "inner join students on checkout.studentID = students.studentID " +
                "where checkout.checkinAt is null";;
//                "where checkout.dueAt < date('" + todaysDate + "') and students.studentID = " + ID + ";";
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
                        resultSet.getString("parts.partName"), resultSet.getInt("parts.barcode"),
                        resultSet.getString("checkout.checkoutAt"), resultSet.getString("checkout.dueAt"),
                        resultSet.getInt("checkout.checkoutID"), resultSet.getInt("parts.partID")));
            }
            statement.close();
            resultSet.close();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(oList);
            resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()){
                String dueAt = resultSet.getString("checkout.dueAt");
                if (isOverdue(dueAt)) {
                    overdueItems.add(new OverdueItem(resultSet.getInt("checkout.studentID"),
                            resultSet.getString("students.studentName"), resultSet.getString("students.email"),
                            resultSet.getString("parts.partName"), resultSet.getString("parts.serialNumber"),
                            dueAt, resultSet.getString("parts.price/100"),
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
                        resultSet.getString("parts.partName"), resultSet.getString("checkout.checkoutAt"),
                        1, resultSet.getString("checkout.reservedAt"),
                        resultSet.getString("checkout.dueAt"), resultSet.getString("checkout.checkoutID"),
                        resultSet.getString("checkout.returnDate"), resultSet.getString("checkout.course")));
            }
            statement.close();
            resultSet.close();
            if (checkedOutItems.size() > 0) {
                date = checkedOutItems.get(0).getCheckedOutAt().get();
            }
            // date null if no checkouts
            for (int i = 0; i < checkedOutItems.size() && date != null; i++){
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
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

    /**
     * Adds a new student to the database
     *
     * @param s student to be added
     */
    public void addStudent(Student s){
        String query = "insert into students (studentID, email, studentName, createdAt, createdBy) values (" + s.getID()
                + ", '" + s.getEmail() + "', '" + s.getName() + "', date('" + gettoday() + "'), 'root');";
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
     * Deletes a student from the database
     *
     * @param name students name
     */
    public void deleteStudent(String name){
        String query = "delete from students where students.studentName = '" + name + "';";
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
     *
     * @param w worker to be added
     */
    public void addWorker(Worker w){
        int bit = w.isAdmin()? 1 : 0;
        String query = "insert into workers (email, workerName, pass, isAdmin, createdAt, createdBy) values ('" + w.getEmail() +
                "', '" + w.getName() + "', '" + w.getPass() + "', " + bit + ", date('" + gettoday() + "'), 'root');";
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
     *
     * @param name workers name
     */
    public void deleteWorker(String name){
        String query = "delete from workers where workers.workerName = '" + name + "';";
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
}