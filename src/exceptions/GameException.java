package exceptions;

public class GameException extends Exception {

    /**
     * Used if methods are called in wrong status
     */

    public GameException(String message) {

        super(message);
    }

    public GameException(String message, Throwable t) {

        super(message, t);

    }

}
