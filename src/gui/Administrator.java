package gui;

public class Administrator extends Worker {

    String adminPin;

    public Administrator(String name, String email,String password, boolean isAdmin, String adminPin) {
        super(name, email, password, isAdmin);
        this.adminPin = adminPin;
    }

    public String writeWorker() {
        return super.writeWorker() + "," + adminPin;
    }

}
