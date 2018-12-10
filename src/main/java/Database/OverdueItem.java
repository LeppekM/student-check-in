package Database;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class OverdueItem {
    private SimpleIntegerProperty ID;
    private SimpleStringProperty part;
    private SimpleStringProperty serial;
    private SimpleStringProperty date;
    private SimpleStringProperty price;
    private SimpleStringProperty name;
    private SimpleStringProperty email;

    public OverdueItem(int studentID, String name, String email, String partCon, String serialCon, String dateCon, String priceCon){
        this.ID = new SimpleIntegerProperty(studentID);
        this.part = new SimpleStringProperty(partCon);
        this.serial = new SimpleStringProperty(serialCon);
        this.date = new SimpleStringProperty(dateCon);
        this.price = new SimpleStringProperty(priceCon);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
    }

    public int getID() {
        return ID.get();
    }

    public SimpleIntegerProperty IDProperty() {
        return ID;
    }

    public void setID(int ID) {
        this.ID.set(ID);
    }

    public String getPrice() {
        return price.get();
    }

    public SimpleStringProperty priceProperty() {
        return price;
    }

    public void setPrice(String price) {
        this.price.set(price);
    }

    public String getPart() {
        return part.get();
    }

    public SimpleStringProperty partProperty() {
        return part;
    }

    public void setPart(String part) {
        this.part.set(part);
    }

    public String getSerial() {
        return serial.get();
    }

    public SimpleStringProperty serialProperty() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial.set(serial);
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getEmail() {
        return email.get();
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }
}
