/**
 * Represents the player in the Tamagotchi game.
 * Stores player information like name and score.
 */
public class Player {
    private String name;
    private int currency;
    private int totalPlayTime;
    private int numberOfSessions;

    public Player(String name) {
        this.name = name;
        this.currency = 0;
        this.totalPlayTime = 0;
        this.numberOfSessions = 0;
    }

    // Currency management
    public int getCurrency() {
        return currency;
    }

    public void addCurrency(int amount) {
        currency += amount;
    }

    public boolean spendCurrency(int amount) {
        if (currency >= amount) {
            currency -= amount;
            return true;
        }
        return false;
    }

    // Statistics tracking
    public void incrementPlayTime(int minutes) {
        totalPlayTime += minutes;
    }

    public void incrementSessions() {
        numberOfSessions++;
    }

    public double getAveragePlayTimePerSession() {
        return numberOfSessions == 0 ? 0 : 
               totalPlayTime / (double) numberOfSessions;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getTotalPlayTime() {
        return totalPlayTime;
    }

    public int getNumberOfSessions() {
        return numberOfSessions;
    }

    public void setName(String name) {
        this.name = name;
    }
} 