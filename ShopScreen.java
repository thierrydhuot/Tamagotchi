import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;

/**
 * The ShopScreen class represents the shop interface where players can purchase items
 * using the in-game currency (dabloons).
 */
public class ShopScreen {
    private Stage stage;
    private GameState gameState;
    private Scene scene;
    
    private java.util.Map<String, Integer> itemPrices = new java.util.HashMap<>();
    
    public ShopScreen(Stage stage, GameState gameState) {
        this.stage = stage;
        this.gameState = gameState;
        
        setupPrices();
        createScene();
    }
    
    private void setupPrices() {
        // Food prices
        itemPrices.put("Kibble", 10);
        itemPrices.put("Treats", 20);
        itemPrices.put("Premium Food", 35);
        
        // Special items
        itemPrices.put("Vaccine", 100);
    }
    
    private void createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F5DC;");
        
        // Top - Title and currency
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        // Center - Shop items
        ScrollPane shopContent = createShopContent();
        root.setCenter(shopContent);
        
        // Bottom - Back button
        HBox bottomBar = createBottomBar();
        root.setBottom(bottomBar);
        
        scene = new Scene(root, 700, 550);
        stage.setScene(scene);
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #D2B48C;");
        
        // Shop title
        Text shopTitle = new Text("Pet Shop");
        shopTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        // Pet name display
        Text petNameText = new Text(gameState.getPet().getName() + "'s Shop");
        petNameText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Currency display
        Text currencyText = new Text("Dabloons: " + gameState.getCurrency());
        currencyText.setFont(Font.font("Arial", 18));
        
        // Add spacer to push currency to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        topBar.getChildren().addAll(shopTitle, petNameText, spacer, currencyText);
        return topBar;
    }
    
    private ScrollPane createShopContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(15));
        content.setAlignment(Pos.TOP_CENTER);
        
        // Food section
        Text foodSectionTitle = new Text("Food Items");
        foodSectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        foodSectionTitle.setFill(Color.FORESTGREEN);
        
        HBox foodItems = new HBox(15);
        foodItems.setAlignment(Pos.CENTER);
        
        // Add food items
        foodItems.getChildren().addAll(
            createShopItemCompact("Kibble", "Basic food (+10 Fullness)", itemPrices.get("Kibble")),
            createShopItemCompact("Treats", "Special treats (+15 Fullness, +5 Happiness)", itemPrices.get("Treats")),
            createShopItemCompact("Premium Food", "High quality food (+25 Fullness, +10 Happiness)", itemPrices.get("Premium Food"))
        );
        
        // Special items section
        Text specialSectionTitle = new Text("Special Items");
        specialSectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        specialSectionTitle.setFill(Color.FORESTGREEN);
        
        HBox specialItems = new HBox(15);
        specialItems.setAlignment(Pos.CENTER);
        
        // Add special items
        specialItems.getChildren().add(
            createShopItemCompact("Vaccine", "Increases your pet's maximum health permanently (+20 Max Health)", itemPrices.get("Vaccine"))
        );
        
        content.getChildren().addAll(
            foodSectionTitle, foodItems,
            new Region() {{ setMinHeight(20); }},
            specialSectionTitle, specialItems
        );
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #F5F5DC;");
        
        return scrollPane;
    }
    
    private VBox createShopItemCompact(String itemName, String description, int price) {
        VBox itemBox = new VBox(5);
        itemBox.setPadding(new Insets(10));
        itemBox.setStyle("-fx-background-color: #E6E6FA; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 5px;");
        itemBox.setPrefWidth(180);
        itemBox.setMaxWidth(180);
        itemBox.setMinHeight(180);
        itemBox.setAlignment(Pos.CENTER);
        
        Text nameText = new Text(itemName);
        nameText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Text descText = new Text(description);
        descText.setWrappingWidth(160);
        descText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        Text priceText = new Text(price + " Dabloons");
        priceText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        priceText.setFill(Color.DARKBLUE);
        
        // Current inventory count
        int currentCount = gameState.getItemCount(itemName);
        Text countText = new Text("In Inventory: " + currentCount);
        
        Button buyButton = createStyledButton("Buy");
        buyButton.setPrefWidth(100);
        
        // Disable button if at max capacity (3 for food items)
        if (currentCount >= 3 && !itemName.equals("Vaccine")) {
            buyButton.setDisable(true);
            countText.setText("In Inventory: " + currentCount + " (MAX)");
        }
        
        buyButton.setOnAction(e -> purchaseItem(itemName, price));
        
        itemBox.getChildren().addAll(nameText, descText, priceText, countText, buyButton);
        
        // Hover effect
        itemBox.setOnMouseEntered(e -> {
            if (!buyButton.isDisabled()) {
                itemBox.setStyle("-fx-background-color: #DEDEFF; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 5px;");
            }
        });
        
        itemBox.setOnMouseExited(e -> {
            itemBox.setStyle("-fx-background-color: #E6E6FA; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 5px;");
        });
        
        return itemBox;
    }
    
    private void purchaseItem(String itemName, int price) {
        // Check if player has enough currency
        if (gameState.getCurrency() < price) {
            showAlert("Not Enough Dabloons", "You don't have enough dabloons to purchase this item!");
            return;
        }
        
        // Check if inventory has space (max 3 for food items)
        if (gameState.getItemCount(itemName) >= 3 && !itemName.equals("Vaccine")) {
            showAlert("Inventory Full", "You can't carry more than 3 of this item!");
            return;
        }
        
        // Deduct currency and add item
        gameState.addCurrency(-price);
        gameState.addItem(itemName, 1);
        
        // Show success message
        showAlert("Purchase Successful", "You purchased " + itemName + "!");
        
        // Update the display
        createScene();
    }
    
    private HBox createBottomBar() {
        HBox bottomBar = new HBox(20);
        bottomBar.setPadding(new Insets(15));
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setStyle("-fx-background-color: #D2B48C;");
        
        Button backButton = createStyledButton("Back to Game");
        backButton.setOnAction(e -> {
            // Return to game screen - this should only be accessible from game screen
            GameScreen gameScreen = new GameScreen(stage, gameState);
            gameScreen.show();
        });
        
        bottomBar.getChildren().add(backButton);
        return bottomBar;
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
        stage.setTitle("Pet Shop");
        stage.show();
    }
} 