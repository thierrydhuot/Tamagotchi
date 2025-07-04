import javafx.animation.AnimationTimer;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.util.Random;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * The GameScreen class handles the main gameplay screen where the player interacts with their pet.
 */
public class GameScreen {
    private Stage stage;
    private GameState gameState;
    private Pet pet;
    private Scene scene;
    private Stage dialog;
    
    // UI elements
    private Text petNameText;
    private Text healthText;
    private Text happinessText;
    private Text fullnessText;
    private Text energyText;
    private ProgressBar healthBar;
    private ProgressBar happinessBar;
    private ProgressBar fullnessBar;
    private ProgressBar energyBar;
    private ImageView petSprite;
    private Text scoreText;
    private Text currencyText;
    
    // Game loop for periodic updates
    private AnimationTimer gameLoop;
    private long lastUpdate = 0;
    
    private VBox root;
    private VBox centerBox;
    
    public GameScreen(Stage stage, GameState gameState) {
        this.stage = stage;
        this.gameState = gameState;
        this.pet = gameState.getPet();
        createScene();
    }
    
    private void createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F5DC;");

        // Create the top bar with pet name and stats
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        // Create center area with pet sprite and stats
        HBox centerArea = new HBox(0);
        centerArea.setAlignment(Pos.CENTER);
        
        // Create inventory display (left side)
        VBox inventoryBox = createInventoryDisplay();
        inventoryBox.setPrefWidth(250);
        inventoryBox.setMaxWidth(250);
        
        // Create pet sprite area (center)
        VBox petArea = new VBox(10);
        petArea.setAlignment(Pos.CENTER);
        petArea.setPadding(new Insets(20));
        petArea.setStyle("-fx-background-color: #FAFAD2;");
        HBox.setHgrow(petArea, Priority.ALWAYS);
        
