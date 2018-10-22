package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EditWorkerController implements Initializable {

    @FXML
    TextField workerName;

    @FXML
    TextField workerEmail;

   @FXML
   TextField isAdmin;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void isAdmin(){
        launchAdminPopup();
        isAdmin.setDisable(false);
    }



    public void save(){
//        try {
//            File file = new File("workers.txt");
//            ArrayList<String> lines = new ArrayList<>();
//            String line;
//            FileReader fr = new FileReader(file);
//            BufferedReader br = new BufferedReader(fr);
//            while ((line = br.readLine()) != null) {
//                if (line.contains(workerEmail.getCharacters())){
//                    line = workerName.getText() + ","  + workerEmail.getText();
//                }
//                lines.add(line);
//            }
//            fr.close();
//            br.close();
//
//            FileWriter fw = new FileWriter(file);
//            BufferedWriter bw = new BufferedWriter(fw);
//            for (String s : lines) {
//                bw.write(s);
//            }
//            bw.flush();
//            bw.close();
//        }catch (FileNotFoundException e){
//            Alert alert = new Alert(Alert.AlertType.ERROR, "File not Found");
//            alert.showAndWait();
//        }catch (IOException e){
//            Alert alert = new Alert(Alert.AlertType.ERROR, "Error writing to file");
//            alert.showAndWait();
//        }
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Worker List Updated");
//        alert.showAndWait();

    }

    public void launchAdminPopup(){
        try {
            Stage diffStage = new Stage();
            Pane pane = FXMLLoader.load(getClass().getResource("AdminPopup.fxml"));
            Scene scene = new Scene(pane,250, 200);
            diffStage.setScene(scene);
            diffStage.initModality(Modality.APPLICATION_MODAL);
            diffStage.setTitle("Enter Credentials");
            diffStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
