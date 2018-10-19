package gui;

public class ExtendedPart extends Part {

    private final SimpleStringProperty professor;
    private Date dueDate;
    private String courseID;

    public ExtendedPart(String partName, long serialNumber, String manufacturer, int quantity, double price, String vendor, String location, String barcode, boolean fault, long studentId, String prof, String courseID, Date dueDate) {
        super(name, location, price, serialNumber, barcode, vendor, manufacturer, fault, studentID);
        this.professor = new SimpleStringProperty(prof);
        this.dueDate = dueDate;
        this.courseID = courseID;
    }

    public String getProf() {
        return this.professor.get();
    }

    public void setProf(String prof) {
        this.professor.set(prof);
    }

    public static String getCourseID() {
        return courseID;
    }

    public static void setCourseID(String courseID) {
        ExtendedPart.courseID = courseID;
    }

    public static Date getDueDate() {
        return dueDate;
    }

    public static void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
