/**
 * Represents a single node within an AVL tree structure (for BattleTree and RevivalTree).
 * This node manages references to its left and right child, its parent,
 * its height, and contains a Card object.
 * It also holds a 'subTree' and an 'insertionQueue'.
 */
public class Node {
    // Height of the node, used for AVL tree balancing.
    private int height;

    // Reference to the left child node.
    private Node left;

    // Reference to the right child node.
    private Node right;

    // Reference to the parent node.
    private Node parent;

    // The Card object stored in this node.
    private Card card;

    // A nested tree structure associated with this node, for the cards one stat in common (attack or health).
    private BattleTree subTree;

    // A queue associated with this node, for the cards have exactly same stats.
    private Queue insertionQueue;

    // Default constructor.
    Node(){}

    /**
     * Sets the height of this node.
     * @param height The new height value.
     */
    public void setHeight(int height){this.height =height;}

    /**
     * Sets the left child of this node.
     * @param left The node to be set as the left child.
     */
    public void setLeft(Node left){this.left =left;}

    /**
     * Sets the right child of this node.
     * @param right The node to be set as the right child.
     */
    public void setRight(Node right){this.right =right;}

    /**
     * Sets the parent of this node.
     * @param parent The node to be set as the parent.
     */
    public void setParent(Node parent){this.parent = parent;}

    /**
     * Sets the card (data) for this node.
     * @param card The Card to be stored.
     */
    public void setCard(Card card){this.card =card;}

    /**
     * Sets the subtree associated with this node.
     * @param subTree The Tree to be associated with this node.
     */
    public void setSubTree(BattleTree subTree) {this.subTree = subTree;}

    /**
     * Sets the insertion queue associated with this node.
     * @param insertionQueue The Queue to be associated with this node.
     */
    public void setInsertionQueue(Queue insertionQueue) {this.insertionQueue = insertionQueue;}

    /**
     * Gets the left child of this node.
     * @return The left child node, or null if none exists.
     */
    public Node getLeft(){return this.left;}

    /**
     * Gets the right child of this node.
     * @return The right child node, or null if none exists.
     */
    public Node getRight(){return this.right;}

    /**
     * Gets the parent of this node.
     * @return The parent node, or null if this is the root.
     */
    public Node getParent(){return this.parent;}

    /**
     * Gets the height of this node.
     * @return The height value.
     */
    public int getHeight(){return this.height;}

    /**
     * Gets the card stored in this node.
     * @return The Card object.
     */
    public Card getCard(){return this.card;}

    /**
     * Gets the subtree associated with this node.
     * @return The associated Tree, or null if none exists.
     */
    public BattleTree getSubTree() {return subTree;}

    /**
     * Gets the insertion queue associated with this node.
     * @return The associated Queue, or null if none exists.
     */
    public Queue getInsertionQueue() {return insertionQueue;}

}

