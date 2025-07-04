import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.ImageView;
import java.util.Map;
import java.util.HashMap;

public class LoadGameScreen {
    private final Stage stage;
    private final GameState gameState;
    private final List<SaveSlot> saveSlots = new ArrayList<>();

    public LoadGameScreen(Stage stage, GameState gameState) {
        this.stage = stage;
        this.gameState = gameState;
        loadSaveSlots();
    }

    private void loadSaveSlots() {
        // Look for save files in the saves directory
        File savesDir = new File("saves");
        if (!savesDir.exists()) {
            savesDir.mkdir();
        }

        // Check for existing save files - one for each animal type
        saveSlots.clear();
        
        // Look for dog save
        File dogSave = new File("saves/dog_save.txt");
        if (dogSave.exists()) {
            saveSlots.add(new SaveSlot("Dog", "Dog", loadPetStats(dogSave)));
        } else {
            saveSlots.add(new SaveSlot("Dog", null, null));
        }
        
        // Look for cat save
        File catSave = new File("saves/cat_save.txt");
        if (catSave.exists()) {
            saveSlots.add(new SaveSlot("Cat", "Cat", loadPetStats(catSave)));
        } else {
            saveSlots.add(new SaveSlot("Cat", null, null));
        }
        
        // Look for bunny save
        File bunnySave = new File("saves/bunny_save.txt");
        if (bunnySave.exists()) {
            saveSlots.add(new SaveSlot("Bunny", "Bunny", loadPetStats(bunnySave)));
        } else {
            saveSlots.add(new SaveSlot("Bunny", null, null));
        }
    }

    private PetStats loadPetStats(File saveFile) {
        // Load actual stats from file
        String name = "Pet Name";
        int health = 80;
        int maxHealth = 100;
        int energy = 75;
        int fullness = 90;
        int happiness = 85;
        int currency = 100;
        int score = 0;
        Map<String, Integer> inventory = new HashMap<>();
        
        // Initialize default inventory
        inventory.put("Kibble", 0);
        inventory.put("Treats", 0);
        inventory.put("Premium Food", 0);
        inventory.put("Vaccine", 0);
        
        try (java.util.Scanner scanner = new java.util.Scanner(saveFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];
                    
                    switch (key) {
                        case "name" -> name = value;
                        case "health" -> health = Integer.parseInt(value);
                        case "maxHealth" -> maxHealth = Integer.parseInt(value);
                        case "energy" -> energy = Integer.parseInt(value);
                        case "fullness" -> fullness = Integer.parseInt(value);
                        case "happiness" -> happiness = Integer.parseInt(value);
                        case "currency" -> currency = Integer.parseInt(value);
                        case "score" -> score = Integer.parseInt(value);
                        case "kibble" -> inventory.put("Kibble", Integer.parseInt(value));
                        case "treats" -> inventory.put("Treats", Integer.parseInt(value));
                        case "premiumFood" -> inventory.put("Premium Food", Integer.parseInt(value));
                        case "vaccine" -> inventory.put("Vaccine", Integer.parseInt(value));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading pet stats: " + e.getMessage());
        }
        
        return new PetStats(name, health, maxHealth, energy, fullness, happiness, currency, score, inventory);
    }

    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F5F5DC;"); // Beige background

        Text title = new Text("Load Game");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-fill: #8B4513;"); // Dark brown text

        // Create a container for the save slots
        VBox saveContainer = new VBox(20);
        saveContainer.setAlignment(Pos.CENTER);
        saveContainer.setPadding(new Insets(25));
        saveContainer.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");
        saveContainer.setMaxWidth(700);

        // Create grid layout for save slots
        GridPane saveGrid = new GridPane();
        saveGrid.setHgap(20);
        saveGrid.setVgap(20);
        saveGrid.setAlignment(Pos.CENTER);

        for (int i = 0; i < saveSlots.size(); i++) {
            SaveSlot slot = saveSlots.get(i);
            VBox slotBox = createSaveSlotBox(slot);
            saveGrid.add(slotBox, i, 0);
        }

        saveContainer.getChildren().add(saveGrid);

        Button backButton = createStyledButton("Back to Main Menu");
        backButton.setStyle(backButton.getStyle().replace("#4CAF50", "#8B4513")); // Brown for back button
        backButton.setOnAction(e -> {
            MainMenuScreen mainMenu = new MainMenuScreen(stage, gameState);
            mainMenu.show();
        });

        root.getChildren().addAll(title, saveContainer, backButton);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Tamagotchi Game - Load Game");
    }

