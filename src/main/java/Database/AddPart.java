package Database;

import Database.ObjectClasses.Part;

import java.sql.*;
import java.time.LocalDateTime;

public class AddPart {
    private final String url = Database.host + Database.dbname;
    private String addQuery = "INSERT INTO parts(partName, serialnumber, manufacturer, price, vendorID," +
            " location, barcode, isCheckedOut, createdAt, createdBy)"+
            "VALUES(?,?,?,?,?,?,?,?,?,?)";
    private String getpartIDQuery = "SELECT partID\n" +
            "FROM parts\n" +
            "ORDER BY partID DESC\n" +
            "LIMIT 1";

    VendorInformation vendorInformation = new VendorInformation();

    /**
     * Adds items to DB
     * @param part Part to be added
     * @param database  Database
     * @param quantity Number of items to be added
     */

    public void addCommonItems(Part part, Database database, int quantity) {
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            Part existing = database.selectPartByPartName(part.getPartName());
                for (int i = 0; i < quantity; i++) {
                    PreparedStatement preparedStatement = connection.prepareStatement(addQuery);
                    insertQuery(part, preparedStatement).execute();
                    preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method adds an item to the database
     * @param part The part to be added
     */
    public long[] addUniqueItems(Part part, Database database, int quantity){
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            long inputBarcode = part.getBarcode();
            int inputSerialNumber = Integer.parseInt(part.getSerialNumber());
            for (int i = 0; i < quantity; i++) {
                while (duplicateBarcode(part.getPartName(), database, inputBarcode)) {
                    inputBarcode++;
                }

                while (duplicateSerialNumber(part.getSerialNumber(), database, inputSerialNumber)) {
                    inputSerialNumber++;
                }

                PreparedStatement preparedStatement = connection.prepareStatement(addQuery);
                insertQuery(part, preparedStatement).execute();
                preparedStatement.close();
            }
            return new long[]{inputBarcode, inputSerialNumber};
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    public boolean duplicateBarcode(String partName, Database database, long barcode) {
        return database.getAllBarcodesForPartName(partName).contains("" + barcode)
                || database.getUniqueBarcodesBesidesPart(partName).contains("" + barcode);
    }

    public boolean duplicateSerialNumber(String partName, Database database, int serialNumber) {
        return database.getAllSerialNumbersForPartName(partName).contains("" + serialNumber);
    }

    /**
     * This method sets the information from a part to the item being added to the database
     * @param part The part being added to the database
     * @param preparedStatement The statement that has items being set to it
     * @return
     */
    private PreparedStatement insertQuery(Part part, PreparedStatement preparedStatement){
        try {
            preparedStatement.setString(1, part.getPartName());
            preparedStatement.setString(2, part.getSerialNumber());
            preparedStatement.setString(3, part.getManufacturer());
            preparedStatement.setDouble(4, part.getPrice());
            preparedStatement.setInt(5, vendorInformation.getVendorIDFromVendor(part.getVendor()));
            preparedStatement.setString(6, part.getLocation());
            preparedStatement.setLong(7, part.getBarcode());
            preparedStatement.setInt(8, 0);
            preparedStatement.setString(9,getCurrentDate());
            preparedStatement.setString(10, "Jim");
            //Hardcoded created by because we don't have workers setup yet
        }catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return preparedStatement;
    }

    /**
     * This method gets the current date
     * @return Current date
     */
    private String getCurrentDate(){
        return LocalDateTime.now().toString();
    }

    /**
     * This method queries the database and finds the max ID. It then increments this id to return
     * the next partID when adding a part
     * @return The new part ID to be added to the database for the corresponding part
     */
    public int getPartID(){
        int partID = 0;
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(getpartIDQuery);
            while(rs.next()){
                partID = rs.getInt("partID");
            }
            statement.close();

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        //Gets max id from table, then increments to create new partID
        return partID;
    }
}
