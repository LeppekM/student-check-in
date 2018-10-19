package gui;

import javafx.beans.property.*;

public class Part {

    private final SimpleStringProperty partName, serialNumber, manufacturer, vendor, location, barcode;
    private final SimpleDoubleProperty price;
    private final SimpleIntegerProperty quantity;
    private final SimpleLongProperty studentId;
    private final SimpleBooleanProperty fault;

    public Part(String partName, String serialNumber, String manufacturer, int quantity, double price, String vendor, String location, String barcode, boolean fault, long studentId){
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.manufacturer = new SimpleStringProperty(manufacturer);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
        this.vendor = new SimpleStringProperty(vendor);
        this.location = new SimpleStringProperty(location);
        this.barcode = new SimpleStringProperty(barcode);
        this.fault = new SimpleBooleanProperty(fault);
        this.studentId = new SimpleLongProperty(studentId);
    }

    public String getName() {
        return this.partName.get();
    }

    public void setName(String name) {
        this.partName.set(name);
    }

    public String getSerial() {
        return this.serialNumber.get();
    }

    public void setSerial(String serial) {
        this.serialNumber.set(serial);
    }

    public String getManufacturer() {
        return manufacturer.get();
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer.set(manufacturer);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quant) {
        quantity.set(quant);
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

    public long getStudentId() {
        return studentId.get();
    }

    public void setVendor(long studentId) {
        this.studentId.set(studentId);
    }
}