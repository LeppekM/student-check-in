package gui;

public class ExtendedPart extends Part {

    private static String name;
    private static String location;
    private static double price;
    private static int serialNumber;
    private static int barcode;
    private static String vendor;
    private static String manufacturer;
    private static boolean fault;
    private static int studentID;
    private static String prof;

    public ExtendedPart(String name, String location, double price, int serialNumber, int barcode, String vendor, String manufacturer,
                        boolean fault, int studentID, String prof) {
        super(name, location, price, serialNumber, barcode, vendor, manufacturer, fault, studentID);
        setProf(prof);
    }

    public static String getProf() {
        return prof;
    }

    public static void setProf(String prof) {
        ExtendedPart.prof = prof;
    }
}
