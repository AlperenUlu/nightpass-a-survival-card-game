/**
 * Implements an AVL tree to store discarded cards waiting for revival.
 * This tree sorts nodes based on the card's missingHealth attribute.
 * It handles duplicate missingHealth values by storing them in a first in first out queue attached to the node.
 */
public class RevivalTree {
    private Node root;

    /**
     * Constructor for RevivalTree.
     * Initializes an empty tree.
     */
    RevivalTree(){
        this.root = null;
    }

    /**
     * Gets the root node of the tree.
     * @return The root node.
     */
    public Node getRoot() {
        return root;
    }

    /**
     * Compares two nodes based on their card's missingHealth.
     * @param firstNode The first node.
     * @param secondNode The second node.
     * @return -1 if missing health of first < second, 1 if missing health first > second, 0 if equal.
     */
    public int compare(Node firstNode, Node secondNode){
        if (firstNode.getCard().getMissingHealth() < secondNode.getCard().getMissingHealth()){
            return -1;
        }
        else if (firstNode.getCard().getMissingHealth() > secondNode.getCard().getMissingHealth()){
            return 1;
        }
        else {
            return 0; // Missing health is equal
        }
    }

    /**
     * Adds a new node to the tree, maintaining AVL balance.
     * If a node with the same 'missingHealth' exists, the new card is
     * added to that node's insertion queue.
     * @param addedNode The node to add.
     */
    public void add(Node addedNode) {
        // Case 1: Tree is empty
        if (root == null) {
            root = addedNode;
            initializeQueue(root); // Every node in this tree gets a queue containing itself
            return;
        }

        Node parent = null;
        Node current = root;
        Node nodeWithSameValue = null;

        // Standard traversal to find the insertion point
        while (current != null) {
            parent = current;
            int comparedResult = compare(addedNode, current);

            if (comparedResult < 0) {
                current = current.getLeft();
            } else if (comparedResult > 0) {
                current = current.getRight();
            } else {
                // Duplicate missingHealth value found
                nodeWithSameValue = current;
                break;
            }
        }

        // Duplicate found. Adding to that node's queue.
        if (nodeWithSameValue != null) {
            QueueNode queueNode = new QueueNode();
            queueNode.setCard(addedNode.getCard());
            nodeWithSameValue.getInsertionQueue().enqueue(queueNode);

        } else {
            // No duplicate. Adding as a new leaf.
            if (compare(addedNode, parent) < 0) {
                parent.setLeft(addedNode);
            } else {
                parent.setRight(addedNode);
            }
            addedNode.setParent(parent);

            // Updating heights and rebalance
            changeHeight(parent);
            balanceTree(parent);
            initializeQueue(addedNode); // Initializing the queue for the new node
        }
    }

    /**
     * Initializes a new insertion queue for a node and adds the node's own card to it.
     * @param mainNode The node that will own this queue.
     */
    private void initializeQueue(Node mainNode) {
        Queue insertionQueue;
        insertionQueue = new Queue();
        mainNode.setInsertionQueue(insertionQueue);

        // Adding the node's own card as the first item in its queue
        QueueNode queueNode = new QueueNode();
        queueNode.setCard(mainNode.getCard());
        insertionQueue.enqueue(queueNode);
    }

    /**
     * Removes a node from the tree based on missingHealth, maintaining AVL balance.
     * If the found node has multiple cards in its queue, it removes one from
     * the queue and updates the node's card with its successor in the queue.
     * If the queue becomes empty, the node itself is removed from the tree.
     * @param discardNode A node containing the missingHealth data to find.
     * @return The Node that was actually removed .
     */
    public Node discard(Node discardNode){
        Node parent = null;
        Node current = root;

        // Finding the node in the tree with the matching missingHealth
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
            else {
                break; // Found the node
            }
        }

        // Node not found, it's not the case in this code,
        // because we check if there is a card to battle before discarding it
        if (current == null) {
            return null;
        }

        // Handling the deletion from the node's queue first
        Queue insertionQueue = current.getInsertionQueue();

        if (insertionQueue != null) {
            // Removing the card from queue not from the tree
            QueueNode discardedQueueNode = insertionQueue.dequeue();
            Node returnedNode = new Node();
            returnedNode.setCard(discardedQueueNode.getCard());

            if (insertionQueue.getHead() == null) {
                // The queue is now empty. This means we must delete the main current node.
                // Fall through to the standard deletion logic below.
            }
            else {
                // The queue still has items. Update the main node's card
                // to match the new head of the queue. The tree structure doesn't change.
                current.setCard(insertionQueue.getHead().getCard());
                return returnedNode;
            }
        }

        // This code is reached if the node's queue became empty.

        Node deletedNode = new Node(); // Storing the card we are about to delete
        deletedNode.setCard(current.getCard());

        // Standard deletion logic
        if(current.getLeft() == null){
            // Node has not a left child
            if (parent == null){
                root = current.getRight();
                if(root !=null){
                    root.setParent(null);
                }
            }
            else{
                if(compare(discardNode, parent) < 0) {
                    parent.setLeft(current.getRight());
                }
                else{
                    parent.setRight(current.getRight());
                }
                if(current.getRight() != null){
                    current.getRight().setParent(parent);
                }
                // Start balancing from the parent,
                // because we quit while if current is null
                changeHeight(parent);
                balanceTree(parent);
            }
        }
        else{
            // Node has a left child
            // Finding the in-order rightmost node of the left subtree
            Node parentOfRightMost = current;
            Node rightMost = current.getLeft();
            while(rightMost.getRight() != null){
                parentOfRightMost = rightMost;
                rightMost = rightMost.getRight();
            }

            // Replacing the current node's data with the rightmost's data
            // This also copies the rightmost's queue
            current.setCard(rightMost.getCard());
            current.setInsertionQueue(rightMost.getInsertionQueue());

            // Unlinking the rightmost's queue to avoid dual references
            rightMost.setInsertionQueue(null);

            // Removing the rightmost from its original location
            if(parentOfRightMost.getRight() == rightMost){
                parentOfRightMost.setRight(rightMost.getLeft());
            }
            else {
                // This happens if the rightmost was the direct left child
                parentOfRightMost.setLeft(rightMost.getLeft());
            }
            if(rightMost.getLeft()!= null){
                rightMost.getLeft().setParent(parentOfRightMost);
            }

            // Starting to balance from the predecessor's original parent
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
     * @param node The node to rotate (the 'grandparent' X).
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
     * @param node The node to rotate (the 'grandparent' X).
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

