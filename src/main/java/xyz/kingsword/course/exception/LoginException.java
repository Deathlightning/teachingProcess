package xyz.kingsword.course.exception;

public class LoginException extends RuntimeException {
    public LoginException() {

    }

    public LoginException(String message) {
        super(message);
    }
}
