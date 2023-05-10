package bgpersonnel.budget.exeception;

import bgpersonnel.budget.authentification.security.exeception.TokenRefreshException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;


@RestControllerAdvice
public class ErrorExceptionControllerAdvice {

    @ExceptionHandler(value = TokenRefreshException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorMessageResponse handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
        return new ErrorMessageResponse(ex.getMessage());
    }

    @ExceptionHandler(value = AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessageResponse handleAlreadyExistsException(AlreadyExistsException ex, WebRequest request) {
        return new ErrorMessageResponse(ex.getMessage());
    }

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessageResponse handleNotFoundException(NotFoundException ex, WebRequest request) {
        return new ErrorMessageResponse(ex.getMessage());
    }
}