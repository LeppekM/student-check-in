package Database.ObjectClasses;

public interface DBObject {

    void update(String partName, String serialNumber, String manufacturer, double price, String vendor, String location, long barcode);

    String toString();

}
