import javafx.application.Application;
import javafx.stage.Stage;
import java.io.File;

/**
 * Main class to launch the Tamagotchi virtual pet application.
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Create the saves directory if it doesn't exist
        File savesDir = new File("saves");
        if (!savesDir.exists()) {
            savesDir.mkdir();
        }
        
        // Initialize game state
        GameState gameState = new GameState();
        
        // Set up stage properties
        primaryStage.setTitle("Tamagotchi Game");
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        
        // Force windowed mode at startup
        primaryStage.setFullScreen(false);
        gameState.setFullscreen(false);
        
        // Show main menu
        MainMenuScreen mainMenu = new MainMenuScreen(primaryStage, gameState);
        mainMenu.show();
        
        primaryStage.show();
    }
    
    /**
     * Main method to launch the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
} 