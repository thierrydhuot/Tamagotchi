import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;

/**
 * The main menu screen of the game.
 */
public class MainMenuScreen {
    private final Stage stage;
    private final GameState gameState;

    public MainMenuScreen(Stage stage, GameState gameState) {
        this.stage = stage;
        this.gameState = gameState;
        
        // Ensure game starts in windowed mode
        stage.setFullScreen(false);
        gameState.setFullscreen(false);
    }

    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F5F5DC;"); // Beige background

        Text title = new Text("Tamagotchi Game");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-fill: #8B4513;"); // Dark brown text

        // Create a container for the buttons
        VBox buttonContainer = new VBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(20));
        buttonContainer.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");
        buttonContainer.setMaxWidth(400);

        Button newGameButton = createStyledButton("New Game");
        newGameButton.setOnAction(e -> {
            NewGameScreen newGameScreen = new NewGameScreen(stage, gameState);
            newGameScreen.show();
        });

        Button loadGameButton = createStyledButton("Load Game");
        loadGameButton.setOnAction(e -> {
            LoadGameScreen loadGameScreen = new LoadGameScreen(stage, gameState);
            loadGameScreen.show();
        });
        
        Button settingsButton = createStyledButton("Settings");
        settingsButton.setOnAction(e -> {
            SettingsScreen settingsScreen = new SettingsScreen(stage, gameState);
            settingsScreen.show();
        });

        Button parentalControlsButton = createStyledButton("Parental Controls");
        parentalControlsButton.setOnAction(e -> {
            ParentalControlsScreen controlsScreen = new ParentalControlsScreen(stage, gameState);
            controlsScreen.show();
        });

        Button exitButton = createStyledButton("Exit");
        exitButton.setOnAction(e -> stage.close());
        exitButton.setStyle("""
            -fx-background-color: #F44336;
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-padding: 10px 20px;
            -fx-min-width: 200px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """);
        
        // Add hover effect for exit button
        exitButton.setOnMouseEntered(e -> exitButton.setStyle("""
            -fx-background-color: #D32F2F;
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-padding: 10px 20px;
            -fx-min-width: 200px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """));
        
        exitButton.setOnMouseExited(e -> exitButton.setStyle("""
            -fx-background-color: #F44336;
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-padding: 10px 20px;
            -fx-min-width: 200px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """));

        buttonContainer.getChildren().addAll(
            newGameButton, 
            loadGameButton, 
            settingsButton,
            parentalControlsButton,
            exitButton
        );

        root.getChildren().addAll(title, buttonContainer);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Tamagotchi Game - Main Menu");
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("""
            -fx-background-color: #4CAF50;
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-padding: 10px 20px;
            -fx-min-width: 200px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """);
        
        // Add hover effect
        button.setOnMouseEntered(e -> button.setStyle("""
            -fx-background-color: #45a049;
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-padding: 10px 20px;
            -fx-min-width: 200px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """));
        
        button.setOnMouseExited(e -> button.setStyle("""
            -fx-background-color: #4CAF50;
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-padding: 10px 20px;
            -fx-min-width: 200px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """));
        
        return button;
    }
} 