/**
 * Represents a node in an AVL tree structured by health values.
 * It stores the health value, tracks its height, and counts
 * how many nodes share the same health value.
 */
public class HealthNode {
    // Height of the node, used for tree balancing.
    private int height;

    // Reference to the left child node.
    private HealthNode left;

    // Reference to the right child node.
    private HealthNode right;

    // Reference to the parent node.
    private HealthNode parent;

    // The health points stored in this node.
    private int health;

    // Counter for how many nodes have this exact health value.
    private int duplicateCount;

    // Default constructor.
    HealthNode(){
    }

    /**
     * Sets the height of this node.
     * @param height The new height value.
     */
    public void setHeight(int height){this.height =height;}

    /**
     * Sets the left child of this node.
     * @param left The node to be set as the left child.
     */
    public void setLeft(HealthNode left){this.left =left;}

    /**
     * Sets the right child of this node.
     * @param right The node to be set as the right child.
     */
    public void setRight(HealthNode right){this.right =right;}

    /**
     * Sets the parent of this node.
     * @param parent The node to be set as the parent.
     */
    public void setParent(HealthNode parent){this.parent = parent;}

    /**
     * Sets the health value for this node.
     * @param health The health value to be stored.
     */
    public void setHealth(int health){this.health =health;}

    /**
     * Sets the count of nodes sharing this health value.
     * @param duplicateCount The total count of duplicate nodes.
     */
    public void setDuplicateCount(int duplicateCount) {this.duplicateCount = duplicateCount;}

    /**
     * Gets the left child of this node.
     * @return The left child node, or null if none exists.
     */
    public HealthNode getLeft(){return this.left;}

    /**
     * Gets the right child of this node.
     * @return The right child node, or null if none exists.
     */
    public HealthNode getRight(){return this.right;}

    /**
     * Gets the parent of this node.
     * @return The parent node, or null if this is the root.
     */
    public HealthNode getParent(){return this.parent;}

    /**
     * Gets the height of this node.
     * @return The height value.
     */
    public int getHeight(){return this.height;}

    /**
     * Gets the health value stored in this node.
     * @return The health value.
     */
    public int getHealth(){return this.health;}

    /**
     * Gets the count of nodes sharing this same health value.
     * @return The count of duplicate nodes.
     */
    public int getDuplicateCount() {return duplicateCount;}
}