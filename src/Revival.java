
/**
 * Manages the process of reviving discarded cards from the RevivalTree.
 * This class formulates the logic for a single revival event,
 * using a finite pool of heal points to restore health to cards.
 * Fully revived cards are returned to the active BattleTree and HealthTree
 */
public class Revival {
    private int heal; // Stores the total healing points available for the current revival event.
    private int discardedDeckSize; // Tracks the current number of cards in the revivalTree.
    private int numRevivedCards; // Counts how many cards are successfully fully revived during this event.

    /**
     * Default constructor for the Revival process.
     * Initializes a new Revival instance.
     * State variables are expected to be initialized by the startRevive method.
     */
    Revival(){
    }
    /**
     * Initiates and manages the entire card revival process.
     * This method first adds the newly defeated card (if any) to the RevivalTree.
     * It then iteratively uses the available heal points to revive cards from the RevivalTree,
     * prioritizing based on the logic in findBestCard(RevivalTree).
     * Fully revived cards are re-added into the attackTree (BattleTree object) and healthTree.
     * @param revivalTree The tree structure holding all discarded cards waiting for revival.
     * @param attackTree  The main battle tree sorted by given order (Order:Attack-Health-InsertionOrder)
     *                    where revived cards are added.
     * @param healthTree  The secondary battle tree sorted by health where revived cards are added.
     * @param battledNode The node containing the card that was just defeated in battle.
     *                    Can be null if revival is triggered without a new card dying.
     * @param heal        The total amount of healing points available for this revival event.
     * @return An ArrayList containing two integers:
     * index 0: The total number of cards fully revived in this event.
     * index 1: The total number of cards added back to the battle decks.
     */
    public int startRevive(RevivalTree revivalTree , BattleTree attackTree, HealthTree healthTree, Node battledNode, int heal){
        this.heal = heal;
        numRevivedCards = 0;

        // Processing the card that was just defeated, if one was provided.
        if (battledNode != null){
            // Getting the card that just died
            Card deadCard = battledNode.getCard();

            // Create a new Card instance for the revival tree to avoid reference issues.
            Card newCardForRevival = new Card(deadCard.getName(),deadCard.getBaseAttack(), deadCard.getBaseHealth());
            newCardForRevival.setCurrentAttack(deadCard.getCurrentAttack());
            newCardForRevival.setCurrentHealth(0);

            // The missing health for revival is its total base health, since the card has just died.
            int missingHealth = deadCard.getBaseHealth();
            newCardForRevival.setMissingHealth(missingHealth);

            // Creating a new node for the revival tree to avoid reference issues and add it
            Node nodeForRevivalTree = new Node();
            nodeForRevivalTree.setCard(newCardForRevival);
            revivalTree.add(nodeForRevivalTree);
            discardedDeckSize++;
        }
        // Main revival loop: it continues as long as we have healing points and cards in the revival tree
        while(this.heal > 0 && !isTreeEmpty(revivalTree)){
            // Finding the best card to heal based on our current heal amount
            Node nodeToRevive = findBestCard(revivalTree);
            // Removing that card from the revival tree to process it
            Node discardedNode = revivalTree.discard(nodeToRevive);
            discardedDeckSize --;
            // Applying healing to the card which may be partial or full
            Node revivedNode= revive(discardedNode);
            // Checking if the card was fully revived
            if (revivedNode.getCard().getMissingHealth() == 0){

                // Creating a corresponding HealthNode for the HealthTree
                HealthNode revivedHealthNode = new HealthNode();
                revivedHealthNode.setHealth(revivedNode.getCard().getBaseHealth());

                // Restoring its current health to its base health
                revivedNode.getCard().setCurrentHealth(revivedNode.getCard().getBaseHealth());

                // Adding the revived card back to the active battle trees
                attackTree.add(revivedNode);
                healthTree.add(revivedHealthNode);
                numRevivedCards ++;

            }
            else{
                // Card was only partially revived
                // Adding the partially healed node back into the revival tree
                revivalTree.add(revivedNode);
                discardedDeckSize++;
            }

        }
        // Prepare the result arrayList with the final counts
        return numRevivedCards;
    }
    /**
     * Applies heal points to a single node and calculates the appropriate attack penalty.
     * If the card is fully revived, its attack is reduced by 10%.
     * If the card is only partially revived, its attack is reduced by 5%.
     * * @param nodeToRevive The node (and its card) to apply healing to.
     * @return A new Node containing a new Card object with updated stats (missingHealth and baseAttack).
     */
    private Node revive(Node nodeToRevive){
        Card cardToRevive = nodeToRevive.getCard();
        int missingHealth = cardToRevive.getMissingHealth();
        int baseAttack = cardToRevive.getBaseAttack();

        // Checking if we have enough heal to fully revive this card
        if (heal >= missingHealth){
            //Fully revival
            heal = heal - missingHealth; // Spending the required heal
            missingHealth = 0;
            baseAttack = (int) (baseAttack * 0.90);
        }
        else {
            // Partial revival
            missingHealth = missingHealth - heal; // Applying all remaining heal
            heal = 0;
            baseAttack = (int) (baseAttack * 0.95);
        }

        // Creating a new Node and Card to hold the updated state
        Node returnedNode = new Node();
        Card returnedCard = new Card(cardToRevive.getName(),baseAttack,cardToRevive.getBaseHealth());
        returnedCard.setMissingHealth(missingHealth);
        returnedNode.setCard(returnedCard);

        // Setting the post-revival attack stat
        returnedNode.getCard().setCurrentAttack(baseAttack);
        return returnedNode;
    }
    /**
     * Utility method to check if the revival tree is empty.
     * @param revivalTree The tree to check.
     * @return true if the tree's root is null, false otherwise.
     */
    private boolean isTreeEmpty(RevivalTree revivalTree){
        // The tree is considered empty if its root node is null
        if(revivalTree.getRoot() == null){
            return true;
        }
        return false;
    }
    /**
     * Selects the optimal card to revive from the RevivalTree based on available heal.
     * The RevivalTree is sorted by 'missingHealth'.
     * If we can fully revive any card, We find the card with the highest missingHealth.
     * that is still higher than or equal to our current heal.
     * If we cannot fully revive any card, we find the card with the lowest missingHealth
     * and apply all remaining partial heal to it.
     * @param revivalTree The tree to search.
     * @return The Node that is the best candidate for revival.
     */
    private Node findBestCard(RevivalTree revivalTree){
        Node optimalNode = null;
        // Checking if a full revival is possible for at least one card
        if (canFullyRevive(revivalTree)){
            Node current = revivalTree.getRoot();
            // Check if we can heal this card
            while (current != null) {
                if (current.getCard().getMissingHealth() <= heal) {
                    // This is our current best candidate
                    optimalNode = current;
                    current = current.getRight();
                } else {
                    // This card misses too much heal. Checking the left subtree.
                    current = current.getLeft();
                }
            }
        }
        else {
            // No full revival is possible.
            // Finding the card with the *lowest* missing health to apply a partial heal.
            Node current = revivalTree.getRoot();
            while (current != null) {
                optimalNode = current;
                current = current.getLeft();
            }
        }
        return optimalNode;

    }
    /**
     * Helper method to determine if the current 'heal' is sufficient to fully revive at least one card in the tree.
     * Tree is sorted by missingHealth.
     * @param revivalTree The tree to check.
     * @return true if any card can be fully revived, false otherwise.
     */
    private boolean canFullyRevive(RevivalTree revivalTree){
        if(revivalTree.getRoot() ==null){
            return false;
        }
        // We only need to check the card lowest missing health
        Node current = revivalTree.getRoot();
        while (current != null){
            if(heal >= current.getCard().getMissingHealth()){
                // At least one card is revivable.
                return true;
            }
            else{
                // This card costs too much. Since the tree is sorted, we only need to check the left subtree .
                current = current.getLeft();
            }
        }
        // We traversed all the way left and couldn't find the card having the least missing health.
        return false;
    }

    /**
     * Gets the current count of cards in the discarded deck (revivalTree).
     * @return The size of the discarded deck.
     */
    public int getDiscardedDeckSize() {
        return discardedDeckSize;
    }
}
