/**
 * Implements a simple First-In, First-Out Queue structure.
 * This class manages a queue of QueueNode objects, tracking the head and tail.
 */
public class Queue {
    private QueueNode head; // The head of the queue, where items are dequeued
    private QueueNode tail; // The tail of the queue, where items are enqueued

    /**
     * Default constructor for the Queue.
     * Initializes an empty queue.
     */
    Queue(){
    }

    /**
     * Adds a new node to the end (tail) of the queue.
     * @param node The QueueNode to be added.
     */
    public void enqueue(QueueNode node){
        // If the queue is empty, the new node is both head and tail
        if(head == null){
            head = node;
            tail = node;
        }
        else{
            tail.setNext(node);
            tail = node;
        }
    }

    /**
     * Removes and returns the node from the front (head) of the queue.
     * @return The QueueNode at the front of the queue, or null if the queue is empty.
     */
    public QueueNode dequeue(){
        // Checking if the queue is empty
        if (head == null) {
            return null;
        }

        QueueNode current = head;
        head = head.getNext();

        // If the queue is now empty, update the tail as well
        if (head == null) {
            tail = null;
        }

        current.setNext(null); // Cleaning up the dequeued node
        return current;
    }

    /**
     * Gets the node at the front (head) of the queue without removing it.
     * @return The QueueNode at the head, or null if the queue is empty.
     */
    public QueueNode getHead() {
        return head;
    }
}