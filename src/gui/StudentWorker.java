package gui;

public class StudentWorker extends Worker {

    public StudentWorker(String name, String email) {
        super(name, email, false);
    }

    public String writeWorker() {
        return super.writeWorker();
    }

}
