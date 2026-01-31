/**
 * Tracks and manages scores for two opposing sides: Stranger and Survivor.
 */
public class ScoreTracker {
    // Stores the current score for the Stranger.
    private int strangerScore = 0;

    // Stores the current score for the Survivor.
    private int survivorScore = 0;

    // Default constructor.
    ScoreTracker(){

    }

    /**
     * Gets the current score of the Stranger.
     * @return The Stranger's score.
     */
    public int getStrangerScore() {
        return strangerScore;
    }

    /**
     * Gets the current score of the Survivor.
     * @return The Survivor's score.
     */
    public int getSurvivorScore() {
        return survivorScore;
    }

    /**
     * Sets the Stranger's score.
     * @param strangerScore The new score for the Stranger.
     */
    public void setStrangerScore(int strangerScore) {
        this.strangerScore = strangerScore;
    }

    /**
     * Sets the Survivor's score.
     * @param survivorScore The new score for the Survivor.
     */
    public void setSurvivorScore(int survivorScore) {
        this.survivorScore = survivorScore;
    }
}
