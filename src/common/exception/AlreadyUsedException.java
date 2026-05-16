package common.exception;

public class AlreadyUsedException extends BusinessException {

    public AlreadyUsedException(String message) {
        super(message);
    }

    public AlreadyUsedException(String message, Throwable cause) {
        super(message, cause);
    }
}
