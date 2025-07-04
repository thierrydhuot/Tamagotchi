import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SettingsScreen {
    private final Stage stage;
    private final GameState gameState;
    private Scene scene;
    private Slider volumeSlider;
    private RadioButton windowedButton;
    private RadioButton fullscreenButton;
    private Slider masterSlider;
    private Slider sfxSlider;
    private Slider musicSlider;

    public SettingsScreen(Stage stage, GameState gameState) {
        this.stage = stage;
        this.gameState = gameState;
        createScene();
    }

    private void createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F5F5DC;");

        Text title = new Text("Settings");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-fill: #8B4513;");

        // Volume settings
        VBox volumeBox = createVolumeSettings();
        
        // Display settings
        VBox displayBox = createDisplaySettings();

        // Navigation buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button saveButton = createStyledButton("Save Settings");
        saveButton.setOnAction(e -> {
            saveSettings();
            applySettings();
            showSuccessDialog();
        });

        Button backButton = createStyledButton("Back to Main Menu");
        backButton.setStyle(backButton.getStyle().replace("#4CAF50", "#8B4513"));
        backButton.setOnAction(e -> {
            MainMenuScreen mainMenu = new MainMenuScreen(stage, gameState);
            mainMenu.show();
        });

        buttonBox.getChildren().addAll(saveButton, backButton);

        root.getChildren().addAll(
            title,
            volumeBox,
            displayBox,
            buttonBox
        );

        scene = new Scene(root, 800, 600);
        stage.setScene(scene);
    }

    private VBox createVolumeSettings() {
        VBox volumeBox = new VBox(15);
        volumeBox.setAlignment(Pos.CENTER);
        volumeBox.setPadding(new Insets(25));
        volumeBox.setMaxWidth(500);
        volumeBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");

        Text volumeTitle = new Text("Volume Settings");
        volumeTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        volumeTitle.setStyle("-fx-fill: #8B4513;");

        // Single volume slider
        HBox volumeControl = new HBox(10);
        volumeControl.setAlignment(Pos.CENTER);
        Label volumeLabel = new Label("Master Volume:");
        volumeLabel.setStyle("-fx-font-size: 16px;");
        volumeSlider = new Slider(0, 100, gameState.getMasterVolume());
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(20);
        volumeSlider.setBlockIncrement(10);
        
        Text volumeValue = new Text(String.format("%.0f%%", volumeSlider.getValue()));
        volumeValue.setStyle("-fx-font-size: 16px;");
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            volumeValue.setText(String.format("%.0f%%", newVal.doubleValue()));
        });

        volumeControl.getChildren().addAll(volumeLabel, volumeSlider, volumeValue);
        volumeBox.getChildren().addAll(volumeTitle, new Separator(), volumeControl);

        return volumeBox;
    }

    private VBox createDisplaySettings() {
        VBox displayBox = new VBox(15);
        displayBox.setAlignment(Pos.CENTER);
        displayBox.setPadding(new Insets(25));
        displayBox.setMaxWidth(500);
        displayBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");

        Text displayTitle = new Text("Display Settings");
        displayTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        displayTitle.setStyle("-fx-fill: #8B4513;");

        // Display mode selection
        VBox displayMode = new VBox(10);
        displayMode.setAlignment(Pos.CENTER);
        Text displayModeTitle = new Text("Display Mode:");
        displayModeTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        displayModeTitle.setStyle("-fx-fill: #8B4513;");

        ToggleGroup displayGroup = new ToggleGroup();
        windowedButton = new RadioButton("Windowed");
        fullscreenButton = new RadioButton("Fullscreen");
        windowedButton.setToggleGroup(displayGroup);
        fullscreenButton.setToggleGroup(displayGroup);
        windowedButton.setStyle("-fx-font-size: 16px;");
        fullscreenButton.setStyle("-fx-font-size: 16px;");

        // Set initial selection based on current state
        if (stage.isFullScreen()) {
            fullscreenButton.setSelected(true);
        } else {
            windowedButton.setSelected(true);
        }

        // Add listeners to handle display mode changes
        windowedButton.setOnAction(e -> {
            stage.setFullScreen(false);
            gameState.setFullscreen(false);
        });

        fullscreenButton.setOnAction(e -> {
            stage.setFullScreen(true);
            gameState.setFullscreen(true);
        });

        displayMode.getChildren().addAll(displayModeTitle, windowedButton, fullscreenButton);
        displayBox.getChildren().addAll(displayTitle, new Separator(), displayMode);

        return displayBox;
    }

    private void saveSettings() {
        // Save volume settings
        gameState.setMasterVolume((int) volumeSlider.getValue());
        
        // Show success dialog
        showSuccessDialog();
    }
    
    private void applySettings() {
        // Apply fullscreen setting immediately
        stage.setFullScreen(gameState.isFullscreen());
    }

    private void showSuccessDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Settings have been saved successfully!");
        alert.showAndWait();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("""
            -fx-background-color: #4CAF50;
            -fx-text-fill: white;
            -fx-font-size: 16px;
            -fx-padding: 10px 20px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            -fx-min-width: 150px;
            """);
        
        // Add hover effect
        button.setOnMouseEntered(e -> button.setStyle("""
            -fx-background-color: #45a049;
            -fx-text-fill: white;
            -fx-font-size: 16px;
            -fx-padding: 10px 20px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            -fx-min-width: 150px;
            """));
        
        button.setOnMouseExited(e -> button.setStyle("""
            -fx-background-color: #4CAF50;
            -fx-text-fill: white;
            -fx-font-size: 16px;
            -fx-padding: 10px 20px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            -fx-min-width: 150px;
            """));
        
        return button;
    }

    public void show() {
        stage.setScene(scene);
        stage.setTitle("Settings");
    }
} 