package sia.sever.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ErrorResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final List<String> errors;

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.errors = new ArrayList<>();
    }

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path, List<String> errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.errors = errors;
    }

    // Getters
    public LocalDateTime getTimestamp(){
        return timestamp;
    }

    public int getStatus(){
        return status;
    }

    public String getError(){
        return error;
    }

    public String getMessage(){
        return message;
    }

    public String getPath(){
        return path;
    }

    public List<String> getErrors(){
        return errors;
    }

}
