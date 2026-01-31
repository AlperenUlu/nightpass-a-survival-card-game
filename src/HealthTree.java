/**
 * Implements an AVL tree specifically for managing HealthNode objects to detect
 * whether there is a card which can survive whilst battle.
 * The tree is ordered by the health point attribute of the nodes.
 * It handles duplicate health values by incrementing a counter within the node instead of
 * inserting node to insertion queue which is different from original Tree structure.
 */
public class HealthTree {
    // The root node of the tree.
    private HealthNode root;

    /**
     * Default constructor. Initializes an empty tree.
     */
    HealthTree(){
        this.root = null;
    }
    /**
     * Gets the root node of the tree.
     * @return The root HealthNode, or null if the tree is empty.
     */
    public HealthNode getRoot() {
        return root;
    }

    /**
     * Sets the root node of the tree.
     * @param root The HealthNode to be set as the new root.
     */
    public void setRoot(HealthNode root) {
        this.root = root;
    }
    /**
     * Compares two HealthNodes based on their health value.
     * @param firstNode  The first node.
     * @param secondNode The second node.
     * @return -1 if (health points of) firstNode < secondNode,
     * 1 if (health points of) firstNode > secondNode, 0 if (health points are) equal.
     */
    public int compare(HealthNode firstNode, HealthNode secondNode){
            if (firstNode.getHealth() < secondNode.getHealth()){
                return -1;
            }
            else if (firstNode.getHealth() > secondNode.getHealth()){
                return 1;
            }
            else {
                return 0;
            }
    }
    /**
     * Adds a new HealthNode to the tree.
     * If a node with the same health value already exists, its counter is incremented.
     * Otherwise, the node is inserted and the tree is rebalanced.
     * @param addedNode The HealthNode to add.
     */
    public void add(HealthNode addedNode) {
        if (root == null) {
            // The tree is empty.
            root = addedNode;
            // Initializing the counter for the nodes sharing exactly same values (counting the node itself).
            addedNode.setDuplicateCount(1);
        } else {
            // The tree is not empty.
            HealthNode parent = null;
            HealthNode current = root;
            HealthNode nodeWithSameValue = null;

            // Traverse the tree to find the insertion point or an element sharing the same value.
            while (current != null) {
                int comparedResult = compare(addedNode, current);
                if (comparedResult < 0) {
                    // If health value is smaller, we go left
                    parent = current;
                    current = current.getLeft();
                } else if (comparedResult > 0) {
                    // If health value is bigger, we go right
                    parent = current;
                    current = current.getRight();
                } else { // If the same, we mark the node for future operations.
                    nodeWithSameValue = current;
                    break;
                }
            }
            // There is a node already in the tree with the same value of health
            if(nodeWithSameValue != null){
                // Instead of inserting, we just increment the counter.
                nodeWithSameValue.setDuplicateCount(nodeWithSameValue.getDuplicateCount()+1);
            }
            else{
                // There is any duplicate in the tree, we insert the new node to the tree.
                if (compare(addedNode, parent) < 0) {
                    // If health value is smaller, we attach as the left child
                    parent.setLeft(addedNode);
                }
                else {
                    // If health value is bigger, we attach as the right child
                    parent.setRight(addedNode);
                }
                addedNode.setParent(parent);

                // After insertion, we must update heights and rebalance the tree
                // starting from the parent of the newly inserted node.
                changeHeight(parent);
                balanceTree(parent);
                // Initializing the counter for the nodes sharing exactly same values (counting the node itself).
                addedNode.setDuplicateCount(1);
            }

        }
    }
    /**
     * Removes a HealthNode from the tree based on its health value.
     * If the node has a counter greater than 1, the counter is decremented,
     * because there is another element in this node having the same value.
     * If the counter is 1, the node is physically removed and the tree is rebalanced.
     * @param discardNode A node containing the health value to be discarded.
     * @return A new HealthNode copy of the node that was logically or physically removed.
     */
    public HealthNode discard(HealthNode discardNode){
        HealthNode parent = null;
        HealthNode current = root;
        // Finding the node to be removed
        while (current != null){
            int comparedResult = compare(discardNode,current);
            if (comparedResult < 0){
                parent = current;
                current = current.getLeft();
            }
            else if (comparedResult > 0){
                parent = current;
                current = current.getRight();
            }
            else {// We found the node to be removed, no need to go anywhere else.
                break;
            }
        }

        // current is not null, because the node to be discarded exists,
        // since we found the best card to be discarded in GameManager class.
        int duplicateCount = current.getDuplicateCount();
        if (duplicateCount > 1){
            // There is more than one card has this health value. We just decrement the counter.
            duplicateCount = duplicateCount -1 ;
            current.setDuplicateCount(duplicateCount);

            // Return a copy of the node's data to avoid errors caused by reference issues.
            HealthNode returnedNode = new HealthNode();
            returnedNode.setHealth(current.getHealth());
            return returnedNode;
        }
        // This is the last card with this health value.
        HealthNode deletedNode = new HealthNode();
        deletedNode.setHealth(current.getHealth());

        if(current.getLeft() == null){
            // The node that we delete has does not have a left child.
            if (parent == null){
                // We are deleting the root node
                root = current.getRight();
                if(root !=null){
                    // The newly assigned root has no parent
                    root.setParent(null);
                }

            }
            else{
                // We are deleting a non-root node
                if(compare(discardNode, parent) < 0) {
                    // The node that we delete was a left child.
                    parent.setLeft(current.getRight());
                }
                else{
                    // The node that we delete was a left child.
                    parent.setRight(current.getRight());
                }
                // We update the parent of the child, if it exists.
                if(current.getRight() != null){
                    current.getRight().setParent(parent);
                }
                // We rebalance the tree starting from the parent
                changeHeight(parent);
                balanceTree(parent);

            }

        }
        else{
            // The node that we delete has a left child.
            // We find the rightMost (the biggest node in the left subtree).
            HealthNode parentOfRightMost = current;
            HealthNode rightMost = current.getLeft();
            while(rightMost.getRight() != null){
                parentOfRightMost = rightMost;
                rightMost = rightMost.getRight();
            }
            // Copying the node's data to avoid errors caused by reference issues.
            current.setHealth(rightMost.getHealth());
            current.setDuplicateCount(rightMost.getDuplicateCount());

            // Removing the predecessor node (which has at most one child which is a left child)
            if(parentOfRightMost.getRight() == rightMost){
                parentOfRightMost.setRight(rightMost.getLeft());
            }
            else { // parentOfRightMost == current since our first move was current.getLeft().
                parentOfRightMost.setLeft(rightMost.getLeft());
            }
            if(rightMost.getLeft()!= null){
                // Updating the parent of the child, if it exists.
                rightMost.getLeft().setParent(parentOfRightMost);
            }
            // Rebalancing starting from the rightMost's parent
            changeHeight(parentOfRightMost);
            balanceTree(parentOfRightMost);
        }
        return deletedNode;
    }
    /**
     * Recalculates the height of a node and all its ancestors up to the root.
     * Height of a leaf is 0. Height of null is -1.
     * @param node The node to start recalculating height from.
     */
    public void changeHeight(HealthNode node){
        while(node != null) {
            // Creating a loop upwards from the starting node to the root
            if (node.getLeft() == null && node.getRight() == null) {
                // The node is a leaf node.
                node.setHeight(0);

            } else {
                // The node is an internal node
                int height;
                int leftHeight;
                int rightHeight;
                // Getting height of left child, or -1 if null
                if(node.getLeft()== null){
                    leftHeight = -1;
                }

                else{
                    leftHeight = node.getLeft().getHeight();
                }
                // Getting height of right child, or -1 if null
                if(node.getRight()== null){
                    rightHeight = -1;
                }
                else{
                    rightHeight = node.getRight().getHeight();
                }
                // Current node's height is the maximum of children's heights, plus 1
                if (leftHeight >= rightHeight) {
                    height = leftHeight + 1;
                    node.setHeight(height);
                } else {
                    height =rightHeight + 1;
                    node.setHeight(height);
                }
            }
            // Moving up to the parent to continue the update
            node = node.getParent();
        }

    }
    /**
     * Calculates the height difference of a node.
     * The equation to calculate is:
     * Difference = height(LeftSubtree) - height(RightSubtree).
     * @param node The node to check.
     * @return The balance factor. (Positive if left-heavy, negative if right-heavy).
     */
    public int getHeightDifference(HealthNode node){
        int leftHeight;
        int rightHeight;
        int difference;
        // Getting height of left child, or -1 if null
        if(node.getLeft()== null){
            leftHeight = -1;
        }
        else{
            leftHeight = node.getLeft().getHeight();
        }
        // Getting height of right child, or -1 if null
        if(node.getRight()== null){
            rightHeight = -1;
        }
        else{
            rightHeight = node.getRight().getHeight();
        }
        // Calculating the difference
        difference = leftHeight - rightHeight;
        return difference;
    }
    /**
     * Checks the balance of a node and its parents, performing needed rotations
     * (LL, LR, RR, RL) as needed to maintain AVL properties.
     * @param node The node alpha to start balancing from.
     */
    public void balanceTree(HealthNode node){
        while (node != null){
            // Looping upwards from the starting node to the root
            int difference = getHeightDifference(node);

            if(difference > 1){// Left is higher
                if(getHeightDifference(node.getLeft()) >= 0){// LL case
                    rotateRight(node);
                }
                else {
                    // First, rotating the left child to the left
                    rotateLeft(node.getLeft());
                    // Then, rotating the current node to the right
                    rotateRight(node);
                }
            }
            else if(difference < -1){
                if(getHeightDifference(node.getRight()) <= 0){// RR case
                    rotateLeft(node);
                }
                else {
                    // First, rotating the right child to the right
                    rotateRight(node.getRight());
                    // Then, rotating the current node to the left
                    rotateLeft(node);
                }
            }
            // Moving up to the parent to check its balance
            node = node.getParent();
        }
    }
    /**
     * Performs a right rotation on the given node (for LL or LR imbalance).
     * The left child becomes the new parent of this node.
     * @param node The node to rotate (the 'grandparent' X).
     */
    public void rotateRight(HealthNode node){
        // node = alpha (unbalanced node)
        HealthNode leftChild = node.getLeft(); // Let's call it beta to simplify the naming
        HealthNode leftRightChild = node.getLeft().getRight();// leftRightChild (subtree to move)

        HealthNode parent = node.getParent(); // alpha's original parent

        // Making the subtree to move the new left child of alpha
        node.setLeft(leftRightChild);
        if (leftRightChild != null){
            leftRightChild.setParent(node);
        }
        //Linking beta up to alpha's parent
        if (parent != null){
            leftChild.setParent(parent);
            if (compare(node,parent) < 0){// Alpha was a left child
                parent.setLeft(leftChild);
            }
            else {// Alpha was a right child
                parent.setRight(leftChild);
            }
        }
        else {
            // Alpha was the root. Beta is now the new root.
            leftChild.setParent(null);
            root = leftChild;
        }
        // Linking alpha as the right child of beta
        node.setParent(leftChild);
        leftChild.setRight(node);

        // Updating heights
        changeHeight(node);
    }
    /**
     * Performs a left rotation on the given node (for RR or RL imbalance).
     * The right child becomes the new parent of this node.
     * @param node The node to rotate (the 'grandparent' X).
     */
    public void rotateLeft(HealthNode node){
        // node = alpha (unbalanced node)
        HealthNode rightChild = node.getRight(); // Let's call it beta to simplify the naming
        HealthNode rightLeftChild = node.getRight().getLeft();// rightLeftChild (subtree to move)

        HealthNode parent = node.getParent();// alpha's original parent

        // Making the subtree to move the new left child of alpha
        node.setRight(rightLeftChild);
        if (rightLeftChild != null){
            rightLeftChild.setParent(node);
        }
        //Linking beta up to alpha's parent
        if (parent != null){
            rightChild.setParent(parent);
            if (compare(node,parent) < 0){// Alpha was a right child
                parent.setLeft(rightChild);
            }
            else {// Alpha was a right child
                parent.setRight(rightChild);
            }
        }
        else {
            // Alpha was the root. Beta is now the new root.
            rightChild.setParent(null);
            root = rightChild;
        }

        // Linking alpha as the right child of beta
        node.setParent(rightChild);
        rightChild.setLeft(node);

        // Updating heights
        changeHeight(node);
    }
}
