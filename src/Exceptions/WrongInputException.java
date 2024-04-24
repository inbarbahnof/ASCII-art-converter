package Exceptions;

/**
 * exception to Wrong Input
 * @author Daniel, Inbar
 */
public class WrongInputException extends Exception {
    /**
     * constructor
     * @param message
     */
    public WrongInputException(String message) {
        super(message);
    }
}