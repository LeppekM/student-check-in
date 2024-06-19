package Database.ObjectClasses;

public class Worker {

    private String name;
    private int ID;
    private int RIFD;
    private String email;
    private String pass;
    private int pin;
    private boolean isAdmin;
    private boolean edit;
    private boolean worker;
    private boolean remove;

    public Worker(String name, int ID, String email, String pass, int pin, int RFID, boolean isAdmin, boolean edit, boolean worker,
                  boolean remove){
        this.name = name;
        this.ID = ID;
        this.email = email;
        this.pass = pass;
        this.pin = pin;
        this.RIFD = RFID;
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

    public Worker(String name, String email, String pass, int RFID){
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.RIFD = RFID;
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

    public int getID(){
        return ID;
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

    public int getRIFD() {
        return RIFD;
    }

    public void setRIFD(int RIFD) {
        this.RIFD = RIFD;
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
