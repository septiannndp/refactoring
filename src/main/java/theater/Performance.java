package theater;

/**
 * Class representing a performance of a play..
 */
public class Performance {

    private final String playID;
    private final int audience;

    /**
     * Constructs a new {@code Performance}.
     *
     * @param playID   the identifier of the play
     * @param audience the size of the audience
     */
    public Performance(String playID, int audience) {
        this.playID = playID;
        this.audience = audience;
    }

    /**
     * Returns the identifier of the play.
     *
     * @return the play identifier
     */
    public String getPlayID() {
        return playID;
    }

    /**
     * Returns the audience size for this performance.
     *
     * @return the audience size
     */
    public int getAudience() {
        return audience;
    }
}
