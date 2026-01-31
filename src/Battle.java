/**
 * Formulates the logic for a battle simulation between a Card and an opposing Stranger.
 */
public class Battle {
    // The node (and its card inside it) participating in the battle.
    private Node node;

    // The attack power of the Stranger.
    private int attStr;

    // The health points of the Stranger.
    private int hpStr;

    // Default constructor.
    Battle(){}

    /**
     * Executes a battle between the card in the given node and a Stranger.
     * Calculates damage, updates scores, and returns a new node with the card's updated (post-battle) stats.
     * @param node     The node containing the Survivor's card.
     * @param attStr   The Stranger's attack power.
     * @param hpStr    The Stranger's health points.
     * @param score    The ScoreTracker object to update based on the battle outcome.
     * @return         A new Node containing a new Card with updated stats after the battle.
     */
    public Node battle(Node node, int attStr, int hpStr, ScoreTracker score){
        if(node == null){
            score.setStrangerScore(score.getStrangerScore() + 2);
            // If the node is null, we can't fight. Stranger gets the points.
        }

        Card fightingCard = node.getCard();
        int currentHealth = fightingCard.getCurrentHealth();
        int currentAttack = fightingCard.getCurrentAttack();
        int baseAttack = fightingCard.getBaseAttack();
        int baseHealth = fightingCard.getBaseHealth();
        int baseHealthStr = hpStr; // Storing original Stranger HP

        // Calculate combat damage
        currentHealth = currentHealth - attStr;
        hpStr = hpStr - currentAttack;

        // Fixing card health (it cannot go below 0)
        if (currentHealth < 0 ){
            currentHealth = 0;
        }

        // Recalculating card attack based on remaining health percentage
        int postAttack = (int) (baseAttack * currentHealth * 1.0 / baseHealth);
        currentAttack = Math.max(1,(postAttack));

        // Score updates based on battle outcome
        if (currentHealth <= 0){
            score.setStrangerScore(score.getStrangerScore() + 2);
        }
        if(hpStr <= 0){
            score.setSurvivorScore(score.getSurvivorScore() + 2);
        }
        if(0< currentHealth && currentHealth <= baseHealth){
            // Card survived (even if it didn't defeat the Stranger)
            score.setStrangerScore(score.getStrangerScore() + 1);
        }
        if(0< hpStr && hpStr <= baseHealthStr){
            // Stranger survived (even if it didn't defeat the Card)
            score.setSurvivorScore(score.getSurvivorScore() + 1);
        }

        // Creating a new Node and Card to return the battle results
        Node returnedNode = new Node();
        // Create a copy of the card to avoid modifying the original
        Card updatedCard = new Card(node.getCard().getName(),node.getCard().getBaseAttack(),node.getCard().getBaseHealth());
        returnedNode.setCard(updatedCard);

        // Setting the post-battle stats on the new card
        returnedNode.getCard().setCurrentAttack(currentAttack);
        returnedNode.getCard().setCurrentHealth(currentHealth);

        return returnedNode;
    }
}