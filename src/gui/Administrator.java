package gui;

public class Administrator extends Worker {

    String adminPin;

    public Administrator(String name, String email, boolean isAdmin, String adminPin) {
        super(name, email, isAdmin);
        this.adminPin = adminPin;
    }

    public String writeWorker() {
        return super.writeWorker() + "," + adminPin;
    }

}
