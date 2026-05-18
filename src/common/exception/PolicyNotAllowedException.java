package common.exception;

public class PolicyNotAllowedException extends BusinessException {

    public PolicyNotAllowedException(String message) {
        super(message);
    }

    public PolicyNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}
