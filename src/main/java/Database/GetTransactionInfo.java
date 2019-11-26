package Database;

import Database.ObjectClasses.Part;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Statement;

public class GetTransactionInfo {

    private final String url = Database.host + "/student_check_in";

    private Statement statement;

    public ObservableList<Part> data = FXCollections.observableArrayList();

    public void getLastTransactionInfo(){

    }
}
