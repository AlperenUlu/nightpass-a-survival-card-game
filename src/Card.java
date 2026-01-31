/**
 * Represents a game card with attributes like name, attack, and health.
 * It tracks both base stats and current stats, which may change during gameplay.
 */
public class Card {
    // The name of the card.
    private String name;

    // The card's default attack value.
    private int baseAttack;

    // The card's attack value during gameplay, which can be modified.
    private int currentAttack;

    // The card's default health value.
    private int baseHealth;

    // The card's health value during gameplay, which can be modified.
    private int currentHealth;

    // The card's missing health value to revive after being discarded, which can be modified.
    private int missingHealth;
    /**
     * Constructs a new Card with specified base stats.
     * Current attack and health are initialized to their base values.
     * @param name The name of the card.
     * @param baseAttack The base attack value.
     * @param baseHealth The base health value.
     */
    Card(String name,int baseAttack, int baseHealth){
        this.name = name;
        this.baseAttack = baseAttack;
        this.baseHealth = baseHealth;
        this.currentAttack = baseAttack;
        this.currentHealth = baseHealth;
    }

    /**
     * Sets the card's base attack value.
     * @param baseAttack The new base attack value.
     */
    public void setBaseAttack(int baseAttack){this.baseAttack =baseAttack;}

    /**
     * Sets the card's base health value.
     * @param baseHealth The new base health value.
     */
    public void setBaseHealth(int baseHealth){this.baseHealth = baseHealth;}

    /**
     * Sets the card's current attack value.
     * @param currentAttack The new current attack value.
     */
    public void setCurrentAttack(int currentAttack){this.currentAttack =currentAttack;}

    /**
     * Sets the card's current health value.
     * @param currentHealth The new current health value.
     */
    public void setCurrentHealth(int currentHealth){this.currentHealth =currentHealth;}

    /**
     * Sets the card's name.
     * @param name The new name for the card.
     */
    public void setName(String name){this.name =name;}

    /**
     * Sets the card's remaining health to revive.
     * @param missingHealth The health needed to revive for the card.
     */
    public void setMissingHealth(int missingHealth) {this.missingHealth = missingHealth;}

    /**
     * Gets the card's base attack value.
     * @return The base attack value.
     */
    public int getBaseAttack(){return this.baseAttack;}

    /**
     * Gets the card's base health value.
     * @return The base health value.
     */
    public int getBaseHealth(){return this.baseHealth;}

    /**
     * Gets the card's name.
     * @return The name of the card.
     */
    public String getName(){return this.name;}

    /**
     * Gets the card's current attack value.
     * @return The current attack value.
     */
    public int getCurrentAttack(){return this.currentAttack;}

    /**
     * Gets the card's current health value.
     * @return The current health value.
     */
    public int getCurrentHealth(){return this.currentHealth;}

    /**
     * Gets the card's missing health value.
     * @return The missing health value.
     */
    public int getMissingHealth() {return missingHealth;}
}