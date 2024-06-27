package HelperClasses;

import App.StudentCheckIn;
import Database.ObjectClasses.Worker;
import Controllers.IController;
import Popups.AdminPinRequestController;
import Tables.TableScreen;
import Controllers.TableScreensController;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;

/**
 * This class assists with launching AlertPanes and stages from FXML
 */
public class StageUtils {

    private static final StageUtils stageUtils = new StageUtils();

    private StageUtils() {} // enforcing Singleton pattern for utility helper class

    public static StageUtils getInstance() {
        return stageUtils;
    }

    /**
     * Helper method to make a popup
     * @param fxml Name of FXML page
     * @param node The root scene to use
     * @param title the title of the popup
     */
    public Stage createPopupStage(String fxml, Node node, String title) {
        Stage stage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxml));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.initOwner(node.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
        } catch (IOException e) {
            StudentCheckIn.logger.error("IOException: Unable to load FXML file: {}", fxml, e);
            e.printStackTrace();
        }
        return stage;
    }

    /**
     * Helper method to make popup with worker initialized
     * @param fxml Name of FXML
     * @param node The root scene to use
     * @param worker Worker name
     */
    public void newStage(String fxml, Node node, Worker worker, TableScreen tableScreen) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            IController controller = loader.getController();
            controller.initWorker(worker);
            node.getScene().setRoot(root);
            ((IController) loader.getController()).initWorker(worker);
            if (tableScreen != null) {
                ((TableScreensController) loader.getController()).setScreen(tableScreen);
                // This prevents horizontal overflow/visual errors in the table scenes
                VBox vBox = (VBox) root.lookup("#scene");
                if (vBox != null) {
                    Stage stage = (Stage) node.getScene().getWindow();
                    vBox.maxWidthProperty().bind(stage.widthProperty());
                }
            }
        } catch (IOException invoke) {
            StudentCheckIn.logger.error("No valid stage was found to load. This could likely be because of a database disconnect.");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
            invoke.printStackTrace();
        }
    }

    /**
     * Sliding alert maker
     * @param title Title of alert
     * @param content Content
     */
    public void checkoutAlert(String title, String content) {
        new Thread(() -> Platform.runLater(() -> {
            Stage owner = createAlert();
            Notifications.create().title(title).text(content).hideAfter(new Duration(2000)).show();
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event -> owner.close());
            delay.play();
        })).start();
    }

    /**
     * Sliding alert maker
     * @param title Title of alert
     * @param content Content
     */
    public void slidingAlert(String title, String content) {
        new Thread(() -> Platform.runLater(() -> {
            Stage owner = createAlert();
            Notifications.create().title(title).text(content).hideAfter(new Duration(5000)).show();
            PauseTransition delay = new PauseTransition(Duration.seconds(5));
            delay.setOnFinished(event -> owner.close());
            delay.play();
        })).start();
    }

    /**
     * Makes textField a required value
     * @param textField TextField to make required
     */
    public void requiredInputValidator(JFXTextField textField) {
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        textField.getValidators().addAll(requiredFieldValidator);
        requiredFieldValidator.setMessage("This field is required");
        requiredFieldValidator.autosize();
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                textField.validate();
            }
        });
    }

    /**
     * Only allows integers to be entered
     * @param textField TextField for change to be applied to
     */
    public void acceptIntegerOnly(TextField textField){
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }

    /**
     * Does not allow more than max characters to be entered into textField
     * @param textField TextField that the filter is applied to
     * @param max the number of characters the textField cannot surpass
     */
    public void setMaxTextLength(TextField textField, int max) {
        textField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue.length() > max) {
                String copy = newValue.substring(0, max);
                textField.setText(copy);
            }
        }));
    }

    /**
     * Error pop-up
     * @param errorText Error text
     */
    public void errorAlert(String errorText){
        Alert errorAlert2 = new Alert(Alert.AlertType.ERROR);
        errorAlert2.setHeaderText("Error");
        errorAlert2.setContentText(errorText);
        errorAlert2.initStyle(StageStyle.UTILITY);

        Platform.runLater(errorAlert2::showAndWait);
    }

    /**
     * This is the success alert that appears when editing/adding parts
     * @param successText text that is being passed to the dialog
     */
    public void successAlert(String successText){
        new Thread(() -> Platform.runLater(() -> {
            Stage owner = createAlert();
            Notifications.create().title("Successful!").text(successText).hideAfter(new Duration(5000)).show();
            PauseTransition delay = new PauseTransition(Duration.seconds(5));
            delay.setOnFinished( event -> owner.close() );
            delay.play();
        })).start();
    }

    /**
     *
     * @param stage
     */
    public void unsavedChangesAlert(Stage stage) {
        stage.setOnCloseRequest(event1 -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to close?");
            alert.setTitle("Confirm Close");
            alert.setHeaderText("If you leave now, unsaved changes could be lost.");
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.YES) {
                    stage.close();
                } else if (buttonType == ButtonType.NO) {
                    event1.consume();
                }
            });
        });
    }

    /**
     * Alert if user tries to return home and fields are filled
     * @return True if user pressed ok, false otherwise
     */
    public boolean missingFieldsAlert() {
        return confirmationAlert("Information may be lost", "If you leave, unsubmitted information may be lost");
    }

    public boolean confirmationAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(content);
        alert.setContentText("Are you ok with this?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }


    /**
     * Asks the student worker to enter an admin pin if they try to do something they do
     * not have the privilege to do
     * @param action the privileged action that the worker tried to do
     * @param node the node that the request is being launched from
     * @return true if the inputted admin pin is correct; false otherwise
     */
    public boolean requestAdminPin(String action, Node node) {
        AtomicBoolean isValid = new AtomicBoolean(false);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminPinRequest.fxml"));
            Parent root = loader.load();
            ((AdminPinRequestController) loader.getController()).setAction(action);
            Scene scene = new Scene(root, 400, 250);
            Stage stage = new Stage();
            stage.setTitle("Admin Pin Required");
            stage.initOwner(node.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("images/msoe.png"));
            stage.setResizable(false);
            stage.setOnCloseRequest(e -> {
                // checks to see whether the pin was submitted or the window was just closed
                if (((AdminPinRequestController) loader.getController()).isSubmitted()) {
                    // checks to see if the input pin is empty. if empty, close pop up
                    if (((AdminPinRequestController) loader.getController()).isNotEmpty()) {
                        // checks to see whether the submitted pin matches one of the admins' pins
                        if (((AdminPinRequestController) loader.getController()).isValid()) {
                            stage.close();
                            isValid.set(true);
                        } else {
                            stage.close();
                            errorAlert("The entered pin is invalid");
                            isValid.set(false);
                        }
                    } else {
                        stage.close();
                        isValid.set(false);
                    }
                }
            });
            stage.showAndWait();
        } catch (IOException e) {
            StudentCheckIn.logger.error("IOException: Loading Admin Pin Request.");
            e.printStackTrace();
        }
        return isValid.get();
    }


    /**
     * Clears the current scene and loads the main menu. If no menu stage was found, sends an alert to user.
     * @author Matthew Karcz
     */
    public void goBack(Pane pane, Worker worker) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Menu.fxml"));
            Parent root = loader.load();
            IController controller = loader.getController();
            controller.initWorker(worker);
            pane.getScene().setRoot(root);
            ((IController) loader.getController()).initWorker(worker);
            pane.getChildren().clear();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            StudentCheckIn.logger.error("IOException: No valid stage was found to load");
            alert.showAndWait();
        }
    }

    /**
     *
     * @return
     */
    private Stage createAlert() {
        Stage owner = new Stage(StageStyle.TRANSPARENT);
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: TRANSPARENT");
        Scene scene = new Scene(root, 1, 1);
        owner.setScene(scene);
        owner.setWidth(1);
        owner.setHeight(1);
        owner.toBack();
        owner.show();
        return owner;
    }

}

