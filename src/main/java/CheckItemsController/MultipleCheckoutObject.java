package CheckItemsController;

/**
 * This class stores info for parts with a multiple quantity option
 */

public class MultipleCheckoutObject {
    private long barcode;
    private int studentID, quantity;
    private boolean status;
    private boolean extended, faulty;
    private String faultyText;


    public MultipleCheckoutObject(long barcode, int studentID, boolean status, int quantity, boolean extended, boolean faulty, String faultyText) {
        this.barcode = barcode;
        this.studentID = studentID;
        this.status = status;
        this.quantity = quantity;
        this.extended = extended;
        this.faulty = faulty;
        this.faultyText = faultyText;
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

    public boolean isExtended(){return extended;}

    public boolean isFaulty(){return faulty;}

    public String getFaultyText(){return faultyText;}

    public void setExtended(boolean extended) {this.extended = extended;}

}
