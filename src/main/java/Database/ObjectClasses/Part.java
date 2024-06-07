package Database.ObjectClasses;

import Database.AddPart;
import javafx.beans.property.*;

public class Part implements DBObject{

    private SimpleStringProperty partName, serialNumber, manufacturer, vendor, location;
    private final SimpleLongProperty barcode;
    private SimpleDoubleProperty price;
    private SimpleIntegerProperty partID, quantity;
    private final SimpleBooleanProperty checkedOut = new SimpleBooleanProperty(false);
    AddPart addPart = new AddPart();


    public Part(String partName, String serialNumber, double price, String location, long barcode) {
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.price = new SimpleDoubleProperty(price);
        this.location = new SimpleStringProperty(location);
        this.barcode = new SimpleLongProperty(barcode);
    }

    public Part(String partName, String serialNumber, String manufacturer, double price, String vendor, String location, long barcode, boolean toMakeConstructorsDifferent, int partID) {
        this(partName, serialNumber, price, location, barcode);
        this.manufacturer = new SimpleStringProperty(manufacturer);
        this.vendor = new SimpleStringProperty(vendor);
        this.partID = new SimpleIntegerProperty(partID);
        this.quantity = new SimpleIntegerProperty(0);
    }

    public Part(String partName, String serialNumber, String manufacturer, double price, String vendor, String location, long barcode, int quantity) {
        this(partName, serialNumber, price, location, barcode);
        this.manufacturer = new SimpleStringProperty(manufacturer);
        this.vendor = new SimpleStringProperty(vendor);
        this.location = new SimpleStringProperty(location);
        this.quantity = new SimpleIntegerProperty(quantity);
        //Returns the next part id
        this.partID = new SimpleIntegerProperty(addPart.getPartID());
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

    public void setManufacturer(String manufacturer) {
        this.manufacturer.set(manufacturer);
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

    public void setVendor(String vendor) {
        this.vendor.set(vendor);
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

    public void setCheckedOut(int checkedOut) {
        this.checkedOut.set(checkedOut == 1);
    }

    public boolean getCheckedOut() {
        return checkedOut.get();
    }

    public int getPartID() {
        return partID.get();
    }

    public void setPartID(int partId) {
        this.partID.set(partId);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    @Override
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