package Popups;

import Database.ObjectClasses.Part;
import javafx.fxml.Initializable;

public abstract class EditPartController implements Initializable {

    public abstract void initPart(Part part);

    public abstract void updateItem();

}