import java.io.File;
import java.util.Map;
import java.util.HashMap;
import javafx.scene.image.Image;

/**
 * Represents a virtual pet with attributes and behaviors.
 */
public class Pet {
    private String name;
    private String type;
    private int health;
    private int maxHealth;
    private int happiness;
    private int fullness;
    private int energy;
    private State state;
    private javafx.scene.image.Image currentSprite;
    private static final Map<String, Map<String, Image>> sprites = new HashMap<>();

    public enum State {
        IDLE, EATING, PLAYING, SLEEPING, SICK
    }

    static {
        // Load all sprites for each pet type
        loadSpritesForType("DOG");
        loadSpritesForType("BUNNY");
        loadSpritesForType("KITTY");
    }

    private static void loadSpritesForType(String petType) {
        Map<String, Image> stateSprites = new HashMap<>();
        String[] states = {"NORMAL", "ANGRY", "SLEEP", "HUNGRY", "DEAD"};
        
        for (String state : states) {
            try {
                String path = "images/" + petType + "_" + state + ".png";
                Image sprite = new Image(new File(path).toURI().toString());
                stateSprites.put(state, sprite);
            } catch (Exception e) {
                System.err.println("Failed to load sprite: " + petType + "_" + state);
                e.printStackTrace();
            }
        }
        
        sprites.put(petType, stateSprites);
    }

    public Pet(String name, String type) {
        this.name = name;
        this.type = type.toUpperCase();
        this.maxHealth = 100;
        this.health = this.maxHealth;
        this.happiness = 100;
        this.fullness = 100;
        this.energy = 100;
        this.state = State.IDLE;
        updateSprite();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.min(health, maxHealth);
        updateSprite();
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void increaseMaxHealth(int amount) {
        this.maxHealth += amount;
        // Also increase current health by the same amount
        this.health = Math.min(maxHealth, health + amount);
    }

    public int getHappiness() {
        return happiness;
    }

    public void setHappiness(int happiness) {
        this.happiness = Math.max(0, Math.min(happiness, 100));
        updateSprite();
    }

    public int getFullness() {
        return fullness;
    }

    public void setFullness(int fullness) {
        this.fullness = Math.max(0, Math.min(fullness, 100));
        updateSprite();
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(energy, 100));
        updateSprite();
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        updateSprite();
    }

    // Incremental attribute changes
    public void increaseHealth(int amount) {
        setHealth(health + amount);
    }

    public void decreaseHealth(int amount) {
        setHealth(health - amount);
    }

    public void increaseHappiness(int amount) {
        setHappiness(happiness + amount);
    }

    public void decreaseHappiness(int amount) {
        setHappiness(happiness - amount);
    }

    public void increaseFullness(int amount) {
        setFullness(fullness + amount);
    }

    public void decreaseFullness(int amount) {
        setFullness(fullness - amount);
    }

    public void increaseEnergy(int amount) {
        setEnergy(energy + amount);
    }

    public void decreaseEnergy(int amount) {
        setEnergy(energy - amount);
    }
    
    /**
     * Heals the pet to maximum health
     */
    public void healToMax() {
        setHealth(maxHealth);
        setState(State.IDLE);
    }
    
    /**
     * Apply a vaccine to increase max health
     * @param amount The amount to increase max health by
     */
    public void applyVaccine(int amount) {
        increaseMaxHealth(amount);
        setState(State.IDLE);
    }

    /**
     * Updates the pet's state based on its current attributes.
     * This method should be called periodically to simulate the passage of time.
     */
    public void update() {
        // Decrease attributes over time to simulate needs
        decreaseFullness(1);
        decreaseHappiness(1);
        decreaseEnergy(1);

        // If energy is low, health decreases
        if (energy < 20) {
            decreaseHealth(1);
        }

        // If fullness is low, health decreases
        if (fullness < 20) {
            decreaseHealth(1);
        }

        // If happiness is low, health decreases
        if (happiness < 20) {
            decreaseHealth(1);
        }

        // Update pet state based on conditions
        if (health < 30) {
            state = State.SICK;
        } else if (energy < 30) {
            state = State.SLEEPING;
        } else {
            state = State.IDLE;
        }

        // Update sprite after all changes
        updateSprite();
    }

    /**
     * Feeds the pet to increase fullness.
     */
    public void feed() {
        increaseFullness(20);
        state = State.EATING;
    }

    /**
     * Plays with the pet to increase happiness.
     */
    public void play() {
        increaseHappiness(20);
        decreaseEnergy(10);
        state = State.PLAYING;
    }

    /**
     * Lets the pet sleep to recover energy.
     */
    public void sleep() {
        increaseEnergy(30);
        state = State.SLEEPING;
    }

    /**
     * Checks if the pet is alive.
     * @return true if pet's health is greater than 0, false otherwise
     */
    public boolean isAlive() {
        return health > 0;
    }

    public Image getCurrentSprite() {
        return currentSprite;
    }

    private void updateSprite() {
        Map<String, Image> typeSprites = sprites.get(type);
        if (typeSprites == null) return;

        // Choose the appropriate sprite based on pet's state and stats
        if (!isAlive()) {
            currentSprite = typeSprites.get("DEAD");
        } else if (state == State.SLEEPING) {
            currentSprite = typeSprites.get("SLEEP");
        } else if (fullness < 50) {
            currentSprite = typeSprites.get("HUNGRY");
        } else if (health < 50 || happiness < 50 || energy < 50) {
            currentSprite = typeSprites.get("ANGRY");
        } else {
            currentSprite = typeSprites.get("NORMAL");
        }
    }
} 