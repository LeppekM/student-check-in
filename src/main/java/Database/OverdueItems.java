package Database;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class OverdueItems {
    private SimpleIntegerProperty ID;
//    private SimpleDoubleProperty price;
    private SimpleStringProperty part, serial, date, price;

    public OverdueItems(int partID, String partCon, String serialCon, String dateCon, String priceCon){
        this.ID = new SimpleIntegerProperty(partID);
        this.part = new SimpleStringProperty(partCon);
        this.serial = new SimpleStringProperty(serialCon);
        this.date = new SimpleStringProperty(dateCon);
        this.price = new SimpleStringProperty(priceCon);
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
}
