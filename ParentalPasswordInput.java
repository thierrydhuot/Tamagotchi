import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ParentalPasswordInput {
    private final Stage stage;
    private final GameState gameState;
    private Scene scene;
    private final Runnable onSuccess;

    public ParentalPasswordInput(Stage stage, GameState gameState, Runnable onSuccess) {
        this.stage = stage;
        this.gameState = gameState;
        this.onSuccess = onSuccess;
        createScene();
    }

    private void createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #F5F5DC; -fx-background-insets: 0;");

        Text title = new Text("Enter Parental Password");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setStyle("-fx-fill: #8B4513;"); // Dark brown text

        VBox passwordBox = createPasswordSection();
        passwordBox.setStyle("-fx-background-color: #F5F5DC; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-insets: 0;");

        // Navigation buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button backButton = createStyledButton("Back to Main Menu");
        backButton.setStyle(backButton.getStyle().replace("#4CAF50", "#8B4513")); // Brown for back button
        backButton.setOnAction(e -> {
            MainMenuScreen mainMenu = new MainMenuScreen(stage, gameState);
            mainMenu.show();
        });

        buttonBox.getChildren().add(backButton);

        // Add all sections to root
        root.getChildren().addAll(title, passwordBox, buttonBox);

        scene = new Scene(root, 600, 400);
        scene.getRoot().setStyle("-fx-background-color: #F5F5DC;");
    }

    private VBox createPasswordSection() {
        VBox passwordBox = new VBox(15);
        passwordBox.setAlignment(Pos.CENTER);
        passwordBox.setPadding(new Insets(25));
        passwordBox.setMaxWidth(400);
        passwordBox.setStyle("-fx-background-color: #F5F5DC; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-insets: 0;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(300);
        passwordField.setStyle("""
            -fx-font-size: 14px;
            -fx-padding: 10px;
            -fx-background-radius: 5;
            -fx-border-color: #8B4513;
            -fx-border-radius: 5;
            """);

        Button verifyButton = createStyledButton("Verify Password");
        verifyButton.setOnAction(e -> {
            if (gameState.verifyParentalPassword(passwordField.getText())) {
                onSuccess.run();
            } else {
                showAlert("Error", "Incorrect password!");
                passwordField.clear();
            }
        });

        // Add enter key handler
        passwordField.setOnAction(e -> verifyButton.fire());

        passwordBox.getChildren().addAll(passwordField, verifyButton);

        return passwordBox;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("""
            -fx-background-color: #4CAF50;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-padding: 10px 20px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            -fx-min-width: 150px;
            """);
        
        // Add hover effect
        button.setOnMouseEntered(e -> button.setStyle("""
            -fx-background-color: #45a049;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-padding: 10px 20px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            -fx-min-width: 150px;
            """));
        
        button.setOnMouseExited(e -> button.setStyle("""
            -fx-background-color: #4CAF50;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-padding: 10px 20px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            -fx-min-width: 150px;
            """));
        
        return button;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void show() {
        stage.setScene(scene);
        stage.setTitle("Parental Password");
        stage.setMinWidth(400);
        stage.setMinHeight(300);
    }
} 