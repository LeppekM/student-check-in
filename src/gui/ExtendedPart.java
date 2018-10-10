package gui;

public class ExtendedPart extends Part {

    private static String prof;
    private static String courseID;
    private static String dueDate;

    public ExtendedPart(String name, String location, double price, int serialNumber, int barcode, String vendor, String manufacturer,
                        boolean fault, int studentID, String prof, String courseID, String dueDate) {
        super(name, location, price, serialNumber, barcode, vendor, manufacturer, fault, studentID);
        setProf(prof);
        setCourseID(courseID);
        setDueDate(dueDate);
    }

    public static String getProf() {
        return prof;
    }

    public static void setProf(String prof) {
        ExtendedPart.prof = prof;
    }

    public static String getCourseID() {
        return courseID;
    }

    public static void setCourseID(String courseID) {
        ExtendedPart.courseID = courseID;
    }

    public static String getDueDate() {
        return dueDate;
    }

    public static void setDueDate(String dueDate) {
        ExtendedPart.dueDate = dueDate;
    }
}
