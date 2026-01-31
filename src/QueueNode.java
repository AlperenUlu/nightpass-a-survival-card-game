/**
 * Represents a single node in a Queue implemented from personally generated linked list.
 * Each node holds a Card object and a reference to the next node.
 */
public class QueueNode {

    // Reference to the next node in the queue.
    QueueNode next;

    // The Card object stored in this node.
    Card card;

    // Default constructor.
    QueueNode(){}

    /**
     * Gets the card stored in this node.
     * @return The Card object.
     */
    public Card getCard() {
        return card;
    }

    /**
     * Gets the next node in the queue.
     * @return The next QueueNode, or null if this is the tail.
     */
    public QueueNode getNext() {
        return next;
    }

    /**
     * Sets the card for this node.
     * @param card The Card to be stored.
     */
    public void setCard(Card card) {
        this.card = card;
    }

    /**
     * Sets the reference to the next node.
     * @param next The next QueueNode in the queue.
     */
    public void setNext(QueueNode next) {
        this.next = next;
    }
}