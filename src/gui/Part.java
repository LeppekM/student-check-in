package gui;

public class Part {

    private static String location;
    private static double price;
    private static int serialNumber;
    private static int barcode;
    private static String vendor;
    private static String manufacturer;
    private static boolean fault;
    private static int studentID;

    public void Part(String location, double price, int serialNumber, int barcode, String vendor, String manufacturer,
                     boolean fault, int studentID){
        setLocation(location);
        setPrice(price);
        setSerialNumber(serialNumber);
        setBarcode(barcode);
        setVendor(vendor);
        setManufacturer(manufacturer);
        setFault(fault);
        setStudentID(studentID);
    }

    public static String getLocation() {
        return location;
    }

    public static void setLocation(String location) {
        Part.location = location;
    }

    public static double getPrice() {
        return price;
    }

    public static void setPrice(double price) {
        Part.price = price;
    }

    public static int getSerialNumber() {
        return serialNumber;
    }

    public static void setSerialNumber(int serialNumber) {
        Part.serialNumber = serialNumber;
    }

    public static String getVendor() {
        return vendor;
    }

    public static void setVendor(String vendor) {
        Part.vendor = vendor;
    }

    public static String getManufacturer() {
        return manufacturer;
    }

    public static void setManufacturer(String manufacturer) {
        Part.manufacturer = manufacturer;
    }

    public static boolean isFault() {
        return fault;
    }

    public static void setFault(boolean fault) {
        Part.fault = fault;
    }

    public static int getStudentID() {
        return studentID;
    }

    public static void setStudentID(int studentID) {
        Part.studentID = studentID;
    }

    public static int getBarcode() {
        return barcode;
    }

    public static void setBarcode(int barcode) {
        Part.barcode = barcode;
    }
}
