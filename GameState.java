/**
 * Manages the overall state of the game, including the current pet, player, and game settings.
 */
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class GameState {
    private boolean isMuted = false;
    private String playerName;
    private Pet pet;
    private int score = 0; // Player's score
    private int currency = 100; // Starting currency (dabloons)
    
    // Game statistics
    private String creationDate;
    private long totalPlayTime = 0;
    private int totalFeedings = 0;
    private int totalPlaySessions = 0;
    private int totalVetVisits = 0;
    private boolean statsVisible = true;
    private double healthSum = 0;
    private int healthReadings = 0;
    
    // Parental controls
    private String parentalPassword = null;
    private int dailyTimeLimit = 120; // in minutes
    private java.time.LocalTime allowedStartTime = java.time.LocalTime.of(8, 0); // 8:00 AM
    private java.time.LocalTime allowedEndTime = java.time.LocalTime.of(20, 0); // 8:00 PM
    
    // Game settings
    private boolean showHealthStat = true;
    private boolean showEnergyStat = true;
    private boolean showFullnessStat = true;
    private boolean showHappinessStat = true;
    private boolean isFullscreen = false;
    
    // Volume settings
    private double masterVolume = 0.8;
    private double sfxVolume = 0.8;
    private double musicVolume = 0.5;
    
    // Inventory system
    private java.util.Map<String, Integer> inventory = new java.util.HashMap<>();
    
    public GameState() {
        // Initialize inventory with some default items
        inventory.put("Kibble", 3);
        inventory.put("Treats", 3);
        inventory.put("Premium Food", 3);
        inventory.put("Vaccine", 1);
        
        // Set creation date
        this.creationDate = java.time.LocalDateTime.now().toString();
    }
    
    // Game management methods
    public void startNewGame(String playerName, Pet pet) {
        this.playerName = playerName;
        this.pet = pet;
        this.score = 0;
        this.currency = 100; // Start with 100 dabloons
    }
    
    public void addScore(int amount) {
        this.score = Math.max(0, this.score + amount);
    }
    
    public int getScore() {
        return score;
    }
    
    // Player and pet methods
    public String getPlayerName() {
        return playerName;
    }
    
    public Pet getPet() {
        return pet;
    }
    
    // Currency methods
    public int getCurrency() {
        return currency;
    }
    
    public void addCurrency(int amount) {
        this.currency += amount;
    }
    
    // Inventory methods
    public int getItemCount(String itemName) {
        return inventory.getOrDefault(itemName, 0);
    }
    
    public boolean useItem(String itemName) {
        int currentCount = getItemCount(itemName);
        if (currentCount > 0) {
            inventory.put(itemName, currentCount - 1);
            return true;
        }
        return false;
    }
    
    public void addItem(String itemName, int amount) {
        int currentCount = getItemCount(itemName);
        
        // Limit food items to 3 maximum
        if (!itemName.equals("Vaccine")) {
            currentCount = Math.min(currentCount + amount, 3);
        } else {
            currentCount += amount;
        }
        
        inventory.put(itemName, currentCount);
    }
    
    // Parental controls methods
    public boolean hasParentalPassword() {
        return parentalPassword != null && !parentalPassword.isEmpty();
    }
    
    public boolean checkParentalPassword(String password) {
        return password != null && password.equals(parentalPassword);
    }
    
    public void setParentalPassword(String password) {
        this.parentalPassword = password;
    }
    
    // Time limit methods
    public int getDailyTimeLimit() {
        return dailyTimeLimit;
    }
    
    public void setDailyTimeLimit(int minutes) {
        this.dailyTimeLimit = minutes;
    }
    
    public java.time.LocalTime getAllowedStartTime() {
        return allowedStartTime;
    }
    
    public void setAllowedStartTime(java.time.LocalTime startTime) {
        this.allowedStartTime = startTime;
    }
    
    public java.time.LocalTime getAllowedEndTime() {
        return allowedEndTime;
    }
    
    public void setAllowedEndTime(java.time.LocalTime endTime) {
        this.allowedEndTime = endTime;
    }
    
    // Stats visibility methods
    public boolean isShowHealthStat() {
        return showHealthStat;
    }
    
    public void setShowHealthStat(boolean show) {
        this.showHealthStat = show;
    }
    
    public boolean isShowEnergyStat() {
        return showEnergyStat;
    }
    
    public void setShowEnergyStat(boolean show) {
        this.showEnergyStat = show;
    }
    
    public boolean isShowFullnessStat() {
        return showFullnessStat;
    }
    
    public void setShowFullnessStat(boolean show) {
        this.showFullnessStat = show;
    }
    
    public boolean isShowHappinessStat() {
        return showHappinessStat;
    }
    
    public void setShowHappinessStat(boolean show) {
        this.showHappinessStat = show;
    }
    
    // Volume methods
    public double getMasterVolume() {
        return masterVolume;
    }
    
    public void setMasterVolume(double volume) {
        this.masterVolume = volume;
    }
    
    public double getSfxVolume() {
        return sfxVolume;
    }
    
    public void setSfxVolume(double volume) {
        this.sfxVolume = volume;
    }
    
    public double getMusicVolume() {
        return musicVolume;
    }
    
    public void setMusicVolume(double volume) {
        this.musicVolume = volume;
    }
    
    public boolean isFullscreen() {
        return isFullscreen;
    }
    
    public void setFullscreen(boolean fullscreen) {
        this.isFullscreen = fullscreen;
    }
    
    public boolean checkPlayTimeAllowed() {
        java.time.LocalTime now = java.time.LocalTime.now();
        return now.isAfter(allowedStartTime) && now.isBefore(allowedEndTime);
    }

    // Add to save game method
    public void saveToFile(String fileName) {
        try (PrintWriter writer = new PrintWriter(new File(fileName))) {
            // Basic info
            writer.println("playerName=" + playerName);
            writer.println("petName=" + pet.getName());
            writer.println("petType=" + pet.getType());
            writer.println("creationDate=" + creationDate);

            // Pet stats
            writer.println("health=" + pet.getHealth());
            writer.println("maxHealth=" + pet.getMaxHealth());
            writer.println("happiness=" + pet.getHappiness());
            writer.println("fullness=" + pet.getFullness());
            writer.println("energy=" + pet.getEnergy());

            // Game progress
            writer.println("score=" + score);
            writer.println("currency=" + currency);

            // Parental controls
            writer.println("parentalPassword=" + parentalPassword);
            writer.println("dailyTimeLimit=" + dailyTimeLimit);
            writer.println("allowedStartTime=" + allowedStartTime);
            writer.println("allowedEndTime=" + allowedEndTime);

            // Game statistics
            writer.println("totalPlayTime=" + totalPlayTime);
            writer.println("totalFeedings=" + totalFeedings);
            writer.println("totalPlaySessions=" + totalPlaySessions);
            writer.println("totalVetVisits=" + totalVetVisits);
            writer.println("healthSum=" + healthSum);
            writer.println("healthReadings=" + healthReadings);

            // Inventory
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                writer.println("inventory." + entry.getKey() + "=" + entry.getValue());
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save game state to " + fileName);
        }
    }

    // Add to load game method
    public void loadFromFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];

                    switch (key) {
                        // Basic info
                        case "playerName" -> playerName = value;
                        case "petName" -> pet.setName(value);
                        case "petType" -> pet.setType(value);
                        case "creationDate" -> creationDate = value;

                        // Pet stats
                        case "health" -> pet.setHealth((int) Double.parseDouble(value));
                        case "maxHealth" -> pet.setMaxHealth((int) Double.parseDouble(value));
                        case "happiness" -> pet.setHappiness((int) Double.parseDouble(value));
                        case "fullness" -> pet.setFullness((int) Double.parseDouble(value));
                        case "energy" -> pet.setEnergy((int) Double.parseDouble(value));

                        // Game progress
                        case "score" -> score = Integer.parseInt(value);
                        case "currency" -> currency = Integer.parseInt(value);

                        // Parental controls
                        case "parentalPassword" -> parentalPassword = value;
                        case "dailyTimeLimit" -> dailyTimeLimit = Integer.parseInt(value);
                        case "allowedStartTime" -> allowedStartTime = LocalTime.parse(value);
                        case "allowedEndTime" -> allowedEndTime = LocalTime.parse(value);

                        // Game statistics
                        case "totalPlayTime" -> totalPlayTime = Long.parseLong(value);
                        case "totalFeedings" -> totalFeedings = Integer.parseInt(value);
                        case "totalPlaySessions" -> totalPlaySessions = Integer.parseInt(value);
                        case "totalVetVisits" -> totalVetVisits = Integer.parseInt(value);
                        case "healthSum" -> healthSum = Double.parseDouble(value);
                        case "healthReadings" -> healthReadings = Integer.parseInt(value);

                        // Inventory
                        default -> {
                            if (key.startsWith("inventory.")) {
                                String itemName = key.substring("inventory.".length());
                                inventory.put(itemName, Integer.parseInt(value));
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load game state from " + fileName);
        }
    }

    // Game statistics methods
    public long getTotalPlayTime() {
        return totalPlayTime;
    }

    public void addPlayTime(long milliseconds) {
        this.totalPlayTime += milliseconds;
    }

    public int getTotalFeedings() {
        return totalFeedings;
    }

    public void incrementFeedings() {
        this.totalFeedings++;
    }

    public int getTotalPlaySessions() {
        return totalPlaySessions;
    }

    public void incrementPlaySessions() {
        this.totalPlaySessions++;
    }

    public int getTotalVetVisits() {
        return totalVetVisits;
    }

    public void incrementVetVisits() {
        this.totalVetVisits++;
    }

    public boolean isStatsVisible() {
        return statsVisible;
    }

    public void setStatsVisible(boolean visible) {
        this.statsVisible = visible;
    }

    public double getAverageHealth() {
        if (healthReadings == 0) return 0;
        return healthSum / healthReadings;
    }

    public double calculateAverageHealth() {
        return getAverageHealth();
    }

    public void recordHealth(double health) {
        this.healthSum += health;
        this.healthReadings++;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String date) {
        this.creationDate = date;
    }

    // Update methods that should track statistics
    public void feedPet(String foodType) {
        if (useItem(foodType)) {
            incrementFeedings();
            recordHealth(pet.getHealth());
        }
    }

    public void playWithPet() {
        incrementPlaySessions();
        recordHealth(pet.getHealth());
    }

    public void visitVet() {
        incrementVetVisits();
        recordHealth(pet.getHealth());
    }
} 