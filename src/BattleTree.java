
/**
 * Implements a self-balancing AVL tree (BattleTree) to store game nodes.
 * This tree can be sorted by attack or health based on its 'treeType'.
 * It handles duplicate primary values (same attack or health)
 * by using a nested subtree that sorts by the secondary value (e.g., health).
 * Subtrees handle their duplicates using a simple Queue.
 * This tree have three layers: attack - health - insertion order
 * If there is same value in one layer, we go to a layer down.
 */
public class BattleTree {
    private Node root;
    private String treeType; // "attack" or "health", controls the comparison logic
    private boolean isSubtree; // True if this tree is a nested subtree

    /**
     * Main constructor for a primary BattleTree (e.g., the main attackTree).
     * @param treeType The attribute to sort by ("attack" or "health").
     */
    BattleTree(String treeType) {
        this.root = null;
        this.treeType = treeType;
        this.isSubtree = false;
    }

    /**
     * Overloaded constructor used internally to create a subtree.
     * @param treeType The attribute to sort by (usually the opposite of the parent tree).
     * @param isSubtree Flag indicating this is a nested tree.
     */
    BattleTree(String treeType, boolean isSubtree) {
        this.root = null;
        this.treeType = treeType;
        this.isSubtree = isSubtree;
    }

    /**
     * Gets the root node of the tree.
     * @return The root node.
     */
    public Node getRoot() {
        return root;
    }

    /**
     * Compares two nodes based on the tree's 'treeType'.
     * @param firstNode The first node.
     * @param secondNode The second node.
     * @return -1 if first < second, 1 if first > second, 0 if equal.
     */
    public int compare(Node firstNode, Node secondNode) {
        if (treeType.equals("health")) {
            if (firstNode.getCard().getCurrentHealth() < secondNode.getCard().getCurrentHealth()) {
                return -1;
            } else if (firstNode.getCard().getCurrentHealth() > secondNode.getCard().getCurrentHealth()) {
                return 1;
            } else {
                return 0; // Healths are equal
            }
        } else { // "attack"
            if (firstNode.getCard().getCurrentAttack() < secondNode.getCard().getCurrentAttack()) {
                return -1;
            } else if (firstNode.getCard().getCurrentAttack() > secondNode.getCard().getCurrentAttack()) {
                return 1;
            } else {
                return 0; // Attacks are equal
            }
        }
    }

