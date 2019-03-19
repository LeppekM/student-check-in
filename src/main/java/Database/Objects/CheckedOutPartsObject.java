package Database.Objects;

public class CheckedOutPartsObject {

    private int checkoutID;
    private int partID;
    private long barcode;
    private int studentID;

    public CheckedOutPartsObject(int checkoutID, int partID, long barcode, int studentID) {
        this.checkoutID = checkoutID;
        this.partID = partID;
        this.barcode = barcode;
        this.studentID = studentID;
    }

    public CheckedOutPartsObject(long barcode, int studentID){
        this.barcode = barcode;
        this.studentID = studentID;
    }

    public int getCheckoutID() {
        return checkoutID;
    }

    public void setCheckoutID(int checkoutID) {
        this.checkoutID = checkoutID;
    }

    public int getPartID() {
        return partID;
    }

    public void setPartID(int partID) {
        this.partID = partID;
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(int barcode) {
        this.barcode = barcode;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    @Override
    public boolean equals(Object o){
        CheckedOutPartsObject c = (CheckedOutPartsObject) o;
        return Long.compare(barcode, c.barcode) == 0 && Long.compare(studentID, c.studentID) == 0;

    }
}
