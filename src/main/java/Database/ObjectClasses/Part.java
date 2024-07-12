package Database.ObjectClasses;

import Database.Database;
import javafx.beans.property.*;

/**
 * Object which represents the Part entity in the database and real life parts/kits being checked in/out
 */
public class Part {

    private final SimpleStringProperty partName, serialNumber;
    private SimpleStringProperty manufacturer, vendor, location;
    private final SimpleLongProperty barcode;
    private final SimpleDoubleProperty price;
    private SimpleIntegerProperty partID;


    public Part(String partName, String serialNumber, double price, String location, long barcode) {
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.price = new SimpleDoubleProperty(price);
        this.location = new SimpleStringProperty(location);
        this.barcode = new SimpleLongProperty(barcode);
    }

    public Part(String partName, String serialNumber, String manufacturer, double price, String vendor,
                String location, long barcode, int partID) {
        this(partName, serialNumber, price, location, barcode);
        this.manufacturer = new SimpleStringProperty(manufacturer);
        this.vendor = new SimpleStringProperty(vendor);
        this.partID = new SimpleIntegerProperty(partID);
    }

    public Part(String partName, String serialNumber, String manufacturer, double price, String vendor,
                String location, long barcode) {
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

    public String getSerialNumber() {
        return serialNumber.get();
    }

    public String getManufacturer() {
        return manufacturer.get();
    }

    public double getPrice() {
        return price.get();
    }

    public String getVendor() {
        return vendor.get();
    }

    public String getLocation() {
        return location.get();
    }

    public Long getBarcode() {
        return barcode.get();
    }

    public void setBarcode(long barcode) {
        this.barcode.set(barcode);
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
        return "Part Name: " + getPartName() +
                "\tSerial Number: " + getSerialNumber() +
                "\tManufacturer: " + getManufacturer() +
                "\tPrice: " + getPrice() +
                "\tVendor: " + getVendor() +
                "\tLocation: " + getLocation() +
                "\tBarcode: " + getBarcode() +
                "\tPart ID: " + getPartID() + "\n";
    }
}