package gui;

import javafx.beans.property.SimpleStringProperty;

public class ExtendedPart extends Part {

    private final SimpleStringProperty professor;

    public ExtendedPart(String partName, long serialNumber, String manufacturer, int quantity, double price, String vendor, String location, String barcode, boolean fault, long studentId, String prof){
        super(partName, serialNumber, manufacturer, quantity, price, vendor, location, barcode, fault, studentId);
        this.professor = new SimpleStringProperty(prof);
    }

    public String getName() {
        return this.professor.get();
    }

    public void setName(String prof) {
        this.professor.set(prof);
    }
}
