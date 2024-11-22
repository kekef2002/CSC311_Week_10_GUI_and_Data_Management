package viewmodel;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import service.UserSession;
import javafx.fxml.FXML;

public class SignUpController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    public void createNewAccount(ActionEvent actionEvent) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Username and password cannot be blank!");
            alert.showAndWait();
            return;
        }

        try {
            // Store user credentials in Preferences and initialize UserSession
            UserSession.getInstance(username, password, "USER");

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Account created successfully!");
            alert.showAndWait();

            // Redirect to login page or dashboard (if needed)
            goBack(actionEvent);
        } catch (Exception e) {
            // Handle any errors
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error creating account: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void goBack(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
