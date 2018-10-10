package gui;

public class Part {

    private static String name;
    private static String location;
    private static double price;
    private static int serialNumber;
    private static int barcode;
    private static String vendor;
    private static String manufacturer;
    private static boolean fault;
    private static int studentID;

    public Part(String name, String location, double price, int serialNumber, int barcode, String vendor, String manufacturer,
                     boolean fault, int studentID){
        setName(name);
        setLocation(location);
        setPrice(price);
        setSerialNumber(serialNumber);
        setBarcode(barcode);
        setVendor(vendor);
        setManufacturer(manufacturer);
        setFault(fault);
        setStudentID(studentID);
    }

    public static String getName() {
        return name;
    }

    private static void setName(String name) {
        Part.name = name;
    }

    public static String getLocation() {
        return location;
    }

    private static void setLocation(String location) {
        Part.location = location;
    }

    public static double getPrice() {
        return price;
    }

    private static void setPrice(double price) {
        Part.price = price;
    }

    public static int getSerialNumber() {
        return serialNumber;
    }

    private static void setSerialNumber(int serialNumber) {
        Part.serialNumber = serialNumber;
    }

    public static String getVendor() {
        return vendor;
    }

    private static void setVendor(String vendor) {
        Part.vendor = vendor;
    }

    public static String getManufacturer() {
        return manufacturer;
    }

    private static void setManufacturer(String manufacturer) {
        Part.manufacturer = manufacturer;
    }

    public static boolean isFault() {
        return fault;
    }

    private static void setFault(boolean fault) {
        Part.fault = fault;
    }

    public static int getStudentID() {
        return studentID;
    }

    private static void setStudentID(int studentID) {
        Part.studentID = studentID;
    }

    public static int getBarcode() {
        return barcode;
    }

    private static void setBarcode(int barcode) {
        Part.barcode = barcode;
    }
}
