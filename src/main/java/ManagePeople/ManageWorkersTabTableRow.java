package ManagePeople;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ManageWorkersTabTableRow extends RecursiveTreeObject<ManageWorkersTabTableRow> {

    private StringProperty name;
    private StringProperty email;
    private BooleanProperty isAdmin;

    public ManageWorkersTabTableRow(String name, String email, boolean isAdmin) {
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.isAdmin = new SimpleBooleanProperty(isAdmin);
    }

    public StringProperty getName() {
        return name;
    }

    public StringProperty getEmail() {
        return email;
    }

    public BooleanProperty getIsAdmin() {
        return isAdmin;
    }
}
