package sia.sever.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice // Better than @ControllerAdvice for rest apis, returning json instead of for returning html
public class GlobalExceptionHandler {

    /* Global exception handler acts as the central hub for error handling(Think of it as a manager)
       It does not create the problem or do the work, it just handles everything professionally if an error
       occurs.                                                                                                */

    // Handle Resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException missingResource, HttpServletRequest request) {
        ErrorResponse resourceNotFound = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), missingResource.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(resourceNotFound, HttpStatus.NOT_FOUND);
    }

    // Handle Invalid input
    @ExceptionHandler(InvalidClassException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInput(InvalidClassException invalidInput, HttpServletRequest request) {
        ErrorResponse invalidInputEntered = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), invalidInput.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(invalidInputEntered, HttpStatus.BAD_REQUEST);
    }

    // Handle Invalid mileage
    @ExceptionHandler(InvalidMileageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMileage(InvalidMileageException invalidMileage, HttpServletRequest request) {
        ErrorResponse invalidMileageEntered = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), invalidMileage.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(invalidMileageEntered, HttpStatus.BAD_REQUEST);
    }

    // Handle Invalid date
    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDate(InvalidDateException invalidDate, HttpServletRequest request) {
        ErrorResponse invalidDateEntered = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), invalidDate.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(invalidDateEntered, HttpStatus.BAD_REQUEST);
    }

    // Handle Validation error input
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException validationError, HttpServletRequest request) {
        List<String> errorList = validationError.getErrors();
        ErrorResponse validationInputError = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), validationError.getMessage(), request.getRequestURI(), errorList);
        return new ResponseEntity<>(validationInputError, HttpStatus.BAD_REQUEST);
    }

    // Handle JWT token Expired
    @ExceptionHandler(JwtExpiredException.class)
    public ResponseEntity<ErrorResponse> handleJwtTokenExpired(JwtExpiredException jwtExpiredException, HttpServletRequest request) {
        ErrorResponse jwtTokenExpired = new ErrorResponse(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), jwtExpiredException.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(jwtTokenExpired, HttpStatus.UNAUTHORIZED);
    }

    // Handle JWT token invalid
    @ExceptionHandler(JwtInvalidException.class)
    public ResponseEntity<ErrorResponse> handleJwtTokenInvalid(JwtInvalidException jwtInvalidException, HttpServletRequest request) {
        ErrorResponse jwtTokenInvalid = new ErrorResponse(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), jwtInvalidException.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(jwtTokenInvalid, HttpStatus.UNAUTHORIZED);
    }

    // Handle Unauthorized User
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedUser(UnauthorizedException unauthorizedException, HttpServletRequest request) {
        ErrorResponse unauthorizedUser = new ErrorResponse(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), unauthorizedException.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(unauthorizedUser, HttpStatus.UNAUTHORIZED);
    }

    // Handle invalid enum values
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Invalid enum value: " + ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Handle Method Arguments not valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errorList = new ArrayList<>();
        ex.getBindingResult();
        for (FieldError fieldError : ex.getFieldErrors()) {
            errorList.add(fieldError.getDefaultMessage());
        }
        ErrorResponse exInputError = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),"Validation failed", request.getRequestURI(), errorList);
        return new ResponseEntity<>(exInputError, HttpStatus.BAD_REQUEST);
    }

    // Default fallback(Handle unexpected errors)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception defaultFallback, HttpServletRequest request) {
        ErrorResponse lastResortError = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), defaultFallback.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(lastResortError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
