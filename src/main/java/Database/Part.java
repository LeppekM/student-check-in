package Database;

import javafx.beans.property.*;

public class Part {

    private final SimpleStringProperty partName, serialNumber, manufacturer, vendor, location, barcode;
    private final SimpleDoubleProperty price;
    private final SimpleIntegerProperty partID, quantity;
    private final SimpleBooleanProperty fault, isDeleted;

    public Part(String partName, String serialNumber, String manufacturer, double price, String vendor, String location, String barcode, boolean fault, int partID, boolean isDeleted){
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.manufacturer = new SimpleStringProperty(manufacturer);
        this.price = new SimpleDoubleProperty(price);
        this.vendor = new SimpleStringProperty(vendor);
        this.location = new SimpleStringProperty(location);
        this.barcode = new SimpleStringProperty(barcode);
        this.fault = new SimpleBooleanProperty(fault);
        this.partID = new SimpleIntegerProperty(partID);
        this.quantity = new SimpleIntegerProperty(0);
        this.isDeleted = new SimpleBooleanProperty(isDeleted);
    }

    public Part(String partName, String serialNumber, String manufacturer, double price, String vendor, String location, String barcode, int quantity){
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.manufacturer = new SimpleStringProperty(manufacturer);
        this.price = new SimpleDoubleProperty(price);
        this.vendor = new SimpleStringProperty(vendor);
        this.location = new SimpleStringProperty(location);
        this.barcode = new SimpleStringProperty(barcode);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.partID = null;
        this.fault = new SimpleBooleanProperty(false);
        this.isDeleted =new SimpleBooleanProperty(false);
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

    public String getBarcode() {
        return barcode.get();
    }

    public void setBarcode(String barcode) {
        this.barcode.set(barcode);
    }

    public boolean getFault() {
        return fault.get();
    }

    public void setFault(boolean fault) {
        this.fault.set(fault);
    }

    public int getPartID() {
        return partID.get();
    }

    public void setPartID(int partId) {
        this.partID.set(partId);
    }

    public boolean getIsDeleted() {
        return isDeleted.get();
    }

    public void setIsDeleted(boolean deleted) {
        this.isDeleted.set(deleted);
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
    public String toString(){
        return "Part Name: "+getPartName()+"\tSerial Number: "+getSerialNumber()+"\tManufacturer: " + getManufacturer() +
                "\tPrice: " + getPrice() + "\tVendor: " + getVendor() +
                "\tLocation: " + getLocation() + "\tBarcode: " + getBarcode() + "\tFault: " + getFault() +
                "\tPart ID: " + getPartID() + "\tIs Deleted: " + getIsDeleted() + "\n";
    }
}