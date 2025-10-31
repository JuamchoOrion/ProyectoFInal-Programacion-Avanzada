package co.edu.uniquindio.stayNow.exceptions;

public class ExpiredResetCodeException extends RuntimeException {
    public ExpiredResetCodeException(String message) {
        super(message);
    }
}
