package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
    private Button cancel;

    @FXML
    TextField workerName;

    @FXML
    TextField workerEmail;

   @FXML
   TextField isAdmin;

   @FXML
    CheckBox adminCheckBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {



    }

    EditWorkerController(String[] worker){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditWorker.fxml"));
            loader.setController(this);
            Pane pane = loader.load();
            Scene scene = new Scene(pane);
            initialize(loader.getLocation(), loader.getResources());
            workerName.setText(worker[0]);
            workerEmail.setText(worker[1]);
            if(worker[2].equals("Administrator")){
                adminCheckBox.setSelected(true);
                isAdmin.setText(worker[3]);
            }
            Stage diff = new Stage();
            diff.setScene(scene);
            diff.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void launchView(){
        try {
            Stage diffStage = new Stage();
            Pane pane = FXMLLoader.load(getClass().getResource("EditWorker.fxml"));
            Scene scene = new Scene(pane,250, 200);
            diffStage.setScene(scene);
            diffStage.initModality(Modality.APPLICATION_MODAL);
            diffStage.setTitle("Enter Credentials");
            diffStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public void isAdmin(){
        launchAdminPopup();
        isAdmin.setDisable(false);
    }



    public void save(){
        try {
            File file = new File("workers.txt");
            ArrayList<String> lines = new ArrayList<>();
            String line;
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                String[]split = line.split(",");
                if( split[1].equalsIgnoreCase(workerEmail.getText()) && split[2].equalsIgnoreCase("false")){
                    line = workerName.getText() + "," + workerEmail.getText() + ","+ "false";
                }
                if (split[2].equalsIgnoreCase("true") & split[1].equalsIgnoreCase(workerEmail.getText())){
                    line = workerName.getText() + "," + workerEmail.getText() + "," + "true" + "," + split[3];
                }
                lines.add(line);

            }
           // System.out.println(workerName.getText());
            System.out.println(lines);
            fr.close();
            br.close();

//            FileWriter fw = new FileWriter(file);
//            BufferedWriter bw = new BufferedWriter(fw);
//            for (String s : lines) {
//                bw.write(s);
//            }
//            bw.flush();
//            bw.close();
        }catch (FileNotFoundException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "File not Found");
            alert.showAndWait();
        }catch (IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error writing to file");
            alert.showAndWait();
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Worker List Updated");
        alert.showAndWait();

    }






}
