package Database;

import javafx.beans.property.*;

public class Part {

    private final SimpleStringProperty partName, serialNumber, manufacturer, vendor, location, barcode, faultDesc;
    private final SimpleDoubleProperty price;
    private final SimpleIntegerProperty partID, quantity, isDeleted;
    private final SimpleBooleanProperty fault, checkedOut = new SimpleBooleanProperty(false);
    AddPart addPart = new AddPart();


    //TODO Combine all these constructors I mean my god why do we have three that do the same thing
    public Part(String partName, String serialNumber, String manufacturer, double price, String vendor, String location, String barcode, boolean fault, int partID, int isDeleted){
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
        this.isDeleted = new SimpleIntegerProperty(isDeleted);
        this.faultDesc = new SimpleStringProperty("");
    }

    public Part(String partName, String serialNumber, String manufacturer, double price, String vendor, String location, String barcode, int quantity, int isDeleted){
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.manufacturer = new SimpleStringProperty(manufacturer);
        this.price = new SimpleDoubleProperty(price);
        this.vendor = new SimpleStringProperty(vendor);
        this.location = new SimpleStringProperty(location);
        this.barcode = new SimpleStringProperty(barcode);
        this.quantity = new SimpleIntegerProperty(quantity);
        //Returns the next part id
        this.partID = new SimpleIntegerProperty(addPart.getPartID());
        this.fault = new SimpleBooleanProperty(false);
        this.isDeleted =new SimpleIntegerProperty(isDeleted);
        this.faultDesc = new SimpleStringProperty("");
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

    public boolean getFault() {return fault.get();}
    public SimpleBooleanProperty faultProperty(){
        return fault;
    }
    public void setFault(boolean fault) {
        this.fault.set(fault);
    }

    public void setCheckedOut(int checkedOut) {
        this.checkedOut.set(checkedOut==1?true:false);

    }
    public boolean getCheckedOut() {System.out.println("Hello There!" + checkedOut.get());
    return checkedOut.get();
        }

    public int getPartID() {
        return partID.get();
    }
    public void setPartID(int partId) {
        this.partID.set(partId);
    }

    public int getIsDeleted() {
        return isDeleted.get();
    }
    public void setIsDeleted(int deleted) {
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

    public String getFaultDesc() {
        return faultDesc.get();
    }
    public void setFaultDesc(String faultDesc) {
        this.faultDesc.set(faultDesc);
    }

    public void update(String partName, String serialNumber, String manufacturer, double price, String vendor, String location, String barcode) {
        this.partName.set(partName);
        this.serialNumber.set(serialNumber);
        this.manufacturer.set(manufacturer);
        this.price.set(price);
        this.vendor.set(vendor);
        this.location.set(location);
        this.barcode.set(barcode);
    }

    @Override
    public String toString(){
        return "Part Name: "+getPartName()+"\tSerial Number: "+getSerialNumber()+"\tManufacturer: " + getManufacturer() +
                "\tPrice: " + getPrice() + "\tVendor: " + getVendor() +
                "\tLocation: " + getLocation() + "\tBarcode: " + getBarcode() + "\tFault: " + getFault() +
                "\tFault Description: " + getFaultDesc() + "\tPart ID: " + getPartID() + "\tIs Deleted: "
                + getIsDeleted() + "\tIs Checked Out: " + getCheckedOut() + "\n";
    }
}