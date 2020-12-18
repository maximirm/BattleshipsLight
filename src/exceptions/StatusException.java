package exceptions;

public class StatusException extends Exception {

    /**
     * Used if methods are called in wrong status
     */

    public StatusException(String message) {

        super(message);
    }

}
