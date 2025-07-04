import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.time.LocalTime;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;

public class ParentalSettingsScreen {
    private final Stage stage;
    private final GameState gameState;
    private Scene scene;

    public ParentalSettingsScreen(Stage stage, GameState gameState) {
        this.stage = stage;
        this.gameState = gameState;
        createScene();
    }

    private void createScene() {
        // Create a ScrollPane as the root container
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #F5F5DC; -fx-background-color: #F5F5DC;");

        VBox root = new VBox(30);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #F5F5DC;"); // Beige background

        Text title = new Text("Parental Controls");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setStyle("-fx-fill: #8B4513;"); // Dark brown text

        // Time settings section (combines daily limit and allowed times)
        VBox timeSettingsBox = createTimeSection();

        // Game statistics section
        VBox statsSection = createStatsSection();

        // Navigation buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 40, 0));

        Button saveButton = createStyledButton("Save Settings");
        saveButton.setOnAction(e -> {
            saveSettings();
            showSuccessDialog();
        });

        Button backButton = createStyledButton("Back to Main Menu");
        backButton.setStyle(backButton.getStyle().replace("#4CAF50", "#8B4513")); // Brown for back button
        backButton.setOnAction(e -> {
            MainMenuScreen mainMenu = new MainMenuScreen(stage, gameState);
            mainMenu.show();
        });

        buttonBox.getChildren().addAll(saveButton, backButton);

        // Add all sections to root
        root.getChildren().addAll(
            title,
            timeSettingsBox,
            statsSection,
            buttonBox
        );

        // Set the VBox as the content of ScrollPane
        scrollPane.setContent(root);

        // Create the scene with the ScrollPane
        scene = new Scene(scrollPane, 800, Math.min(700, Screen.getPrimary().getVisualBounds().getHeight() * 0.9));
    }

    private VBox createTimeSection() {
        VBox timeBox = new VBox(20);
        timeBox.setAlignment(Pos.CENTER);
        timeBox.setPadding(new Insets(25));
        timeBox.setMaxWidth(500);
        timeBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");

        Text timeTitle = new Text("Time Restrictions");
        timeTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        timeTitle.setStyle("-fx-fill: #8B4513;");

        // Daily time limit
        VBox dailyLimitBox = new VBox(10);
        dailyLimitBox.setAlignment(Pos.CENTER);
        
        Text dailyLimitLabel = new Text("Daily Time Limit (hours):");
        dailyLimitLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        
        Spinner<Integer> dailyLimitSpinner = new Spinner<>(1, 24, gameState.getDailyTimeLimit());
        dailyLimitSpinner.setEditable(true);
        dailyLimitSpinner.setPrefWidth(100);
        dailyLimitSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            gameState.setDailyTimeLimit(newVal);
        });

        HBox spinnerBox = new HBox(10);
        spinnerBox.setAlignment(Pos.CENTER);
        spinnerBox.getChildren().addAll(dailyLimitLabel, dailyLimitSpinner);

        // Allowed play times
        Text playTimesLabel = new Text("Allowed Play Times:");
        playTimesLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        
        HBox timePickerBox = new HBox(20);
        timePickerBox.setAlignment(Pos.CENTER);
        timePickerBox.setPadding(new Insets(10, 0, 0, 0));
        
        ComboBox<String> startTime = new ComboBox<>();
        ComboBox<String> endTime = new ComboBox<>();
        startTime.setPrefWidth(100);
        endTime.setPrefWidth(100);
        
        // Populate time options (24-hour format)
        for (int hour = 0; hour < 24; hour++) {
            String time = String.format("%02d:00", hour);
            startTime.getItems().add(time);
            endTime.getItems().add(time);
        }
        
        startTime.setValue(formatTime(gameState.getAllowedStartTime()));
        endTime.setValue(formatTime(gameState.getAllowedEndTime()));
        
        startTime.setOnAction(e -> gameState.setAllowedStartTime(parseTime(startTime.getValue())));
        endTime.setOnAction(e -> gameState.setAllowedEndTime(parseTime(endTime.getValue())));
        
        VBox startBox = new VBox(5);
        startBox.setAlignment(Pos.CENTER);
        Text fromText = new Text("From:");
        fromText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        startBox.getChildren().addAll(fromText, startTime);

        VBox endBox = new VBox(5);
        endBox.setAlignment(Pos.CENTER);
        Text toText = new Text("To:");
        toText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        endBox.getChildren().addAll(toText, endTime);

        timePickerBox.getChildren().addAll(startBox, endBox);

        timeBox.getChildren().addAll(
            timeTitle,
            new Separator(),
            spinnerBox,
            new Separator(),
            playTimesLabel,
            timePickerBox
        );

        return timeBox;
    }

    private VBox createStatsSection() {
        VBox statsBox = new VBox(15);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(25));
        statsBox.setMaxWidth(500);
        statsBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");

        Text statsTitle = new Text("Game Statistics");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        statsTitle.setStyle("-fx-fill: #8B4513;");

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(10);
        statsGrid.setAlignment(Pos.CENTER);

        // Calculate time played
        long totalMinutesPlayed = gameState.getTotalPlayTime() / (1000 * 60);
        String timePlayedStr = String.format("%d hours, %d minutes", 
            totalMinutesPlayed / 60, totalMinutesPlayed % 60);

        addStatRow(statsGrid, 0, "Total Time Played:", timePlayedStr);
        addStatRow(statsGrid, 1, "Current Score:", String.valueOf(gameState.getScore()));
        addStatRow(statsGrid, 2, "Current Dabloons:", String.valueOf(gameState.getCurrency()));
        addStatRow(statsGrid, 3, "Average Health:", String.format("%.1f", gameState.getAverageHealth()));
        addStatRow(statsGrid, 4, "Total Feedings:", String.valueOf(gameState.getTotalFeedings()));
        addStatRow(statsGrid, 5, "Play Sessions:", String.valueOf(gameState.getTotalPlaySessions()));
        addStatRow(statsGrid, 6, "Vet Visits:", String.valueOf(gameState.getTotalVetVisits()));

        statsBox.getChildren().addAll(statsTitle, new Separator(), statsGrid);

        return statsBox;
    }

    private void addStatRow(GridPane grid, int row, String label, String value) {
        Text labelText = new Text(label);
        Text valueText = new Text(value);
        labelText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        valueText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(labelText, 0, row);
        grid.add(valueText, 1, row);
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
        return button;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessDialog() {
        showAlert("Success", "Settings have been saved successfully!");
    }

    private String formatTime(LocalTime time) {
        return String.format("%02d:00", time.getHour());
    }

    private LocalTime parseTime(String timeStr) {
        String[] parts = timeStr.split(":");
        return LocalTime.of(Integer.parseInt(parts[0]), 0);
    }

    private void saveSettings() {
        // Save all settings to file
        gameState.saveToFile("game_state.txt");
        showSuccessDialog();
    }

    public void show() {
        stage.setScene(scene);
        stage.setTitle("Parental Controls");
        
        // Set minimum dimensions for the window
        stage.setMinWidth(600);
        stage.setMinHeight(400);
    }
} 