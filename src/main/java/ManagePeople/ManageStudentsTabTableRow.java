package ManagePeople;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ManageStudentsTabTableRow extends RecursiveTreeObject<ManageStudentsTabTableRow> {

    private StringProperty firstName, lastName;
    private StringProperty id;
    private StringProperty email;

    public ManageStudentsTabTableRow(String firstName, String lastName, String id, String email) {
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.id = new SimpleStringProperty(id);
        this.email = new SimpleStringProperty(email);
    }

    public StringProperty getFirstName() {
        return firstName;
    }

    public StringProperty getLastName() {
        return lastName;
    }

    public StringProperty getId() {
        return id;
    }

    public StringProperty getEmail() {
        return email;
    }

}