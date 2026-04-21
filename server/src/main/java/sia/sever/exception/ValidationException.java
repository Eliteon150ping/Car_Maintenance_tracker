package sia.sever.exception;

import java.util.List;

public class ValidationException extends RuntimeException {

    // Fields
    private final List<String> errors;

    // Constructor
    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    // Getters
    public List<String> getErrors(){
        return errors;
    }
}