        // Create pet sprite
        String prefix = pet.getType().equalsIgnoreCase("Cat") ? "KITTY" : pet.getType().toUpperCase();
        String spritePath = "images/" + prefix + "_NORMAL.png";
        try {
            File imageFile = new File(spritePath);
            System.out.println("Initial sprite loading: " + spritePath);
            System.out.println("Pet type: " + pet.getType());
            System.out.println("File exists: " + imageFile.exists());
            System.out.println("File absolute path: " + imageFile.getAbsolutePath());
            String imageURI = imageFile.toURI().toString();
            System.out.println("Image URI: " + imageURI);
            javafx.scene.image.Image image = new javafx.scene.image.Image(imageURI);
            petSprite = new ImageView(image);
            petSprite.setFitWidth(200);
            petSprite.setFitHeight(200);
            petSprite.setPreserveRatio(true);
            petSprite.setSmooth(true);
            
            // Add idle animation
            Timeline idleAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, 
                    new KeyValue(petSprite.translateYProperty(), 0)
                ),
                new KeyFrame(Duration.seconds(1), 
                    new KeyValue(petSprite.translateYProperty(), -10)
                ),
                new KeyFrame(Duration.seconds(2), 
                    new KeyValue(petSprite.translateYProperty(), 0)
                )
            );
            idleAnimation.setCycleCount(Timeline.INDEFINITE);
            idleAnimation.play();
            
            petArea.getChildren().add(petSprite);
            System.out.println("Initial sprite loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading initial sprite: " + spritePath);
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
            // Create a colored rectangle as fallback
            Rectangle fallback = new Rectangle(200, 200);
            fallback.setFill(getPetColor());
            petArea.getChildren().add(fallback);
        }
        
        // Create stats display (right side)
        VBox statsBox = createStatsDisplay();
        statsBox.setPrefWidth(250);
        statsBox.setMaxWidth(250);
        
        centerArea.getChildren().addAll(inventoryBox, petArea, statsBox);
        root.setCenter(centerArea);

        // Create bottom bar with buttons
        HBox bottomBar = createControls();
        root.setBottom(bottomBar);

        scene = new Scene(root, 800, 600);
        
        // Setup keyboard shortcuts
        setupKeyboardShortcuts();
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #D2B48C;");

        // Pet name display
        petNameText = new Text(pet.getName());
        petNameText.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Score display
        scoreText = new Text("Score: " + gameState.getScore());
        scoreText.setFont(Font.font("Arial", 18));

        // Currency display
        currencyText = new Text("Dabloons: " + gameState.getCurrency());
        currencyText.setFont(Font.font("Arial", 18));
        
        // Add a save button
        Button saveButton = createStyledButton("Save Game");
        saveButton.setOnAction(e -> saveGame());
        
        // Add a back button
        Button backButton = createStyledButton("Back to Menu");
        backButton.setOnAction(e -> {
            MainMenuScreen mainMenuScreen = new MainMenuScreen(stage, gameState);
            mainMenuScreen.show();
        });

        // Adding spacer to push save button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(petNameText, scoreText, currencyText, spacer, saveButton, backButton);
        return topBar;
    }

    private VBox createInventoryDisplay() {
        VBox inventoryBox = new VBox(15);
        inventoryBox.setPadding(new Insets(15));
        inventoryBox.setStyle("-fx-background-color: #E6E6FA; -fx-border-color: #8B4513; -fx-border-width: 2px;");
        inventoryBox.setPrefWidth(200);
        
        Text inventoryTitle = new Text("Inventory");
        inventoryTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        // Display food items
        VBox foodItems = new VBox(5);
        Text foodTitle = new Text("Food:");
        foodTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Text kibbleCount = new Text("Kibble: " + gameState.getItemCount("Kibble"));
        Text treatsCount = new Text("Treats: " + gameState.getItemCount("Treats"));
        Text premiumFoodCount = new Text("Premium Food: " + gameState.getItemCount("Premium Food"));
        
        foodItems.getChildren().addAll(foodTitle, kibbleCount, treatsCount, premiumFoodCount);
        
        // Display special items
        VBox specialItems = new VBox(5);
        Text specialTitle = new Text("Special Items:");
        specialTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Text vaccineCount = new Text("Vaccine: " + gameState.getItemCount("Vaccine"));
        specialItems.getChildren().addAll(specialTitle, vaccineCount);
        
        // Add shop button
        Button shopButton = createStyledButton("Go to Shop");
        shopButton.setOnAction(e -> {
            ShopScreen shopScreen = new ShopScreen(stage, gameState);
            shopScreen.show();
        });
        
        inventoryBox.getChildren().addAll(inventoryTitle, foodItems, specialItems, shopButton);
        return inventoryBox;
    }

    private VBox createStatsDisplay() {
        VBox statsBox = new VBox(15);
        statsBox.setPadding(new Insets(15));
        statsBox.setStyle("-fx-background-color: #E6E6FA; -fx-border-color: #8B4513; -fx-border-width: 2px;");
        statsBox.setPrefWidth(200);
        
        Text statsTitle = new Text("Pet Stats");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        // Create the status bars
        healthBar = createStatusBar(pet.getHealth(), Color.RED);
        healthText = new Text("Health: " + pet.getHealth() + "/" + pet.getMaxHealth());
        
        happinessBar = createStatusBar(pet.getHappiness(), Color.PINK);
        happinessText = new Text("Happiness: " + pet.getHappiness());
        
        fullnessBar = createStatusBar(pet.getFullness(), Color.GREEN);
        fullnessText = new Text("Fullness: " + pet.getFullness());
        
        energyBar = createStatusBar(pet.getEnergy(), Color.BLUE);
        energyText = new Text("Energy: " + pet.getEnergy());
        
        // Add a vet button
        Button vetButton = createStyledButton("Visit Vet (50 Dabloons)");
        vetButton.setOnAction(e -> visitVet());
        
        statsBox.getChildren().addAll(
            statsTitle,
            healthText, healthBar,
            happinessText, happinessBar,
            fullnessText, fullnessBar,
            energyText, energyBar,
            vetButton
        );
        
        return statsBox;
    }

    private HBox createControls() {
        HBox controls = new HBox(15);
        controls.setPadding(new Insets(15));
        controls.setAlignment(Pos.CENTER);
        controls.setStyle("-fx-background-color: #D2B48C;");
        
        Button feedButton = createStyledButton("Feed");
        feedButton.setOnAction(e -> showFeedOptions());
        
        Button playButton = createStyledButton("Play");
        playButton.setOnAction(e -> play());
        
        Button sleepButton = createStyledButton("Sleep");
        sleepButton.setOnAction(e -> sleep());
        
        Button useItemButton = createStyledButton("Use Item");
        useItemButton.setOnAction(e -> showUseItemOptions());
        
        controls.getChildren().addAll(feedButton, playButton, sleepButton, useItemButton);
        return controls;
    }
    
    private void setupGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Update every 5 seconds (5,000,000,000 nanoseconds)
                if (now - lastUpdate >= 5_000_000_000L) {
                    lastUpdate = now;
                    updatePetStats();
                }
            }
        };
        gameLoop.start();
    }
    
    private void updatePetStats() {
        // Decrease stats over time
        pet.decreaseFullness(1);
        pet.decreaseEnergy(1);
        pet.decreaseHappiness(1);
        
        // Health decreases if other stats are low
        if (pet.getFullness() < 20 || pet.getEnergy() < 20 || pet.getHappiness() < 20) {
            pet.decreaseHealth(1);
        }
        
        // Update the UI
        updateStats();
    }
    
    private void setupKeyboardShortcuts() {
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case F -> showFeedOptions();
                case P -> play();
                case S -> sleep();
                case ESCAPE -> {
                    MainMenuScreen mainMenu = new MainMenuScreen(stage, gameState);
                    mainMenu.show();
                }
            }
        });
    }
    
    private void showFeedOptions() {
        // Create a modal dialog
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle("Feed " + pet.getName());
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #F5F5DC;");
        
        Text title = new Text("Select Food");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        GridPane foodGrid = new GridPane();
        foodGrid.setHgap(15);
        foodGrid.setVgap(15);
        foodGrid.setAlignment(Pos.CENTER);
        
        int row = 0;
        int col = 0;
        
        // Create food items if they exist in inventory
        if (gameState.getItemCount("Kibble") > 0) {
            VBox kibbleBox = createFoodButton("Kibble", "Basic food (+10 Fullness)", dialog);
            foodGrid.add(kibbleBox, col++, row);
            if (col > 1) { col = 0; row++; }
        }
        
        if (gameState.getItemCount("Treats") > 0) {
            VBox treatsBox = createFoodButton("Treats", "Special treats (+15 Fullness, +5 Happiness)", dialog);
            foodGrid.add(treatsBox, col++, row);
            if (col > 1) { col = 0; row++; }
        }
        
        if (gameState.getItemCount("Premium Food") > 0) {
            VBox premiumBox = createFoodButton("Premium Food", "High quality food (+25 Fullness, +10 Happiness)", dialog);
            foodGrid.add(premiumBox, col++, row);
            if (col > 1) { col = 0; row++; }
        }
        
        // If no food in inventory
        if (row == 0 && col == 0) {
            Text noFood = new Text("No food in inventory!\nVisit the shop to buy some.");
            noFood.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            foodGrid.add(noFood, 0, 0);
        }
        
        Button closeButton = createStyledButton("Cancel");
        closeButton.setOnAction(e -> dialog.close());
        
        content.getChildren().addAll(title, foodGrid, closeButton);
        
        Scene dialogScene = new Scene(content, 500, 400);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }
    
    private VBox createFoodButton(String name, String description, Stage dialog) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setMinWidth(150);
        box.setMinHeight(150);
        box.setMaxWidth(150);
        box.setStyle("-fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-color: #F8F8F8;");
        
        Text nameText = new Text(name);
        nameText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Text descText = new Text(description);
        descText.setWrappingWidth(130);
        descText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        Text countText = new Text("Available: " + gameState.getItemCount(name));
        
        box.getChildren().addAll(nameText, descText, countText);
        
        // Hover effect
        box.setOnMouseEntered(e -> box.setStyle("-fx-border-color: #4CAF50; -fx-border-radius: 5; -fx-background-color: #F8F8F8;"));
        box.setOnMouseExited(e -> box.setStyle("-fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-color: #F8F8F8;"));
        
        // Click effect
        box.setOnMouseClicked(e -> {
            feedPet(name);
            dialog.close();
        });
        
        return box;
    }
    
    private void feedPet(String foodName) {
        // Check if the item is in inventory and remove it
        if (gameState.useItem(foodName)) {
            // Update sprite to hungry animation
            updatePetSprite("HUNGRY");
            
            switch (foodName) {
                case "Kibble":
                    pet.increaseFullness(10);
                    break;
                case "Treats":
                    pet.increaseFullness(15);
                    pet.increaseHappiness(5);
                    break;
                case "Premium Food":
                    pet.increaseFullness(25);
                    pet.increaseHappiness(10);
                    break;
            }
            // Add score for feeding
            gameState.addScore(10);
            updateStats();
            updateTopBar();
            
            // Show feeding animation
            scaleTransition(petSprite, 1.2, 0.2);
            
            // Return to normal sprite after delay
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> updatePetSprite("NORMAL")));
            timeline.play();
        } else {
            showAlert("No " + foodName, "You don't have any " + foodName + " in your inventory.");
        }
    }
    
    private void play() {
        // Update sprite to happy/play animation
        updatePetSprite("ANGRY"); // Use ANGRY for play animation
        
        // Increase happiness, decrease energy
        pet.increaseHappiness(15);
        pet.decreaseEnergy(10);
        
        // Add score and earn dabloons
        gameState.addScore(15);
        int earnedDabloons = 5 + (int)(Math.random() * 5); // 5-9 dabloons
        gameState.addCurrency(earnedDabloons);
        
        showAlert("Playing!", "You played with " + pet.getName() + ".\nHappiness +15, Energy -10\nEarned " + earnedDabloons + " dabloons!\nScore +15");
        
        // Update the UI to reflect changes
        updateStats();
        updateTopBar();
        
        // Return to normal sprite after delay
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> updatePetSprite("NORMAL")));
        timeline.play();
    }
    
    private void sleep() {
        // Update sprite to sleep animation
        updatePetSprite("SLEEP");
        
        // Increase energy, decrease happiness slightly
        pet.increaseEnergy(25);
        pet.decreaseHappiness(5);
        
        // Add score and earn dabloons
        gameState.addScore(10);
        int earnedDabloons = 3 + (int)(Math.random() * 3); // 3-5 dabloons
        gameState.addCurrency(earnedDabloons);
        
        showAlert("Sleeping!", pet.getName() + " is sleeping.\nEnergy +25, Happiness -5\nEarned " + earnedDabloons + " dabloons!\nScore +10");
        
        // Update the UI to reflect changes
        updateStats();
        updateTopBar();
        
        // Return to normal sprite after delay
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> updatePetSprite("NORMAL")));
        timeline.play();
    }
    
    private void showUseItemOptions() {
        // Create a modal dialog
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle("Use Item");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #F5F5DC;");
        
        Text title = new Text("Select Item");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        GridPane itemGrid = new GridPane();
        itemGrid.setHgap(15);
        itemGrid.setVgap(15);
        itemGrid.setAlignment(Pos.CENTER);
        
        // Check if vaccine is available
        if (gameState.getItemCount("Vaccine") > 0) {
            VBox vaccineBox = createItemButton("Vaccine", "Increases max health (+20 Max Health)", dialog);
            itemGrid.add(vaccineBox, 0, 0);
        } else {
            Text noItems = new Text("No special items in inventory!\nVisit the shop to buy some.");
            noItems.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            itemGrid.add(noItems, 0, 0);
        }
        
        Button closeButton = createStyledButton("Cancel");
        closeButton.setOnAction(e -> dialog.close());
        
        content.getChildren().addAll(title, itemGrid, closeButton);
        
        Scene dialogScene = new Scene(content, 400, 350);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }
    
    private VBox createItemButton(String name, String description, Stage dialog) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setMinWidth(150);
        box.setMinHeight(150);
        box.setMaxWidth(150);
        box.setStyle("-fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-color: #F8F8F8;");
        
        Text nameText = new Text(name);
        nameText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Text descText = new Text(description);
        descText.setWrappingWidth(130);
        descText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        Text countText = new Text("Available: " + gameState.getItemCount(name));
        
        box.getChildren().addAll(nameText, descText, countText);
        
        // Hover effect
        box.setOnMouseEntered(e -> box.setStyle("-fx-border-color: #4CAF50; -fx-border-radius: 5; -fx-background-color: #F8F8F8;"));
        box.setOnMouseExited(e -> box.setStyle("-fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-color: #F8F8F8;"));
        
        // Click effect
        box.setOnMouseClicked(e -> {
            useItem(name);
            dialog.close();
        });
        
        return box;
    }
    
    private void useItem(String itemName) {
        // Check if the item is in inventory and remove it
        if (gameState.useItem(itemName)) {
            if ("Vaccine".equals(itemName)) {
                pet.increaseMaxHealth(20);
                pet.setHealth(pet.getMaxHealth()); // Heal to full when increasing max health
                showAlert("Vaccine Used", "Your pet's maximum health has been increased by 20!");
            }
            updateStats();
        } else {
            showAlert("No " + itemName, "You don't have any " + itemName + " in your inventory.");
        }
    }
    
    private void visitVet() {
        // Check if the player has enough currency
        if (gameState.getCurrency() >= 50) {
            // Deduct the cost
            gameState.addCurrency(-50);
            
            // Heal the pet to full health
            pet.setHealth(pet.getMaxHealth());
            
            // Add score
            gameState.addScore(20);
            
            // Update the display
            updateStats();
            updateTopBar();
            
            showAlert("Vet Visit", "Your pet has been healed to full health!\nScore +20");
        } else {
            showAlert("Not Enough Dabloons", "You need 50 dabloons to visit the vet.");
        }
    }
    
    private void saveGame() {
        String petType = pet.getType().toLowerCase();
        String saveDirectory = "saves";
        File saveDir = new File(saveDirectory);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        
        String fileName = saveDirectory + "/" + petType + "_save.txt";
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.File(fileName))) {
            // Save pet information
            writer.println("name=" + pet.getName());
            writer.println("type=" + pet.getType());
            writer.println("health=" + pet.getHealth());
            writer.println("maxHealth=" + pet.getMaxHealth());
            writer.println("energy=" + pet.getEnergy());
            writer.println("fullness=" + pet.getFullness());
            writer.println("happiness=" + pet.getHappiness());
            
            // Save game state information
            writer.println("currency=" + gameState.getCurrency());
            writer.println("score=" + gameState.getScore());
            
            // Save inventory
            writer.println("kibble=" + gameState.getItemCount("Kibble"));
            writer.println("treats=" + gameState.getItemCount("Treats"));
            writer.println("premiumFood=" + gameState.getItemCount("Premium Food"));
            writer.println("vaccine=" + gameState.getItemCount("Vaccine"));
            
            System.out.println("Game saved to " + fileName);
            
            // Show confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Saved");
            alert.setHeaderText("Game Saved Successfully");
            alert.setContentText("Your game has been saved to: " + fileName);
            alert.showAndWait();
            
        } catch (Exception e) {
            System.err.println("Error saving game: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText("Could Not Save Game");
            alert.setContentText("An error occurred while saving the game: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private void updateStats() {
        // Update text displays
        healthText.setText("Health: " + pet.getHealth() + "/" + pet.getMaxHealth());
        happinessText.setText("Happiness: " + pet.getHappiness());
        fullnessText.setText("Fullness: " + pet.getFullness());
        energyText.setText("Energy: " + pet.getEnergy());
        
        // Update progress bars
        healthBar.setProgress(pet.getHealth() / 100.0);
        happinessBar.setProgress(pet.getHappiness() / 100.0);
        fullnessBar.setProgress(pet.getFullness() / 100.0);
        energyBar.setProgress(pet.getEnergy() / 100.0);
    }
    
    private ProgressBar createStatusBar(int currentValue, Color color) {
        ProgressBar bar = new ProgressBar();
        bar.setProgress(currentValue / 100.0);
        bar.setStyle("-fx-accent: " + toRGBCode(color) + ";");
        return bar;
    }
    
    private String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
    
    private Color getPetColor() {
        switch (pet.getType().toLowerCase()) {
            case "dog":
                return Color.BROWN;
            case "cat":
                return Color.ORANGE;
            case "bunny":
                return Color.LIGHTGRAY;
            default:
                return Color.BLUE;
        }
    }
    
    private void showFeedingAnimation() {
        // Animation effect when feeding the pet
        scaleTransition(petSprite, 1.2, 0.2);
    }
    
    private void scaleTransition(ImageView node, double scale, double duration) {
        ScaleTransition st = new ScaleTransition(javafx.util.Duration.seconds(duration), node);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(scale);
        st.setToY(scale);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }
    
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px;"
        );
        
        // Add hover effect
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #45a049; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px;"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px;"
        ));
        
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
        stage.setTitle("Virtual Pet Game - " + pet.getName());
        stage.setScene(scene);
        stage.show();
        setupGameLoop();
    }
    
    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }
    
    private void updateTopBar() {
        scoreText.setText("Score: " + gameState.getScore());
        currencyText.setText("Dabloons: " + gameState.getCurrency());
    }
    
    private void updatePetSprite(String state) {
        String prefix = pet.getType().equalsIgnoreCase("Cat") ? "KITTY" : pet.getType().toUpperCase();
        String spritePath = "images/" + prefix + "_" + state + ".png";
        try {
            File imageFile = new File(spritePath);
            System.out.println("Attempting to load sprite: " + spritePath);
            System.out.println("Pet type: " + pet.getType());
            System.out.println("File exists: " + imageFile.exists());
            System.out.println("File absolute path: " + imageFile.getAbsolutePath());
            String imageURI = imageFile.toURI().toString();
            System.out.println("Image URI: " + imageURI);
            javafx.scene.image.Image image = new javafx.scene.image.Image(imageURI);
            petSprite.setImage(image);
            System.out.println("Sprite updated successfully");
        } catch (Exception e) {
            System.err.println("Error updating sprite: " + spritePath);
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 