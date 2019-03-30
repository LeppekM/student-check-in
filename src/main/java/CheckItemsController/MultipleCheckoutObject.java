package CheckItemsController;

public class MultipleCheckoutObject {
    private long barcode;
    private int studentID, quantity;
    private boolean status;


    public MultipleCheckoutObject(long barcode, int studentID, boolean status, int quantity) {
        this.barcode = barcode;
        this.studentID = studentID;
        this.status = status;
        this.quantity = quantity;
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }

    public int getStudentID() {
        return studentID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public boolean isCheckedOut() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
