import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.scene.image.ImageView;

/**
 * Screen for creating a new game, allowing the player to select and name their pet.
 */
public class NewGameScreen {
    private final Stage stage;
    private final GameState gameState;
    private Scene scene;
    private ToggleButton dogButton;
    private ToggleButton catButton;
    private ToggleButton bunnyButton;
    private TextField nameField;
    private Button startButton;
    private String selectedPetType = null;

    public NewGameScreen(Stage stage, GameState gameState) {
        this.stage = stage;
        this.gameState = gameState;
        createScene();
    }

    public NewGameScreen(Stage stage, GameState gameState, String preselectedAnimal) {
        this.stage = stage;
        this.gameState = gameState;
        this.selectedPetType = preselectedAnimal;
        createScene();
    }

    private void createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F5F5DC;");

        // Title
        Text title = new Text("New Game");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");

        // Pet selection
        VBox selectionBox = new VBox(15);
        selectionBox.setAlignment(Pos.CENTER);

        Text subtitle = new Text("Choose Your Pet");
        subtitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Pet selection buttons in a horizontal layout
        HBox petSelection = new HBox(30);
        petSelection.setAlignment(Pos.CENTER);

        // Create toggle group for pet selection
        ToggleGroup petGroup = new ToggleGroup();

        // Create pet buttons
        dogButton = createPetToggleButton("Dog", "Brown", petGroup);
        catButton = createPetToggleButton("Cat", "Gray", petGroup);
        bunnyButton = createPetToggleButton("Bunny", "LightGray", petGroup);

        // If a pet type was preselected, select that button
        if (selectedPetType != null) {
            switch (selectedPetType) {
                case "Dog" -> dogButton.setSelected(true);
                case "Cat" -> catButton.setSelected(true);
                case "Bunny" -> bunnyButton.setSelected(true);
            }
        }

        petSelection.getChildren().addAll(dogButton, catButton, bunnyButton);

        // Pet name input
        HBox nameBox = new HBox(10);
        nameBox.setAlignment(Pos.CENTER);
        
        Label nameLabel = new Label("Pet Name:");
        nameLabel.setStyle("-fx-font-size: 16px;");
        
        nameField = new TextField();
        nameField.setPrefWidth(200);
        nameField.setPromptText("Enter a name for your pet");

        nameBox.getChildren().addAll(nameLabel, nameField);

        // Start button
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        startButton = createStyledButton("Start Game");
        startButton.setDisable(true);
        startButton.setOnAction(e -> startGame());

        // Back button
        Button backButton = createStyledButton("Back to Main Menu");
        backButton.setStyle(backButton.getStyle().replace("#4CAF50", "#9E9E9E"));
        backButton.setOnAction(e -> {
            MainMenuScreen mainMenu = new MainMenuScreen(stage, gameState);
            mainMenu.show();
        });

        buttonBox.getChildren().addAll(startButton, backButton);

        // Add listeners for input validation
        petGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedPetType = ((ToggleButton) newVal).getText();
                validateInputs();
            } else {
                selectedPetType = null;
                validateInputs();
            }
        });

        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateInputs());

        selectionBox.getChildren().addAll(subtitle, petSelection, nameBox);
        root.getChildren().addAll(title, selectionBox, buttonBox);

        scene = new Scene(root, 800, 600);
    }

    private ToggleButton createPetToggleButton(String petType, String color, ToggleGroup group) {
        VBox buttonContent = new VBox(10);
        buttonContent.setAlignment(Pos.CENTER);
        
        // Create ImageView for the pet sprite
        String spritePath = "images/" + (petType.equals("Cat") ? "KITTY" : petType.toUpperCase()) + "_NORMAL.png";
        ImageView petImage = new ImageView(new File(spritePath).toURI().toString());
        petImage.setFitWidth(100);
        petImage.setFitHeight(100);
        petImage.setPreserveRatio(true);
        petImage.setSmooth(true);

        // Pet type label
        Label petLabel = new Label(petType);
        petLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        buttonContent.getChildren().addAll(petImage, petLabel);
        
        // Create toggle button
        ToggleButton button = new ToggleButton();
        button.setGraphic(buttonContent);
        button.setText(petType); // Store pet type in the button text
        button.setContentDisplay(ContentDisplay.TOP);
        button.setToggleGroup(group);
        button.setPrefSize(150, 180);
        button.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #E0E0E0;
            -fx-border-radius: 5;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """);

        // Add hover effect
        button.setOnMouseEntered(e -> 
            button.setStyle(button.getStyle() + "-fx-border-color: #4CAF50; -fx-border-width: 2;"));
        button.setOnMouseExited(e -> 
            button.setStyle(button.getStyle().replace("-fx-border-color: #4CAF50; -fx-border-width: 2;", 
                                                    "-fx-border-color: #E0E0E0;")));

        // Add selected style
        button.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                button.setStyle(button.getStyle() + "-fx-border-color: #4CAF50; -fx-border-width: 3;");
            } else {
                button.setStyle(button.getStyle().replace("-fx-border-color: #4CAF50; -fx-border-width: 3;", 
                                                        "-fx-border-color: #E0E0E0;"));
            }
        });
        
        return button;
    }

    private void validateInputs() {
        boolean isValid = selectedPetType != null && !nameField.getText().trim().isEmpty();
        startButton.setDisable(!isValid);
    }

    private void startGame() {
        String petName = nameField.getText().trim();
        
        if (selectedPetType == null || petName.isEmpty()) {
            showError("Please select a pet type and enter a name.");
            return;
        }
        
        // Create the pet based on selected type
        Pet pet = new Pet(petName, selectedPetType);
        
        // Initialize pet with default stats
        pet.setHealth(100);
        pet.setHappiness(100);
        pet.setFullness(100);
        pet.setEnergy(100);
        
        // Start the game in GameState with the player's name (using pet name for now)
        gameState.startNewGame(petName, pet);
        
        // Save the game to a file
        saveGame(pet);
        
        // Show the game screen
        GameScreen gameScreen = new GameScreen(stage, gameState);
        gameScreen.show();
    }
    
    private void saveGame(Pet pet) {
        // Create saves directory if it doesn't exist
        File savesDir = new File("saves");
        if (!savesDir.exists()) {
            savesDir.mkdir();
        }
        
        // Create a save file based on pet type
        String filename = "saves/" + pet.getType().toLowerCase() + "_save.txt";
        try (FileWriter writer = new FileWriter(filename)) {
            // Write pet information
            writer.write("name=" + pet.getName() + "\n");
            writer.write("type=" + pet.getType() + "\n");
            writer.write("health=" + pet.getHealth() + "\n");
            writer.write("happiness=" + pet.getHappiness() + "\n");
            writer.write("fullness=" + pet.getFullness() + "\n");
            writer.write("energy=" + pet.getEnergy() + "\n");
            System.out.println("Game saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
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
        return button;
    }

    public void show() {
        stage.setScene(scene);
    }
} 