package Database;

import Database.ObjectClasses.CheckedOutPartsObject;
import HelperClasses.DatabaseHelper;
import HelperClasses.StageWrapper;
import InventoryController.StudentCheckIn;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CheckingOutPart {

    private final String url = Database.host + "/student_check_in";
    private final String getPartIDtoAdd = "SELECT partID \n" +
            "FROM parts \n" +
            "WHERE barcode = ? \n" +
            "    AND isCheckedOut = 0\n" +
            "    LIMIT 1";

    private final String getCheckoutIDFromPartID = "select checkoutID from checkout where (partID = ? and checkinAt is null) ";

    private DatabaseHelper helper = new DatabaseHelper();
    private List<CheckedOutPartsObject> checkedOutItems = new ArrayList<>();
    private StageWrapper stageWrapper = new StageWrapper();


    /**
     * Adds a new checkout item to the database
     * @param barcode
     * @param studentID
     */
    public boolean addNewCheckoutItem(long barcode, int studentID){
        if(barcodeExists(barcode)) {
            try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
                String addToCheckouts = "INSERT INTO checkout (partID, studentID, barcode, checkoutAt, dueAt)\n" +
                        "VALUE(?,?,?,?,?);";
                PreparedStatement statement = connection.prepareStatement(addToCheckouts);
                addNewCheckoutHelper(barcode, studentID, statement).execute();
                statement.close();
                return true;
            } catch (SQLException e) {
                StudentCheckIn.logger.error("SQLException: Can't connect to the database when adding new checkout item.");
                throw new IllegalStateException("Cannot connect to the database", e);
            } catch (NullPointerException e){
                stageWrapper.errorAlert("Part with barcode of "+ barcode +" is already checked out");
                return false;
            }
        }
        else {
            stageWrapper.errorAlert("Barcode was not found in database, part was not checked out");
        }
        return true;
    }



    /**
     * Checks out multiple items
     * @param barcode Barcode of item
     * @param studentID Student ID of student
     * @param quantity Number of items
     */
    public boolean addMultipleCheckouts(long barcode, int studentID, int quantity){
        List<Long> barcodes = getNonCheckedOutBarcodes(barcode);
        if(quantity ==1){
           return addNewCheckoutItem(barcode, studentID);
        }

        for (int i =0; i< quantity; i++){
            if(i<barcodes.size()){
                addNewCheckoutItem(barcodes.get(i), studentID);
            }
            else {
                stageWrapper.errorAlert("Checked out " + i + " part(s). No more parts in inventory can be checked out");
                return false;
            }
        }
        barcodes.clear();
        return true;
    }

    /**
     * Gets a list of all barcodes
     * @param barcode
     * @return A list of barcodes
     */
    public List<Long> getAllBarcodes(long barcode){
        List<Long> barcodes = new LinkedList<>();
        if(!barcodeExists(barcode)){
            barcodes.add(1L);
            barcodes.add(2L);
            return barcodes;
        }
        String selectBarcodes = "select barcode from parts where partName = ?";
        String partName = getListofBarcodes(barcode);

        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(selectBarcodes);
            statement.setString(1, partName);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                barcodes.add(rs.getLong("barcode"));
            }
            statement.close();
            rs.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database.");
            throw new IllegalStateException("Cannot connect the database", e);
        }
        return barcodes;
    }

    /**
     * Gets list of barcodes not checked out
     * @param barcode
     * @return List of barcodes not checked out
     */
    public List<Long> getNonCheckedOutBarcodes(long barcode){
        List<Long> barcodes = new LinkedList<>();
        if(!barcodeExists(barcode)){//Always return a case where barcodes will be different for method to checkout multiple barcodes.
            barcodes.add(1L);
            barcodes.add(2L);
            return barcodes;
        }
        String selectBarcodesNotCheckedOut = "select barcode from parts where partName = ? and isCheckedOut = 0";
        String partName = getListofBarcodes(barcode);

        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(selectBarcodesNotCheckedOut);
            statement.setString(1, partName);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                barcodes.add(rs.getLong("barcode"));
            }
            statement.close();
            rs.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database.");
            throw new IllegalStateException("Cannot connect the database", e);
        }
        return barcodes;
    }

    /**
     * Gets partName from a barcode
     * @param barcode Barcode to get partname from
     * @return Part name
     */
     private String getListofBarcodes(long barcode){
        String getPartName = "select partName from parts where barcode = ?";
        String partName = null;
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(getPartName);
            statement.setLong(1, barcode);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                partName = rs.getString("partName");
            }
            statement.close();
            rs.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database when setting part status.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return partName;
    }

    /**
     * Helper for adding checkout item to DB, also sets isCheckedout in parts table to 1
     * @param barcode Barcode of part
     * @param studentID Student ID entered
     * @param preparedStatement Statement to be executed
     * @return
     */
    private PreparedStatement addNewCheckoutHelper(long barcode, int studentID, PreparedStatement preparedStatement){
        int partID = getPartIDFromBarcode(barcode, getPartIDtoAdd);
        if (partID == 0){
            throw new  NullPointerException();
        }
        try {
            preparedStatement.setInt(1, partID);
            preparedStatement.setInt(2, studentID);
            preparedStatement.setLong(3, barcode);
            preparedStatement.setString(4, helper.getCurrentDateTimeStamp());
            preparedStatement.setString(5, helper.setDueDate());
        }catch (SQLException e){
            StudentCheckIn.logger.error("SQLException: Can't connect to the database.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        String setPartStatusCheckedOut = "UPDATE parts SET isCheckedOut = 1 WHERE partID = ?";
        setPartStatus(partID, setPartStatusCheckedOut); //This will set the partID found above to a checked out status
        return preparedStatement;
    }

    /**
     * This method takes a barcode as parameter and returns the corresponding partID to be added to checkout table.
     * @param barcode barcode of part
     * @return Part ID to return
     */
    int getPartIDFromBarcode(long barcode, String status){
        int partID = 0;

        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(status);
            statement.setLong(1, barcode);
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
     * Checks if barcode exists
     * @param barcode Barcode to be checked
     * @return True if barcode exists
     */
    private boolean barcodeExists(long barcode){
        List<Long> barcodes = new LinkedList<>();
        final String getAllBarcodes = "select barcode from parts";
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(getAllBarcodes);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                barcodes.add(rs.getLong("barcode"));
            }
            statement.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database when checking if barcode exists.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return (barcodes.contains(barcode));
    }

    /**
     * Sets part checkout status to 1 to signify the part is checked out
     * @param partID Part ID of part
     */
    void setPartStatus(int partID, String status){
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
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
     * Returns barcodes of items that are checked out
     * @return A list of barcodes in the checked out tab
     */
    public List<CheckedOutPartsObject> returnCheckedOutObjects(){
        if(checkedOutItems.size()!=0){
            checkedOutItems.clear();
        }
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            Statement statement = connection.createStatement();
            String getCheckedOutItems = "select barcode, studentID from checkout \n" +
                    "where checkinAt is NULL";
            ResultSet resultSet = statement.executeQuery(getCheckedOutItems);
            while(resultSet.next()){
                checkedOutItems.add(new CheckedOutPartsObject(resultSet.getLong("barcode"), resultSet.getInt("studentID")));
            }
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database.");
            throw new IllegalStateException("Cannot connect the database", e);
        }
        return checkedOutItems;
    }



    int getCheckoutIDFromBarcodeAndStudentID(int studentID, long barcode){
        int checkoutID = 0;
        String query = "select checkoutID from checkout where studentID =? and barcode = ? and checkinAt IS NULL limit 1";
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, studentID);
            statement.setLong(2, barcode);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                checkoutID = rs.getInt("checkoutID");
            }
            statement.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database when getting checkout from part ID.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return checkoutID;
    }

    public boolean partCanBeCheckedOut(long barcode, int studentID){
        return false;
//        int sID = 0;
////        int partID = getPartIDFromBarcode(barcode,)
//        System.out.println(partID);
//        String query ="select studentID from checkout where partID = ? and checkinAt is null";
//        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
//            PreparedStatement statement = connection.prepareStatement(query);
//            statement.setInt(1, partID);
//            ResultSet rs = statement.executeQuery();
//            if(rs.next()){
//                sID = rs.getInt("studentID");
//            }
//            statement.close();
//        } catch (SQLException e) {
//            StudentCheckIn.logger.error("SQLException: Can't connect to the database when getting checkout from part ID.");
//            throw new IllegalStateException("Cannot connect to the database", e);
//        }
//        System.out.println(studentID);
//        System.out.println(sID);
//        if(sID == 0 ){
//            return true;
//        }
//        return sID == studentID;
    }

    int getPartIDFromBarcodeAndStudentID(int studentID, long barcode){
        int partID = 0;
        String query = "select partID from checkout where studentID =? and barcode = ? and checkinAt IS NULL limit 1";
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, studentID);
            statement.setLong(2, barcode);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                partID = rs.getInt("partID");
            }
            statement.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database when getting checkout from part ID.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return partID;
    }

    /**
     * Method to return an item to checked in status
     * @param barcode Barcode of item
     */
    public void setItemtoCheckedin(int studentID, long barcode){
        String getPartIDtoCheckin = "SELECT partID \n" +
                "FROM parts \n" +
                "WHERE barcode = ? \n" +
                "    AND isCheckedOut = 1\n" +
                "    LIMIT 1";
        int partID = getPartIDFromBarcode(barcode, getPartIDtoCheckin);
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            String setDate = "update checkout\n" +
                    "set checkinAt =? \n" +
                    "where checkoutID = ?";
            PreparedStatement statement = connection.prepareStatement(setDate);
            statement.setString(1, helper.getCurrentDateTimeStamp());
            statement.setInt(2, getCheckoutIDFromBarcodeAndStudentID(studentID, barcode));
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        String setPartStatusCheckedIn = "UPDATE parts SET isCheckedOut = 0 WHERE partID = ?";
        setPartStatus(partID, setPartStatusCheckedIn); //Sets part to checkedin

    }
}
