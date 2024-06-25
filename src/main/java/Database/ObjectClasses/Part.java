package Database.ObjectClasses;

import Database.Database;
import javafx.beans.property.*;

public class Part {

    private SimpleStringProperty partName, serialNumber, manufacturer, vendor, location;
    private final SimpleLongProperty barcode;
    private SimpleDoubleProperty price;
    private SimpleIntegerProperty partID;
    private final SimpleBooleanProperty checkedOut = new SimpleBooleanProperty(false);


    public Part(String partName, String serialNumber, double price, String location, long barcode) {
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.price = new SimpleDoubleProperty(price);
        this.location = new SimpleStringProperty(location);
        this.barcode = new SimpleLongProperty(barcode);
    }

    public Part(String partName, String serialNumber, String manufacturer, double price, String vendor, String location, long barcode, int partID) {
        this(partName, serialNumber, price, location, barcode);
        this.manufacturer = new SimpleStringProperty(manufacturer);
        this.vendor = new SimpleStringProperty(vendor);
        this.partID = new SimpleIntegerProperty(partID);
    }

    public Part(String partName, String serialNumber, String manufacturer, double price, String vendor, String location, long barcode) {
        this(partName, serialNumber, price, location, barcode);
        this.manufacturer = new SimpleStringProperty(manufacturer);
        this.vendor = new SimpleStringProperty(vendor);
        this.location = new SimpleStringProperty(location);
        //Returns the next part id
        Database database = Database.getInstance();
        this.partID = new SimpleIntegerProperty(database.getMaxPartID() + 1);
    }

    public Part(String partName, String serialNumber, String location, long barcode, int partID, double price) {
        this(partName, serialNumber, price, location, barcode);
        this.location = new SimpleStringProperty(location);
        this.partID = new SimpleIntegerProperty(partID);
    }

    public String getPartName() {
        return partName.get();
    }

    public void setPartName(String name) {
        this.partName.set(name);
    }

    public String getSerialNumber() {
        return serialNumber.get();
    }

    public void setSerialNumber(String serial) {
        this.serialNumber.set(serial);
    }

    public String getManufacturer() {
        return manufacturer.get();
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public String getVendor() {
        return vendor.get();
    }

    public String getLocation() {
        return location.get();
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public Long getBarcode() {
        return barcode.get();
    }

    public void setBarcode(long barcode) {
        this.barcode.set(barcode);
    }

    public boolean getCheckedOut() {
        return checkedOut.get();
    }

    public int getPartID() {
        return partID.get();
    }

    public void update(String partName, String serialNumber, String manufacturer, double price, String vendor, String location, long barcode) {
        this.partName.set(partName);
        this.serialNumber.set(serialNumber);
        this.manufacturer.set(manufacturer);
        this.price.set(price);
        this.vendor.set(vendor);
        this.location.set(location);
        this.barcode.set(barcode);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Part Name: ").append(getPartName());
        str.append("\tSerial Number: ").append(getSerialNumber());
        str.append("\tManufacturer: ").append(getManufacturer());
        str.append("\tPrice: ").append(getPrice());
        str.append("\tVendor: ").append(getVendor());
        str.append("\tLocation: ").append(getLocation());
        str.append("\tBarcode: ").append(getBarcode());
        str.append("\tFault: ").append(false);
        str.append("\tFault Description: "); // will not correctly parse if these are not appended
        str.append("\tPart ID: ").append(getPartID());
        str.append("\tIs Deleted: ");
        str.append("\tIs CheckedOut: ").append(getCheckedOut()).append("\n");
        return str.toString();
    }
}