    private VBox createSaveSlotBox(SaveSlot slot) {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setMinWidth(200);
        box.setMinHeight(300);
        box.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Animal type label
        Text animalType = new Text(slot.animalType());
        animalType.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #8B4513;");

        // Create pet sprite image
        ImageView portrait = null;
        String prefix = slot.animalType().equalsIgnoreCase("Cat") ? "KITTY" : slot.animalType().toUpperCase();
        String spritePath = "images/" + prefix + "_NORMAL.png";
        
        try {
            File imageFile = new File(spritePath);
            System.out.println("Load screen - attempting to load sprite: " + spritePath);
            System.out.println("Animal type: " + slot.animalType());
            System.out.println("File exists: " + imageFile.exists());
            System.out.println("File absolute path: " + imageFile.getAbsolutePath());
            String imageURI = imageFile.toURI().toString();
            System.out.println("Image URI: " + imageURI);
            portrait = new ImageView(new javafx.scene.image.Image(imageURI));
            System.out.println("Sprite loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading sprite in load screen: " + spritePath);
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
            // Create a colored rectangle as fallback
            Rectangle fallback = new Rectangle(120, 120);
            fallback.setFill(Color.GRAY);
            portrait = new ImageView();
        }

        if (portrait != null) {
            portrait.setFitWidth(120);
            portrait.setFitHeight(120);
            portrait.setPreserveRatio(true);
        }

        // Pet name
        Text nameText = new Text(slot.name());
        nameText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Stats
        VBox statsBox = new VBox(5);
        statsBox.setAlignment(Pos.CENTER);
        
        Text healthText = new Text("Health: " + slot.health() + "/" + slot.maxHealth());
        Text energyText = new Text("Energy: " + slot.energy() + "%");
        Text fullnessText = new Text("Fullness: " + slot.fullness() + "%");
        Text happinessText = new Text("Happiness: " + slot.happiness() + "%");
        Text currencyText = new Text("Dabloons: " + slot.currency());
        Text scoreText = new Text("Score: " + slot.score());

        // Style all stat texts
        healthText.setStyle("-fx-font-size: 14px;");
        energyText.setStyle("-fx-font-size: 14px;");
        fullnessText.setStyle("-fx-font-size: 14px;");
        happinessText.setStyle("-fx-font-size: 14px;");
        currencyText.setStyle("-fx-font-size: 14px;");
        scoreText.setStyle("-fx-font-size: 14px;");

        statsBox.getChildren().addAll(healthText, energyText, fullnessText, happinessText, currencyText, scoreText);

        // Load button
        Button loadButton = createStyledButton("Load Game");
        loadButton.setOnAction(e -> loadGame(slot));

        box.getChildren().addAll(animalType, portrait, nameText, statsBox, loadButton);
        return box;
    }

    private void loadGame(SaveSlot slot) {
        // Get pet type and stats
        String petType = slot.animalType();
        PetStats stats = slot.stats();
        
        // Create a new pet with the correct name and type
        Pet pet = new Pet(stats.name(), petType);
        
        // Set pet stats
        pet.setHealth(stats.health());
        
        // Set max health if it's different from default
        if (stats.maxHealth() > 100) {
            pet.increaseMaxHealth(stats.maxHealth() - 100);
        }
        
        pet.setHappiness(stats.happiness());
        pet.setFullness(stats.fullness());
        pet.setEnergy(stats.energy());
        
        // Start the game with the loaded pet
        gameState.startNewGame(stats.name(), pet);
        
        // Set currency and score from save
        gameState.addCurrency(stats.currency() - gameState.getCurrency());
        gameState.addScore(stats.score() - gameState.getScore());
        
        // Load inventory
        for (Map.Entry<String, Integer> entry : stats.inventory().entrySet()) {
            String itemName = entry.getKey();
            int count = entry.getValue();
            // Clear existing inventory first
            while (gameState.getItemCount(itemName) > 0) {
                gameState.useItem(itemName);
            }
            // Add saved items
            gameState.addItem(itemName, count);
        }
        
        // Show the game screen
        GameScreen gameScreen = new GameScreen(stage, gameState);
        gameScreen.show();
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
    
    // Record classes for save slot data
    private record SaveSlot(String animalType, String petType, PetStats stats) {
        public String name() {
            return stats.name();
        }
        public int health() {
            return stats.health();
        }
        public int maxHealth() {
            return stats.maxHealth();
        }
        public int energy() {
            return stats.energy();
        }
        public int fullness() {
            return stats.fullness();
        }
        public int happiness() {
            return stats.happiness();
        }
        public int currency() {
            return stats.currency();
        }
        public int score() {
            return stats.score();
        }
    }
    
    private record PetStats(String name, int health, int maxHealth, int energy, int fullness, int happiness, int currency, int score,
                          Map<String, Integer> inventory) {}
} 