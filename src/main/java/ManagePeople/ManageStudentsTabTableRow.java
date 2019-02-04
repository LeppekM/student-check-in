package ManagePeople;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ManageStudentsTabTableRow extends RecursiveTreeObject<ManageStudentsTabTableRow> {

    private StringProperty name;
    private StringProperty id;
    private StringProperty email;

    public ManageStudentsTabTableRow(String name, String id, String email) {
        this.name = new SimpleStringProperty(name);
        this.id = new SimpleStringProperty(id);
        this.email = new SimpleStringProperty(email);
    }

    public StringProperty getName() {
        return name;
    }

    public StringProperty getId() {
        return id;
    }

    public StringProperty getEmail() {
        return email;
    }

}