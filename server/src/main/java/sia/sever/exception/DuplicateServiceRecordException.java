package sia.sever.exception;

public class DuplicateServiceRecordException extends RuntimeException {
    public DuplicateServiceRecordException(String message) {
        super(message);
    }
}
