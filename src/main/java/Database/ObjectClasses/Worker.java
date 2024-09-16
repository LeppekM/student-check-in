package Database.ObjectClasses;

/**
 * Object representing Worker entity in database and real-life employee
 */
public class Worker {

    private String name, email, pass;
    private int workerID, pin;
    private long workerRFID;
    private boolean isAdmin, edit,  worker, remove;

    public Worker(String name, int workerID, String email, String pass, int pin, long rfid, boolean isAdmin,
                  boolean edit, boolean worker, boolean remove){
        this.name = name;
        this.workerID = workerID;
        this.email = email;
        this.pass = pass;
        this.pin = pin;
        this.workerRFID = rfid;
        this.isAdmin = isAdmin;
        if (isAdmin){
            this.edit = true;
            this.worker = true;
            this.remove = true;
        } else {
            this.edit = edit;
            this.worker = worker;
            this.remove = remove;
        }
    }

    public Worker(String name, String email, String pass, long rfid){
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.workerRFID = rfid;
        this.pin = 0;
        this.isAdmin = false;
        edit = false;
        worker = false;
        remove = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWorkerID(){
        return workerID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass(){
        return pass;
    }

    public void setPass(String pass){
        this.pass = pass;
    }

    public int getPin(){
        return pin;
    }

    public void setPin(int pin){
        this.pin = pin;
    }

    public long getWorkerRFID() {
        return workerRFID;
    }

    public void setWorkerRFID(long workerRFID) {
        this.workerRFID = workerRFID;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean canEditParts() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public boolean canEditWorkers() {
        return worker;
    }

    public void setWorker(boolean worker) {
        this.worker = worker;
    }

    public boolean canRemoveParts() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }
}
