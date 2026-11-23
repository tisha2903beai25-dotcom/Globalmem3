package exceptions;

public class SafetyViolationException extends Exception {

    public SafetyViolationException(String message) {
        super(message);
    }
}