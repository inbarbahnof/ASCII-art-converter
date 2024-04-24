package Exceptions;

/**
 * exception to Empty Charset
 * @author Daniel, Inbar
 */
public class EmptyCharsetException extends Exception {
    /**
     * constructor
     * @param message
     */
    public EmptyCharsetException(String message) {
        super(message);
    }
}
