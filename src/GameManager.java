
/**
 * Manages the core game logic, card collections, and interactions.
 * This class orchestrates the battles, card revivals, and scoring by coordinating
 * between the BattleTree, RevivalTree, HealthTree, and other utility classes.
 */
public class GameManager {
    // data fields
    private BattleTree attackTree;  // Main tree for active cards, sorted by order attack-health-insertion
    private RevivalTree revivalTree; // Tree for discarded cards, sorted by missing health
    private HealthTree healthTree;  // Main tree for active cards, sorted by health
    private Battle battle;          // Handles battle logic
    private Revival revival;        // Handles revival logic
    private ScoreTracker score;     // Tracks survivor and stranger scores
    int deckSize;           // Tracks the number of active cards in play

    /**
     * Constructor for GameManager.
     * Initializes all necessary trees, logic handlers, and trackers.
     */
    GameManager(){
        this.attackTree = new BattleTree("attack");
        this.revivalTree = new RevivalTree();
        this.healthTree = new HealthTree();
        this.battle = new Battle();
        this.revival = new Revival();
        this.score = new ScoreTracker();

    }
    /**
     * Creates a new card and adds it to the decks (AttackTree and HealthTree).
     * @param name The name of the card.
     * @param att The base attack value of the card.
     * @param hp The base health value of the card.
     * @return A string to be written on output file.
     */
    public String drawCard(String name, int att, int hp){
        createNode(name,att,hp);
        return "Added " + name + " to the deck";
    }
    /**
     * Initiates a battle sequence.
     * Finds the best card to play based on priority rules, simulates the battle,
     * and then triggers the revival process.
     * @param att The attack power of the "Stranger".
     * @param hp The health points of the "Stranger".
     * @param heal The amount of healing points available for revival after the battle.
     * @return A string summarizing the battle outcome and revival results.
     */
    public String startBattle(int att, int hp , int heal){
        // No cards in the battle deck to fight with Stranger
        if (isBattleTreeEmpty()){
            int numRevivedCards = 0;

            // Trigger revival process without a newly defeated card
            numRevivedCards= revival.startRevive(revivalTree,attackTree,healthTree,null,heal);

            deckSize += numRevivedCards;
            return "No card to play, " + numRevivedCards + " cards revived" ;

        }
        // Cards are available for battle
        else{
            int priorityNumber;
            int numRevivedCards;

            // Finding the best card based on the 4 priority rules.
            Node bestCard = findBestCard(att,hp,healthTree,attackTree);
            int bestCardHealth = bestCard.getCard().getCurrentHealth();

            // Determining the priority number for the output string
            if (bestCard.getCard().getCurrentAttack() >= hp && bestCard.getCard().getCurrentHealth() >att){
                priorityNumber = 1;
            }
            else if (bestCard.getCard().getCurrentAttack() < hp && bestCard.getCard().getCurrentHealth() > att){
                priorityNumber = 2;
            }
            else if (bestCard.getCard().getCurrentAttack() >= hp && bestCard.getCard().getCurrentHealth() <= att){
                priorityNumber = 3;
            }
            else {
                priorityNumber = 4;
            }
            // Preparing nodes to remove the selected card from both trees
            Node searchAttackNode = new Node();
            HealthNode searchHealthNode = new HealthNode();
            searchAttackNode.setCard(bestCard.getCard());
            searchHealthNode.setHealth(bestCardHealth);

            // Removing the card from both attack(its name is attack tree
            // because it prioritizes attack points before other parameters) and healthTrees
            Node discardedNode = attackTree.discard(searchAttackNode);
            HealthNode discardedHealthNode = healthTree.discard(searchHealthNode);
            deckSize--;

            // Simulating the battle
            Node battledNode = battle.battle(discardedNode,att,hp,score);

            // Checking if the card survived the battle
            if (0 < battledNode.getCard().getCurrentHealth()) {
                // Card survived, adding it back to the trees avoiding reference issues.
                Card survivedCard = battledNode.getCard();
                Node nodeForAttackTree = new Node();
                HealthNode nodeForHealthTree = new HealthNode();

                nodeForAttackTree.setCard(survivedCard);
                nodeForHealthTree.setHealth(survivedCard.getCurrentHealth());

                attackTree.add(nodeForAttackTree);
                healthTree.add(nodeForHealthTree);
                deckSize++;

                // Start revival process for the cards died in previous turns (no card died this turn)
                numRevivedCards = revival.startRevive(revivalTree,attackTree,healthTree, null, heal);
                //If a card revived, we increment the size of active battle tree
                deckSize += numRevivedCards;
            }
            else {
                // Card died, starting revival process and passing the dead card to it
                numRevivedCards = revival.startRevive(revivalTree,attackTree,healthTree,battledNode,heal);
                //If a card revived, we increment the size of active battle tree
                deckSize += numRevivedCards;
            }

            // Formatting the output string based on priority and revival.
            if (priorityNumber == 1 || priorityNumber == 2){
                return "Found with priority " + priorityNumber + ", Survivor plays "+battledNode.getCard().getName()+", the played card returned to deck, "+ numRevivedCards +" cards revived";
            }
            else {
                return "Found with priority "+ priorityNumber +", Survivor plays "+battledNode.getCard().getName()+", the played card is discarded, "+ numRevivedCards +" cards revived";
            }

        }
    }
    /**
     * Allows the "Stranger" to steal a card from the deck based on specific criteria.
     * The card must have higher attack AND higher health than the provided values.
     * @param att The attack threshold.
     * @param hp The health threshold.
     * @return A string indicating which card was stolen, or that no card could be stolen.
     */
    public String stealCard(int att, int hp){
        if(isBattleTreeEmpty()){
            return "No card to steal";
        }
        // Checking if any card in the tree even meets the criteria
        else if(!isExceedAttackLimit(att,attackTree) || !isExceedHealthLimit(hp,healthTree)){
            return "No card to steal";
        }
        else{
            // Finding the card with the lowest attack that is still > attack of Stranger.
            Node current = attackTree.getRoot();
            Node optimalNode = null;
            while (current != null) {
                if (current.getCard().getCurrentAttack() > att) {
                    optimalNode = current;
                    current = current.getLeft();
                } else {
                    current = current.getRight();
                }
            }

            // From that node's subtree (cards with same attack), we find the card with the lowest health > hp of Stranger
            Node optimalNodeSubTree = findMinHealthStealCard(optimalNode, hp);
            // If no card in that subtree qualifies, we check the next best attack node
            while (optimalNodeSubTree == null && optimalNode != null) {
                optimalNode = findOptimalNodeStealCard(optimalNode);// Finds next inorder successor
                optimalNodeSubTree = findMinHealthStealCard(optimalNode, hp);
            }
            if (optimalNodeSubTree != null){
                // A card was found, remove it from both trees avoiding reference issues.
                Node searchAttackNode = new Node();
                HealthNode searchHealthNode = new HealthNode();
                searchAttackNode.setCard(optimalNodeSubTree.getCard());
                searchHealthNode.setHealth(optimalNodeSubTree.getCard().getCurrentHealth());

                Node discardedNode = attackTree.discard(searchAttackNode);
                HealthNode discardedHealthNode = healthTree.discard(searchHealthNode);
                deckSize--;

                // Generating the output if a card is stolen by stranger
                String cardName = discardedNode.getCard().getName();
                return "The Stranger stole the card: " + cardName;
            }
            else {
                // Generating the output if a card cannot be stolen by stranger
                return "No card to steal";
            }

        }
    }
    /**
     * Finds the best card to play in a battle according to 4 priority rules.
     * (Can Kill, Can Survive): Lowest Attack > hp, then Lowest Health > att
     * (Can't Kill, Can Survive): Highest Attack < hp, then Lowest Health > att
     * (Can Kill, Can't Survive): Lowest Attack > hp, then Lowest Health
     * (Can't Kill, Can't Survive): Highest Attack < hp, then Lowest Health
     * @param att Stranger's attack
     * @param hp Stranger's health
     * @param healthTree HealthTree for survival checks
     * @param attackTree BattleTree for kill checks
     * @return The Node containing the best card to play.
     */
    private Node findBestCard(int att, int hp, HealthTree healthTree, BattleTree attackTree) {
        // Node to be returned, found in a health-sorted subtree (order is attack-health-insertion)
        Node optimalNodeSubTree = null;

        // Check if we have any card that can survive the enemy's attack
        if (canSurvive(att, healthTree)) {
            // We can survive. This is either Priority 1 or 2.
            if (!canKill(hp, attackTree)) {
                // Priority 2: Finding the card with the highest attack that is still less than the enemy's hp.
                Node current = attackTree.getRoot();
                Node optimalNode = null;
                while (current != null) {

                    if (current.getCard().getCurrentAttack() < hp) {
                        optimalNode = current; // This is a potential candidate
                        current = current.getRight(); // Try to find a higher attack (but still < hp)
                    } else {
                        current = current.getLeft(); // This attack is too high (>= hp)
                    }
                }

                // 2. From that node's subtree, find the card with the lowest health that can survive.
                optimalNodeSubTree = findMinHealthPriorityOneTwo(optimalNode, att);

                // 3. If no card in that subtree can survive, check the next-best attack node (in-order predecessor).
                while (optimalNodeSubTree == null && optimalNode != null) {
                    optimalNode = findOptimalNodePriorityTwo(optimalNode); // Get previous attack value
                    optimalNodeSubTree = findMinHealthPriorityOneTwo(optimalNode, att);
                }
            } else {
                // Priority 1: Finding the card with the lowest attack that is greater than or equal to the enemy's hp.
                Node current = attackTree.getRoot();
                Node optimalNode = null;
                while (current != null) {
                    if (current.getCard().getCurrentAttack() >= hp) {
                        optimalNode = current; // This is a potential candidate
                        current = current.getLeft(); // Try to find a lower attack (but still >= hp)
                    } else {
                        current = current.getRight(); // This attack is too low
                    }
                }

                // From that node's subtree, find the card with the lowest health that can survive.
                optimalNodeSubTree = findMinHealthPriorityOneTwo(optimalNode, att);

                // If no card in that subtree can survive, check the next-best attack node (in-order successor).
                while (optimalNodeSubTree == null && optimalNode != null) {
                    optimalNode = findOptimalNodePriorityOne(optimalNode); // Get next (higher) attack value
                    optimalNodeSubTree = findMinHealthPriorityOneTwo(optimalNode, att);
                }

                // Priority 1 fails, default to Priority 2,
                // because there are cards that can survive or kill, but not at the same time
                if (optimalNodeSubTree == null) {
                    // Rerun the logic for Priority 2
                    current = attackTree.getRoot();
                    optimalNode = null;
                    while (current != null) {
                        if (current.getCard().getCurrentAttack() < hp) {
                            optimalNode = current;
                            current = current.getRight();
                        } else {
                            current = current.getLeft();
                        }
                    }
                    optimalNodeSubTree = findMinHealthPriorityOneTwo(optimalNode, att);
                    while (optimalNodeSubTree == null && optimalNode != null) {
                        optimalNode = findOptimalNodePriorityTwo(optimalNode);
                        optimalNodeSubTree = findMinHealthPriorityOneTwo(optimalNode, att);
                    }
                }
            }
        } else {
            // No card can survive the hit. This is either Priority 3 or 4.

            if (!canKill(hp, attackTree)) {
                // Priority 4: Find the card with the HIGHEST attack that is LESS than the enemy's HP.
                Node current = attackTree.getRoot();
                Node optimalNode = null;
                while (current != null) {
                    if (current.getCard().getCurrentAttack() < hp) {
                        optimalNode = current;
                        current = current.getRight();
                    } else {
                        current = current.getLeft();
                    }
                }
                // From that subtree, find the card with the absolute lowest health.
                optimalNodeSubTree = findMinHealth(optimalNode);
            } else {
                // Priority 3: Find the card with the lowest attack that is greater than or equal to the enemy's hp.
                Node current = attackTree.getRoot();
                Node optimalNode = null;
                while (current != null) {
                    if (current.getCard().getCurrentAttack() >= hp) {
                        optimalNode = current;
                        current = current.getLeft();
                    } else {
                        current = current.getRight();
                    }
                }
                // From that subtree, find the card with the absolute lowest health.
                optimalNodeSubTree = findMinHealth(optimalNode);
            }
        }

        // Return the chosen card
        return optimalNodeSubTree;
    }
    /**
     * Finds the in-order successor of a node in the BattleTree (used for Priority 1).
     * This finds the node with the next highest attack value.
     * @param node The current node.
     * @return The next node in in-order traversal.
     */
    private Node findOptimalNodePriorityOne(Node node){
        // Current node has a right child.
        // The successor is the leftmost node in the right subtree.
        if(node.getRight() != null){
            Node nextCandidate = node.getRight();
            while (nextCandidate.getLeft() != null){
                nextCandidate = nextCandidate.getLeft();
            }
            return nextCandidate;
        }
        // Current node has no right child.
        // The successor is the lowest parent whose left child is also an parent.
        else{
            Node parent = node.getParent();
            Node current = node;
            // Going up the tree as long as we are the right child
            while(parent != null && current == parent.getRight()){
                current = parent;
                parent = parent.getParent();
            }
            // The first time we are a left child, that parent is the successor
            return parent;
        }
    }
    /**
     * Finds the in-order predecessor of a node in the BattleTree (used for Priority 2).
     * This finds the node with the next lowest attack value.
     * @param node The current node.
     * @return The previous node in in-order traversal.
     */
    private Node findOptimalNodePriorityTwo(Node node){
        // Node has a left child.
        // The predecessor is the rightmost node in the left subtree.
        if(node.getLeft() != null){
            Node nextCandidate = node.getLeft();
            while (nextCandidate.getRight() != null){
                nextCandidate = nextCandidate.getRight();
            }
            return nextCandidate;
        }
        // Case 2: Node has no left child.
        // The predecessor is the lowest parent whose right child is also an parent.
        else{
            Node parent = node.getParent();
            Node current = node;
            // Going up the tree as long as we are the left child
            while(parent != null && current == parent.getLeft()){
                current = parent;
                parent = parent.getParent();
            }
            // The first time we are a right child, that parent is the predecessor
            return parent;
        }
    }
    /**
     * Finds the in-order successor of a node in the BattleTree (used for stealCard).
     * This finds the node with the *next highest* attack value.
     * @param node The current node.
     * @return The next node in in-order traversal.
     */
    private Node findOptimalNodeStealCard(Node node){
        // Logic is identical to findOptimalNodePriorityOne
        // Its name is different to emphasize the intention when using it
        if(node.getRight() != null){
            Node nextCandidate = node.getRight();
            while (nextCandidate.getLeft() != null){
                nextCandidate = nextCandidate.getLeft();
            }
            return nextCandidate;
        }
        else{
            Node parent = node.getParent();
            Node current = node;
            while(parent != null && current == parent.getRight()){
                current = parent;
                parent = parent.getParent();
            }
            return parent;
        }
    }
    /**
     * Finds the card with the minimum health that can survive in a given node's subtree.
     * The subtree is from the BattleTree, and it is sorted by health.
     * @param optimalNode The node (from the main AttackTree) whose health-subtree we search.
     * @param attackStr The Stranger's attack (health threshold to survive).
     * @return The node with the minimum health > attackStr, or null.
     */
    private Node findMinHealthPriorityOneTwo(Node optimalNode, int attackStr){
        // Checking if the battle tree is empty
        if (isBattleTreeEmpty()){
            return null;
        }
        else{
            // Checking if the node or its subtree is empty
            if (optimalNode == null || optimalNode.getSubTree().getRoot() == null) {
                return null;
            }

            Node current = optimalNode.getSubTree().getRoot();
            Node optimalNodeSubTree = null;

            // Searching the health-sorted subtree
            while (current != null) {
                // Check if this card can survive
                if (current.getCard().getCurrentHealth() > attackStr) {
                    optimalNodeSubTree = current;
                    // Trying to find a card with even lower health that can also survive
                    current = current.getLeft();
                } else {
                    // This card cannot survive. Checking cards with higher health.
                    current = current.getRight();
                }
            }

            return optimalNodeSubTree;
        }
    }
    /**
     * Finds the card with the minimum health above a limit in a given node's subtree.
     * Used for the stealCard logic. The subtree is sorted by health.
     * @param optimalNode The node (from the main AttackTree) whose health-subtree we search.
     * @param hpStr The health limit to steal.
     * @return The node with the minimum health > hpStr, or null.
     */
    private Node findMinHealthStealCard(Node optimalNode, int hpStr){
        // Checking if the battle tree is empty
        if (isBattleTreeEmpty()){
            return null;
        }
        else{
            // Checking if the node or its subtree is empty
            if (optimalNode == null || optimalNode.getSubTree().getRoot() == null) {
                return null;
            }

            Node current = optimalNode.getSubTree().getRoot();
            Node optimalNodeSubTree = null;

            // Searching the health-sorted subtree
            while (current != null) {
                // Checking if this card meets the health criteria
                if (current.getCard().getCurrentHealth() > hpStr) {
                    optimalNodeSubTree = current;
                    // Trying to find a card with even lower health that also meets the criteria
                    current = current.getLeft();
                } else {
                    // This card's health is too low. Checking cards with higher health.
                    current = current.getRight();
                }
            }

            return optimalNodeSubTree;
        }
    }
    /**
     * Finds the card with the absolute minimum health in a given node's subtree.
     * Used for Priorities 3 and 4, where survival is not possible.
     * @param optimalNode The node (from the main AttackTree) whose health-subtree we search.
     * @return The node with the minimum health.
     */
    private Node findMinHealth(Node optimalNode){
        // Checking if the battle tree is empty
        if (isBattleTreeEmpty()){
            return null;
        }
        else{
            // Checking if the node or its subtree is empty
            if (optimalNode == null || optimalNode.getSubTree().getRoot() == null) {
                return null;
            }

            Node current = optimalNode.getSubTree().getRoot();
            Node optimalNodeSubTree = null;

            // In a health-sorted AVL, the minimum is the leftmost node.
            while (current != null) {
                optimalNodeSubTree = current;
                current = current.getLeft();
            }

            return optimalNodeSubTree;
        }
    }
    /**
     * Checks if the BattleTree is empty.
     * @return true if empty, false otherwise.
     */
    private boolean isBattleTreeEmpty(){
        if(attackTree.getRoot() == null){
            return true;
        }
        return false;
    }
    /**
     * Checks if any card in the deck can survive a hit of attackStr.
     * @param attackStr The incoming attack value.
     * @param healthTree The HealthTree to check sorted by health.
     * @return true if at least one card has health > attackStr, false otherwise.
     */
    private boolean canSurvive(int attackStr,HealthTree healthTree){
        if(healthTree.getRoot() ==null){
            return false;
        }
        HealthNode current = healthTree.getRoot();
        while (current != null){
            if(current.getHealth() > attackStr){
                return true;
            }
            else{
                current = current.getRight();
            }
        }
        return false;

    }
    /**
     * Checks if any card in the deck can kill an enemy with healthStr.
     * @param healthStr The enemy's health value.
     * @param attackTree The BattleTree to check.
     * @return true if at least one card has attack >= healthStr, false otherwise.
     */
    private boolean canKill(int healthStr, BattleTree attackTree){
        if(attackTree.getRoot() ==null){
            return false;
        }
        Node current = attackTree.getRoot();
        while (current != null){
            if(current.getCard().getCurrentAttack() >= healthStr){
                return true;
            }
            else{
                current = current.getRight();
            }
        }
        return false;

    }
    /**
     * Checks if any card in the HealthTree has health greater than health limit.
     * Used for the stealCard pre-check.
     * @param healthStr The health limit.
     * @param healthTree The tree to check.
     * @return true if a card exists with health > health limit, false otherwise.
     */
    private boolean isExceedHealthLimit(int healthStr,HealthTree healthTree){
        if(healthTree.getRoot() ==null){
            return false;
        }
        HealthNode current = healthTree.getRoot();
        while (current != null){
            if(current.getHealth() > healthStr){
                return true;
            }
            else{
                current = current.getRight();
            }
        }
        return false;

    }
    /**
     * Checks if any card in the BattleTree has attack greater than attack limit.
     * Used for the stealCard pre-check.
     * @param attackStr The attack limit.
     * @param attackTree The tree to check.
     * @return true if a card exists with attack > attack limit, false otherwise.
     */
    private boolean isExceedAttackLimit(int attackStr, BattleTree attackTree){
        if(attackTree.getRoot() ==null){
            return false;
        }
        Node current = attackTree.getRoot();
        while (current != null){
            if(current.getCard().getCurrentAttack() > attackStr){
                return true;
            }
            else{
                current = current.getRight();
            }
        }
        return false;

    }
    /**
     * Helper method to create a new card and add its corresponding nodes
     * to both the AttackTree and HealthTree.
     * @param name Card name.
     * @param att Card base attack.
     * @param hp Card base health.
     */
    private void createNode(String name, int att, int hp){
        // Creating the single Card object
        Card card = new Card(name,att,hp);

        // Creating the two nodes that will reference this card or card's features.
        Node nodeAttackTree = new Node();
        HealthNode nodeHealthTree = new HealthNode();

        // Setting the data for each node
        nodeAttackTree.setCard(card);
        nodeHealthTree.setHealth(hp);

        // Adding to both trees to maintain synchronization
        attackTree.add(nodeAttackTree);
        healthTree.add(nodeHealthTree);
        deckSize++;
    }
    /**
     * Determines the winner of the game based on the final scores.
     * @return A string declaring the winner and their score.
     */
    public String findWinner(){
        int survivorScore = score.getSurvivorScore();
        int strangerScore = score.getStrangerScore();
        if(survivorScore >= strangerScore){
            return "The Survivor, Score: " + survivorScore;
        }
        else {
            return "The Stranger, Score: " + strangerScore;
        }
    }
    /**
     * Gets the current number of cards in the active deck.
     * @return A string reporting the deck size.
     */
    public String countDeck(){
        return "Number of cards in the deck: " + deckSize;
    }

    /**
     * Gets the current number of cards in the discard pile (RevivalTree).
     * @return A string reporting the discard pile size.
     */
    public String countDiscardDeck(){

        return "Number of cards in the discard pile: " + revival.getDiscardedDeckSize();
    }

}

