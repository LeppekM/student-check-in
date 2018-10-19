package gui;

public class ExtendedPart extends Part {
 
    private final SimpleStringProperty professor;
+<<<<<<< destination:d3db1937de8c018f656443f0bbe37b60437ccb3c
 
    public ExtendedPart(String partName, long serialNumber, String manufacturer, int quantity, double price, String vendor, String location, String barcode, boolean fault, long studentId, String prof){
        super(partName, serialNumber, manufacturer, quantity, price, vendor, location, barcode, fault, studentId);
        this.professor = new SimpleStringProperty(prof);
    }

    public String getName() {
        return this.professor.get();
    }
 
    public void setName(String prof) {
        this.professor.set(prof);
+=======
+    
+    public ExtendedPart(String partName, long serialNumber, String manufacturer, int quantity, double price, String vendor, String location, String barcode, boolean fault, long studentId, String prof){
+        super(partName, serialNumber, manufacturer, quantity, price, vendor, location, barcode, fault, studentId);
+        this.professor = new SimpleStringProperty(prof);
+    }
+ 
+    public String getName() {
+        return this.professor.get();
+    }
+ 
+    public void setName(String prof) {
+        this.professor.set(prof);
+    
+    public static String getCourseID() {
+        return courseID;
+    }
+
+    public static void setCourseID(String courseID) {
+        ExtendedPart.courseID = courseID;
+    }
+
+    public static Date getDueDate() {
+        return dueDate;
+    }
+
+    public static void setDueDate(String dueDate) {
+        this.dueDate = dueDate;
+>>>>>>> source:16bc91f8230788fcbda218ed522ef2894f76d637
    }
}
