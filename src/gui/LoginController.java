package gui;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private StackPane loginScene;

    @FXML
    private ImageView msoeBackgroundImage;

    @FXML
    private TextField usernameInputLoginPage, passwordInputLoginPage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new File("msoe.png"));
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            this.msoeBackgroundImage.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void login() {
        try {
            Worker worker = loginWorker(usernameInputLoginPage.getText(), passwordInputLoginPage.getText());
            if (worker != null) {
                Pane mainMenuPane = FXMLLoader.load(getClass().getResource("Menu.fxml"));
                loginScene.getScene().setRoot(mainMenuPane);
            }
        }
        catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
            invoke.printStackTrace();

        }
    }

    private Worker loginWorker(String email, String password) {
        Worker worker = null;
        try {
            File workersFile = new File("src/workers.txt");
            BufferedReader r = new BufferedReader(new FileReader(workersFile));
            String line;
            String[] workerLine;
            boolean isFound = false;
            while (!isFound && (line = r.readLine()) != null) {
                workerLine = line.split(",");
                if (email.equals(workerLine[1]) && password.equals(workerLine[2])) {
                    if (workerLine[2].equals("true")) {
                        worker = new Administrator(workerLine[0], workerLine[1], workerLine[2], true, workerLine[4]);
                    } else {
                        worker = new StudentWorker(workerLine[0], workerLine[1], workerLine[2]);
                    }
                    isFound = true;
                }
            }
            r.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return worker;
    }

}
