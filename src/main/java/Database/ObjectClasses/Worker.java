package Database.ObjectClasses;

public class Worker {

    private String name;
    private int ID;
    private String email;
    private String pass;
    private int pin;
    private boolean isAdmin;
    private boolean edit;
    private boolean worker;
    private boolean remove;
    private boolean over;

    public Worker(String name, int ID, String email, String pass, int pin, boolean isAdmin, boolean edit, boolean worker,
                  boolean remove, boolean over){
        this.name = name;
        this.ID = ID;
        this.email = email;
        this.pass = pass;
        this.pin = pin;
        this.isAdmin = isAdmin;
        if (isAdmin){
            edit = true;
            worker = true;
            remove = true;
            over = true;
        }else {
            this.edit = edit;
            this.worker = worker;
            this.remove = remove;
            this.over = over;
        }
    }

    public Worker(String name, String email, String pass){
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.pin = 0;
        this.isAdmin = false;
        edit = false;
        worker = false;
        remove = false;
        over = false;
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public boolean isWorker() {
        return worker;
    }

    public void setWorker(boolean worker) {
        this.worker = worker;
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    public boolean isOver() {
        return over;
    }

    public void setOver(boolean over) {
        this.over = over;
    }
}
