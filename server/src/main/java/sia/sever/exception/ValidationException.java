package sia.sever.exception;

import java.util.Map;

public class ValidationException extends RuntimeException {

    // Fields
    private final Map<String, String> errors;

    // Constructor
    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

    // Getters
    public Map<String, String> getErrors(){
        return errors;
    }
}
