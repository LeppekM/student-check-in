package gui;

public class StudentWorker extends Worker {

    public StudentWorker(String name, String email, String password) {
        super(name, email, password, false);
    }

    public String writeWorker() {
        return super.writeWorker();
    }

}
