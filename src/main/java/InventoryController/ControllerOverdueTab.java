package InventoryController;

import Database.Database;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerOverdueTab  extends ControllerInventoryPage implements Initializable {

    @FXML
    TableColumn studentID, partID, serial, date, fee;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Database d = new Database();
        d.test();
        JOptionPane.showMessageDialog(null, "Done");
//        try {
//            File file = new File("testOverdue.txt");
//            FileReader fileReader = new FileReader(file);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//            String line = "";
//            while ((line = bufferedReader.readLine()) != null){
//
//            }
//            bufferedReader.close();
//        }catch (FileNotFoundException e){
//            e.printStackTrace();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
    }
}
