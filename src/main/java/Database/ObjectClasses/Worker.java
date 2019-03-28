package Database.ObjectClasses;

public class Worker {

    private String name;
    private int ID;
    private String email;
    private String pass;
    private int pin;
    private boolean isAdmin;
    private boolean parts;
    private boolean worker;
    private boolean student;
    private boolean over;

    public Worker(String name, int ID, String email, String pass, int pin, boolean isAdmin, boolean parts, boolean worker,
                  boolean student, boolean over){
        this.name = name;
        this.ID = ID;
        this.email = email;
        this.pass = pass;
        this.pin = pin;
        this.isAdmin = isAdmin;
        if (isAdmin){
            parts = true;
            worker = true;
            student = true;
            over = true;
        }else {
            this.parts = parts;
            this.worker = worker;
            this.student = student;
            this.over = over;
        }
    }

    public Worker(String name, String email, String pass){
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.pin = 0;
        this.isAdmin = false;
        parts = false;
        worker = false;
        student = false;
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

    public boolean isParts() {
        return parts;
    }

    public void setParts(boolean parts) {
        this.parts = parts;
    }

    public boolean isWorker() {
        return worker;
    }

    public void setWorker(boolean worker) {
        this.worker = worker;
    }

    public boolean isStudent() {
        return student;
    }

    public void setStudent(boolean student) {
        this.student = student;
    }

    public boolean isOver() {
        return over;
    }

    public void setOver(boolean over) {
        this.over = over;
    }
}
