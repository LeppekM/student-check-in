package Database.Objects;

import Database.Database;

public class Worker {

    private String name;
    private String email;
    private String pass;
    private boolean isAdmin;

    public Worker(String name, String email, String pass, boolean isAdmin){
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
