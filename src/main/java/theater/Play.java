package theater;

/**
 * Class representing a theatrical play.
 */
public class Play {

    private final String name;
    private final String type;

    /**
     * Constructs a new {@code Play}.
     *
     * @param name the name of the play
     * @param type the type of the play (for example, tragedy or comedy)
     */
    public Play(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Returns the name of the play.
     *
     * @return the play name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of the play.
     *
     * @return the play type
     */
    public String getType() {
        return type;
    }
}