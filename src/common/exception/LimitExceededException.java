package common.exception;

public class LimitExceededException extends BusinessException {

    public LimitExceededException(String message) {
        super(message);
    }

    public LimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
