package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.StringProperty;

public class ManageStudentsTabTableRow extends RecursiveTreeObject<ManageStudentsTabTableRow> {

    private StringProperty name;
    private StringProperty id;
    private StringProperty email;

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