    /**
     * Adds a new node to the tree, maintaining AVL balance.
     * If a node with the same primary value exists, it's added to that node's
     * subtree (for main trees) or insertion queue (for subtrees).
     * @param addedNode The node to add.
     */
    public void add(Node addedNode) {
        // Tree is empty
        if (root == null) {
            root = addedNode;
            // Initializing the node's duplicate-handling structure
            if (!isSubtree) {
                initializeSubTree(root);
            } else {
                initializeQueue(root);
            }
            return;
        }

        Node parent = null;
        Node current = root;
        Node nodeWithSameValue = null;

        // Traversal to find the insertion point
        while (current != null) {
            parent = current;
            int comparedResult = compare(addedNode, current);

            if (comparedResult < 0) {
                current = current.getLeft();
            } else if (comparedResult > 0) {
                current = current.getRight();
            } else {
                // Duplicate value found
                nodeWithSameValue = current;
                break;
            }
        }

        // Duplicate found. Adding to sub structure
        // subtree or insertion queue based on tree type since the order is attack-health-insertion queue.
        if (nodeWithSameValue != null) {

            if (nodeWithSameValue.getSubTree() == null) {
                // This is a subtree node, so it has an insertionQueue
                QueueNode queueNode = new QueueNode();
                queueNode.setCard(addedNode.getCard());
                nodeWithSameValue.getInsertionQueue().enqueue(queueNode);
            } else {
                // This is a main tree node, so it has a subTree
                Node subNode = new Node();
                subNode.setCard(addedNode.getCard());
                nodeWithSameValue.getSubTree().add(subNode);
            }

        } else {
            // No duplicate. Inserting as a new leaf in the main tree.
            if (compare(addedNode, parent) < 0) {
                parent.setLeft(addedNode);
            } else {
                parent.setRight(addedNode);
            }
            addedNode.setParent(parent);

            // Updating heights and rebalancing the tree starting from the parent
            changeHeight(parent);
            balanceTree(parent);

            // After insertion, initializing the new node's duplicate-handling structure
            if (!isSubtree) {
                initializeSubTree(addedNode);
            } else {
                initializeQueue(addedNode);
            }
        }
    }
    /**
     * Removes a node from the tree, maintaining AVL balance.
     * Handles deletion from the main tree, a subtree, or an insertion queue.
     * @param discardNode A node containing the card data to be discarded.
     * @return The Node that was actually removed (containing the correct card data).
     */
    public Node discard(Node discardNode) {
        Node parent = null;
        Node current = root;

        // Finding the node in the main tree with the matching primary value
        while (current != null) {
            int comparedResult = compare(discardNode, current);
            if (comparedResult < 0) {
                parent = current;
                current = current.getLeft();
            } else if (comparedResult > 0) {
                parent = current;
                current = current.getRight();
            } else {
                break; // Found the node with the matching primary value
            }
        }

        if (current == null) {
            return null; // Node not found, this is not the case in our tree (explained in other tree classes)
        }

        // Deciding how to handle the deletion (SubTree, Queue, or MainTree)
        BattleTree subTree = current.getSubTree();
        Queue insertionQueue = current.getInsertionQueue();

        if (subTree != null) {
            // This is a main node. Discarding from its subtree.
            Node discardedFromSubtree = subTree.discard(discardNode);

            if (subTree.getRoot() == null) {
                // The subtree is now empty. This means we must delete the main current node.
                // Fall through to the deletion logic below.
            } else {
                // The subtree still has nodes. Updating the main node's card
                // to match the new root of the subtree (which is the new best secondary card).
                current.setCard(subTree.getRoot().getCard());
                return discardedFromSubtree; // Returning the card we actually removed
            }

        } else if (insertionQueue != null) {
            // This is a subtree node. Discarding from its queue.
            QueueNode discardedQueueNode = insertionQueue.dequeue();
            Node returnedNode = new Node();
            returnedNode.setCard(discardedQueueNode.getCard());

            if (insertionQueue.getHead() == null) {
                // The queue is now empty. This means we must delete this subtree node.
                // Fall through to deletion logic below.
            } else {
                // The queue still has nodes. Updating this node's card
                // to match the new head of the queue.
                current.setCard(insertionQueue.getHead().getCard());
                return returnedNode; // Return the card we actually removed
            }
        }

        // This code is reached if:
        // The node had no duplicates (subTree/queue was null)
        // or the node's subTree/queue became empty after discarding from it.

        Node deletedNode = new Node(); // Storing the card we are about to delete
        deletedNode.setCard(current.getCard());

        // Deletion logic
        if (current.getLeft() == null) {
            // Node has not a left child
            if (parent == null) {
                root = current.getRight();
                if (root != null) {
                    root.setParent(null);
                }
            } else {
                if (compare(discardNode, parent) < 0) {
                    parent.setLeft(current.getRight());
                } else {
                    parent.setRight(current.getRight());
                }
                if (current.getRight() != null) {
                    current.getRight().setParent(parent);
                }
                // Starting to balance from the parent
                changeHeight(parent);
                balanceTree(parent);
            }
        } else {
            // Node has a left child
            // Finding the rightmost node of the left subtree
            Node parentOfRightMost = current;
            Node rightMost = current.getLeft();
            while (rightMost.getRight() != null) {
                parentOfRightMost = rightMost;
                rightMost = rightMost.getRight();
            }

            // Replacing the current node's data with the rightMost's data
            // This also copies the rightMost's sub-structure (subtree or queue)
            current.setCard(rightMost.getCard());
            current.setSubTree(rightMost.getSubTree());
            current.setInsertionQueue(rightMost.getInsertionQueue());

            // Unlinking the predecessor's structures to avoid dual references
            rightMost.setSubTree(null);
            rightMost.setInsertionQueue(null);

            // Removing the rightMost from its original location
            if (parentOfRightMost.getRight() == rightMost) {
                parentOfRightMost.setRight(rightMost.getLeft());
            } else {
                // This happens if the rightMost was the direct left child
                parentOfRightMost.setLeft(rightMost.getLeft());
            }
            if (rightMost.getLeft() != null) {
                rightMost.getLeft().setParent(parentOfRightMost);
            }

            // Start balancing from the rightMost's original parent
            changeHeight(parentOfRightMost);
            balanceTree(parentOfRightMost);
        }
        return deletedNode;
    }
    /**
     * Initializes a new subtree for a main tree node.
     * The subtree sorts by the *opposite* attribute of the main tree.
     * @param mainNode The main tree node that will own this subtree.
     */
    private void initializeSubTree(Node mainNode) {
        BattleTree subTree;
        if (this.treeType.equals("health")) {
            // It is not necessary in fact, placed for security issues since order is attack-health-insertion queue.
            subTree = new BattleTree("attack", true); // Main tree sorts by health, subtree by attack
        } else { // "attack"
            subTree = new BattleTree("health", true); // Main tree sorts by attack, subtree by health
        }
        mainNode.setSubTree(subTree);

        // Adding the node's own card to its new subtree
        Node subNode = new Node();
        subNode.setCard(mainNode.getCard());
        subTree.add(subNode);
    }

    /**
     * Initializes a new insertion queue for a subtree node.
     * @param mainNode The subtree node that will own this queue.
     */
    private void initializeQueue(Node mainNode) {
        Queue insertionQueue;
        insertionQueue = new Queue();
        mainNode.setInsertionQueue(insertionQueue);

        // Adding the node's own card to its new queue
        QueueNode queueNode = new QueueNode();
        queueNode.setCard(mainNode.getCard());
        insertionQueue.enqueue(queueNode);
    }
    /**
     * Recalculates the height of a node and all its ancestors up to the root.
     * Height of a leaf is 0. Height of null is -1.
     * @param node The node to start recalculating height from.
     */
    public void changeHeight(Node node){
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
    public int getHeightDifference(Node node){
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
    public void balanceTree(Node node){
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
     * @param node The node to rotate.
     */
    public void rotateRight(Node node){
        // node = alpha (unbalanced node)
        Node leftChild = node.getLeft(); // Let's call it beta to simplify the naming
        Node leftRightChild = node.getLeft().getRight();// leftRightChild (subtree to move)

        Node parent = node.getParent(); // alpha's original parent

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
     * @param node The node to rotate.
     */
    public void rotateLeft(Node node){
        // node = alpha (unbalanced node)
        Node rightChild = node.getRight(); // Let's call it beta to simplify the naming
        Node rightLeftChild = node.getRight().getLeft();// rightLeftChild (subtree to move)

        Node parent = node.getParent();// alpha's original parent

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