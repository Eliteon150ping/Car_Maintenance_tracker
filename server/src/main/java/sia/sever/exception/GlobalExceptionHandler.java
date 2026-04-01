package sia.sever.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.io.InvalidClassException;
import java.time.LocalDateTime;

@RestControllerAdvice // Better than @ControllerAdvice for rest apis, returning json instead of for returning html
public class GlobalExceptionHandler {

    /* Global exception handler acts as the central hub for error handling(Think of it as a manager)
       It does not create the problem or do the work, it just handles everything professionally if an error
       occurs.                                                                                                */

    // Handle Resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException missingResource, HttpServletRequest request){
        ErrorResponse resourceNotFound = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), missingResource.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(resourceNotFound, HttpStatus.NOT_FOUND);
    }

    // Handle Invalid input
    @ExceptionHandler(InvalidClassException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInput(InvalidClassException invalidInput, HttpServletRequest request){
        ErrorResponse invalidInputEntered = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), invalidInput.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(invalidInputEntered, HttpStatus.BAD_REQUEST);
    }

    // Default fallback(Handle unexpected errors)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception defaultFallback, HttpServletRequest request){
        ErrorResponse lastResortError = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), defaultFallback.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(lastResortError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
