package Database;

import java.sql.*;
import java.time.LocalDateTime;

public class AddPart {
    private final String url = "jdbc:mysql://localhost:3306/student_check_in";
    private String addQuery = "INSERT INTO parts(partName, serialnumber, manufacturer, price, vendorID," +
            " location, barcode, createdAt, createdBy, isDeleted)"+
            "VALUES(?,?,?,?,?,?,?,?,?,?)";
    private String getpartIDQuery = "SELECT partID\n" +
            "FROM parts\n" +
            "ORDER BY partID DESC\n" +
            "LIMIT 1";

    VendorInformation vendorInformation = new VendorInformation();

    Database database = new Database();

    public int[] addCommonItems(Part part, int quantity) {
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            Part existing = database.selectPartByPartName(part.getPartName());
            if (existing == null || (part.getBarcode().equals(existing.getBarcode())
                    && part.getSerialNumber().equals(existing.getSerialNumber())
                    && part.getManufacturer().equals(existing.getManufacturer())
                    && part.getPrice() == existing.getPrice()
                    && part.getVendor().equals(existing.getVendor()))) {
                for (int i = 0; i < quantity; i++) {
                    PreparedStatement preparedStatement = connection.prepareStatement(addQuery);
                    insertQuery(part, preparedStatement).execute();
                    vendorInformation.getVendorList();
                    preparedStatement.close();
                }
                return new int[]{Integer.parseInt(part.getBarcode()), Integer.parseInt(part.getSerialNumber())};
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new int[]{-1, -1};
    }

    /**
     * This method adds an item to the database
     * @param part The part to be added
     */
    public int[] addUniqueItems(Part part, int quantity){
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            int inputBarcode = Integer.parseInt(part.getBarcode());
            int inputSerialNumber = Integer.parseInt(part.getSerialNumber());
            for (int i = 0; i < quantity; i++) {
                while (duplicateBarcode(part.getPartName(), inputBarcode)) {
                    inputBarcode++;
                }

                while (duplicateSerialNumber(part.getSerialNumber(), inputSerialNumber)) {
                    inputSerialNumber++;
                }

                PreparedStatement preparedStatement = connection.prepareStatement(addQuery);
                insertQuery(part, preparedStatement).execute();
                vendorInformation.getVendorList();
                preparedStatement.close();
            }
            return new int[]{inputBarcode, inputSerialNumber};
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    public boolean duplicateBarcode(String partName, int barcode) {
        return database.getAllBarcodesForPartName(partName).contains("" + barcode)
                || database.getUniqueBarcodesBesidesPart(partName).contains("" + barcode);
    }

    public boolean duplicateSerialNumber(String partName, int serialNumber) {
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
            preparedStatement.setString(7, part.getBarcode());
            preparedStatement.setString(8, getCurrentDate());
            //Hardcoded created by because we don't have workers setup yet
            preparedStatement.setString(9, "Jim");
            preparedStatement.setInt(10, part.getIsDeleted());
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
